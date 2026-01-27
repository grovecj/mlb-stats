-- Sync Jobs table for tracking data synchronization operations
CREATE TABLE sync_jobs (
    id BIGSERIAL PRIMARY KEY,
    job_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    season INTEGER,
    triggered_by VARCHAR(20) NOT NULL,
    started_by_user_id BIGINT REFERENCES app_users(id),
    total_items INTEGER,
    processed_items INTEGER DEFAULT 0,
    current_step VARCHAR(100),
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    records_created INTEGER DEFAULT 0,
    records_updated INTEGER DEFAULT 0,
    error_count INTEGER DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT valid_job_type CHECK (job_type IN ('FULL_SYNC', 'TEAMS', 'ROSTERS', 'GAMES', 'STATS', 'STANDINGS', 'BOX_SCORES')),
    CONSTRAINT valid_status CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'CANCELLED')),
    CONSTRAINT valid_triggered_by CHECK (triggered_by IN ('MANUAL', 'SCHEDULED'))
);

CREATE INDEX idx_sync_jobs_type_status ON sync_jobs(job_type, status);
CREATE INDEX idx_sync_jobs_started_at ON sync_jobs(started_at DESC);
CREATE INDEX idx_sync_jobs_job_type_completed ON sync_jobs(job_type, completed_at DESC);
