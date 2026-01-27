package com.mlbstats.api.dto;

import com.mlbstats.domain.sync.SyncJob;
import com.mlbstats.domain.sync.SyncJobStatus;
import com.mlbstats.domain.sync.SyncJobType;
import com.mlbstats.domain.sync.TriggerType;

import java.time.Duration;
import java.time.LocalDateTime;

public record SyncJobDto(
        Long id,
        SyncJobType jobType,
        String jobTypeDisplay,
        SyncJobStatus status,
        Integer season,
        TriggerType triggeredBy,
        String startedByUserEmail,
        Integer totalItems,
        Integer processedItems,
        Integer progressPercentage,
        String currentStep,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        Long durationSeconds,
        Integer recordsCreated,
        Integer recordsUpdated,
        Integer errorCount,
        String errorMessage,
        LocalDateTime createdAt
) {
    public static SyncJobDto fromEntity(SyncJob job) {
        Long durationSeconds = null;
        if (job.getStartedAt() != null) {
            LocalDateTime endTime = job.getCompletedAt() != null ? job.getCompletedAt() : LocalDateTime.now();
            durationSeconds = Duration.between(job.getStartedAt(), endTime).getSeconds();
        }

        return new SyncJobDto(
                job.getId(),
                job.getJobType(),
                job.getJobType().getDisplayName(),
                job.getStatus(),
                job.getSeason(),
                job.getTriggeredBy(),
                job.getStartedByUser() != null ? job.getStartedByUser().getEmail() : null,
                job.getTotalItems(),
                job.getProcessedItems(),
                job.getProgressPercentage(),
                job.getCurrentStep(),
                job.getStartedAt(),
                job.getCompletedAt(),
                durationSeconds,
                job.getRecordsCreated(),
                job.getRecordsUpdated(),
                job.getErrorCount(),
                job.getErrorMessage(),
                job.getCreatedAt()
        );
    }
}
