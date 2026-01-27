package com.mlbstats.domain.sync;

import com.mlbstats.domain.user.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sync_jobs")
@Getter
@Setter
@NoArgsConstructor
public class SyncJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private SyncJobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncJobStatus status;

    private Integer season;

    @Enumerated(EnumType.STRING)
    @Column(name = "triggered_by", nullable = false)
    private TriggerType triggeredBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "started_by_user_id")
    private AppUser startedByUser;

    @Column(name = "total_items")
    private Integer totalItems;

    @Column(name = "processed_items")
    private Integer processedItems = 0;

    @Column(name = "current_step")
    private String currentStep;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "records_created")
    private Integer recordsCreated = 0;

    @Column(name = "records_updated")
    private Integer recordsUpdated = 0;

    @Column(name = "error_count")
    private Integer errorCount = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void start() {
        this.status = SyncJobStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    public void complete(int created, int updated, int errors) {
        this.status = SyncJobStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.recordsCreated = created;
        this.recordsUpdated = updated;
        this.errorCount = errors;
    }

    public void fail(String errorMessage) {
        this.status = SyncJobStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }

    public void cancel() {
        this.status = SyncJobStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }

    public void updateProgress(int processed, Integer total, String step) {
        this.processedItems = processed;
        if (total != null) {
            this.totalItems = total;
        }
        this.currentStep = step;
    }

    public int getProgressPercentage() {
        if (totalItems == null || totalItems == 0) {
            return 0;
        }
        return Math.min(100, (processedItems * 100) / totalItems);
    }

    public boolean isRunning() {
        return status == SyncJobStatus.RUNNING;
    }

    public boolean isComplete() {
        return status == SyncJobStatus.COMPLETED ||
               status == SyncJobStatus.FAILED ||
               status == SyncJobStatus.CANCELLED;
    }
}
