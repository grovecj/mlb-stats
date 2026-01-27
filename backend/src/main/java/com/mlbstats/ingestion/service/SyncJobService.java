package com.mlbstats.ingestion.service;

import com.mlbstats.common.exception.ResourceNotFoundException;
import com.mlbstats.common.exception.SyncJobConflictException;
import com.mlbstats.domain.sync.*;
import com.mlbstats.domain.user.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncJobService {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30 minutes
    private static final List<SyncJobStatus> ACTIVE_STATUSES = List.of(SyncJobStatus.PENDING, SyncJobStatus.RUNNING);

    private final SyncJobRepository syncJobRepository;
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> jobEmitters = new ConcurrentHashMap<>();

    @Transactional
    public SyncJob createJob(SyncJobType jobType, Integer season, TriggerType trigger, AppUser user) {
        // Check for running full sync (blocks all other syncs)
        syncJobRepository.findRunningFullSync().ifPresent(fullSync -> {
            throw new SyncJobConflictException(SyncJobType.FULL_SYNC, fullSync.getId());
        });

        // Check for same type already running
        if (jobType != SyncJobType.FULL_SYNC) {
            syncJobRepository.findFirstByJobTypeAndStatusIn(jobType, ACTIVE_STATUSES)
                    .ifPresent(existing -> {
                        throw new SyncJobConflictException(jobType, existing.getId());
                    });
        }

        // If starting a full sync, check no other syncs are running
        if (jobType == SyncJobType.FULL_SYNC) {
            List<SyncJob> activeJobs = syncJobRepository.findActiveJobs();
            if (!activeJobs.isEmpty()) {
                throw new SyncJobConflictException("Cannot start full sync while other syncs are running");
            }
        }

        SyncJob job = new SyncJob();
        job.setJobType(jobType);
        job.setStatus(SyncJobStatus.PENDING);
        job.setSeason(season);
        job.setTriggeredBy(trigger);
        job.setStartedByUser(user);

        return syncJobRepository.save(job);
    }

    @Transactional
    public SyncJob startJob(Long jobId) {
        SyncJob job = getJob(jobId);
        job.start();
        SyncJob savedJob = syncJobRepository.save(job);
        broadcastProgress(savedJob);
        return savedJob;
    }

    @Transactional
    public SyncJob updateProgress(Long jobId, int processed, Integer total, String currentStep) {
        SyncJob job = getJob(jobId);
        job.updateProgress(processed, total, currentStep);
        SyncJob savedJob = syncJobRepository.save(job);
        broadcastProgress(savedJob);
        return savedJob;
    }

    @Transactional
    public SyncJob incrementProgress(Long jobId, String currentStep) {
        SyncJob job = getJob(jobId);
        int processed = job.getProcessedItems() != null ? job.getProcessedItems() + 1 : 1;
        job.updateProgress(processed, null, currentStep);
        SyncJob savedJob = syncJobRepository.save(job);
        broadcastProgress(savedJob);
        return savedJob;
    }

    @Transactional
    public SyncJob completeJob(Long jobId, int created, int updated, int errors) {
        SyncJob job = getJob(jobId);
        job.complete(created, updated, errors);
        SyncJob savedJob = syncJobRepository.save(job);
        broadcastProgress(savedJob);
        closeEmitters(jobId);
        return savedJob;
    }

    @Transactional
    public SyncJob failJob(Long jobId, String errorMessage) {
        SyncJob job = getJob(jobId);
        job.fail(errorMessage);
        SyncJob savedJob = syncJobRepository.save(job);
        broadcastProgress(savedJob);
        closeEmitters(jobId);
        return savedJob;
    }

    @Transactional
    public SyncJob cancelJob(Long jobId) {
        SyncJob job = getJob(jobId);
        if (!job.isRunning() && job.getStatus() != SyncJobStatus.PENDING) {
            throw new IllegalStateException("Can only cancel pending or running jobs");
        }
        job.cancel();
        SyncJob savedJob = syncJobRepository.save(job);
        broadcastProgress(savedJob);
        closeEmitters(jobId);
        return savedJob;
    }

    public SyncJob getJob(Long jobId) {
        return syncJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("SyncJob", jobId));
    }

    public List<SyncJob> getRecentJobs(int limit) {
        return syncJobRepository.findTop20ByOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    public List<SyncJob> getActiveJobs() {
        return syncJobRepository.findActiveJobs();
    }

    public Optional<SyncJob> getRunningJob(SyncJobType jobType) {
        return syncJobRepository.findFirstByJobTypeAndStatusIn(jobType, List.of(SyncJobStatus.RUNNING));
    }

    public SseEmitter subscribeToJob(Long jobId) {
        // Verify job exists
        SyncJob job = getJob(jobId);

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        jobEmitters.computeIfAbsent(jobId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(jobId, emitter));
        emitter.onTimeout(() -> removeEmitter(jobId, emitter));
        emitter.onError(e -> removeEmitter(jobId, emitter));

        // Send initial state
        try {
            emitter.send(SseEmitter.event()
                    .name("progress")
                    .data(buildProgressData(job)));
        } catch (IOException e) {
            log.warn("Failed to send initial SSE event for job {}", jobId, e);
            removeEmitter(jobId, emitter);
        }

        return emitter;
    }

    public Map<SyncJobType, DataFreshness> getDataFreshness() {
        Map<SyncJobType, DataFreshness> freshness = new EnumMap<>(SyncJobType.class);

        for (SyncJobType type : SyncJobType.values()) {
            Optional<SyncJob> lastCompleted = syncJobRepository.findLastCompletedByJobType(type);
            freshness.put(type, lastCompleted.map(this::calculateFreshness)
                    .orElse(new DataFreshness(type, null, FreshnessLevel.CRITICAL, "Never synced")));
        }

        return freshness;
    }

    public DataFreshness getDataFreshnessForType(SyncJobType type) {
        return syncJobRepository.findLastCompletedByJobType(type)
                .map(this::calculateFreshness)
                .orElse(new DataFreshness(type, null, FreshnessLevel.CRITICAL, "Never synced"));
    }

    private DataFreshness calculateFreshness(SyncJob lastJob) {
        LocalDateTime completedAt = lastJob.getCompletedAt();
        if (completedAt == null) {
            return new DataFreshness(lastJob.getJobType(), null, FreshnessLevel.CRITICAL, "Never synced");
        }

        Duration age = Duration.between(completedAt, LocalDateTime.now());
        FreshnessLevel level = getFreshnessLevel(lastJob.getJobType(), age);
        String description = formatAge(age);

        return new DataFreshness(lastJob.getJobType(), completedAt, level, description);
    }

    private FreshnessLevel getFreshnessLevel(SyncJobType type, Duration age) {
        long hours = age.toHours();
        long days = age.toDays();
        long minutes = age.toMinutes();

        return switch (type) {
            case TEAMS -> {
                if (days < 7) yield FreshnessLevel.FRESH;
                if (days < 30) yield FreshnessLevel.STALE;
                yield FreshnessLevel.CRITICAL;
            }
            case ROSTERS -> {
                if (days < 7) yield FreshnessLevel.FRESH;
                if (days < 14) yield FreshnessLevel.STALE;
                yield FreshnessLevel.CRITICAL;
            }
            case GAMES -> {
                if (hours < 1) yield FreshnessLevel.FRESH;
                if (hours < 6) yield FreshnessLevel.STALE;
                yield FreshnessLevel.CRITICAL;
            }
            case STATS -> {
                if (hours < 24) yield FreshnessLevel.FRESH;
                if (days < 3) yield FreshnessLevel.STALE;
                yield FreshnessLevel.CRITICAL;
            }
            case STANDINGS -> {
                if (minutes < 15) yield FreshnessLevel.FRESH;
                if (minutes < 60) yield FreshnessLevel.STALE;
                yield FreshnessLevel.CRITICAL;
            }
            case BOX_SCORES -> {
                if (hours < 6) yield FreshnessLevel.FRESH;
                if (hours < 24) yield FreshnessLevel.STALE;
                yield FreshnessLevel.CRITICAL;
            }
            case FULL_SYNC -> {
                if (hours < 24) yield FreshnessLevel.FRESH;
                if (days < 7) yield FreshnessLevel.STALE;
                yield FreshnessLevel.CRITICAL;
            }
        };
    }

    private String formatAge(Duration age) {
        long minutes = age.toMinutes();
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";

        long hours = age.toHours();
        if (hours < 24) return hours + " hour" + (hours == 1 ? "" : "s") + " ago";

        long days = age.toDays();
        if (days < 30) return days + " day" + (days == 1 ? "" : "s") + " ago";

        long months = days / 30;
        return months + " month" + (months == 1 ? "" : "s") + " ago";
    }

    private void broadcastProgress(SyncJob job) {
        CopyOnWriteArrayList<SseEmitter> emitters = jobEmitters.get(job.getId());
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        Map<String, Object> data = buildProgressData(job);

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("progress")
                        .data(data));
            } catch (IOException e) {
                log.debug("Failed to send SSE event, removing emitter for job {}", job.getId());
                emitters.remove(emitter);
            }
        }
    }

    private Map<String, Object> buildProgressData(SyncJob job) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", job.getId());
        data.put("jobType", job.getJobType());
        data.put("status", job.getStatus());
        data.put("season", job.getSeason());
        data.put("processedItems", job.getProcessedItems());
        data.put("totalItems", job.getTotalItems());
        data.put("progressPercentage", job.getProgressPercentage());
        data.put("currentStep", job.getCurrentStep());
        data.put("startedAt", job.getStartedAt());
        data.put("completedAt", job.getCompletedAt());
        data.put("recordsCreated", job.getRecordsCreated());
        data.put("recordsUpdated", job.getRecordsUpdated());
        data.put("errorCount", job.getErrorCount());
        data.put("errorMessage", job.getErrorMessage());
        return data;
    }

    private void removeEmitter(Long jobId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = jobEmitters.get(jobId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                jobEmitters.remove(jobId);
            }
        }
    }

    private void closeEmitters(Long jobId) {
        CopyOnWriteArrayList<SseEmitter> emitters = jobEmitters.remove(jobId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    log.debug("Error completing emitter for job {}", jobId, e);
                }
            }
        }
    }

    public enum FreshnessLevel {
        FRESH, STALE, CRITICAL
    }

    public record DataFreshness(
            SyncJobType type,
            LocalDateTime lastSyncedAt,
            FreshnessLevel level,
            String description
    ) {}
}
