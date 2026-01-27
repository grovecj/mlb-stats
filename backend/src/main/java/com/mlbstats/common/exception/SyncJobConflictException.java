package com.mlbstats.common.exception;

import com.mlbstats.domain.sync.SyncJobType;

public class SyncJobConflictException extends RuntimeException {

    private final Long existingJobId;
    private final SyncJobType jobType;

    public SyncJobConflictException(String message) {
        super(message);
        this.existingJobId = null;
        this.jobType = null;
    }

    public SyncJobConflictException(SyncJobType jobType, Long existingJobId) {
        super(String.format("A %s sync job is already running (job ID: %d)", jobType.getDisplayName(), existingJobId));
        this.existingJobId = existingJobId;
        this.jobType = jobType;
    }

    public Long getExistingJobId() {
        return existingJobId;
    }

    public SyncJobType getJobType() {
        return jobType;
    }
}
