-- Add SABERMETRICS to the valid_job_type check constraint

ALTER TABLE sync_jobs DROP CONSTRAINT valid_job_type;

ALTER TABLE sync_jobs ADD CONSTRAINT valid_job_type
    CHECK (job_type IN ('FULL_SYNC', 'TEAMS', 'ROSTERS', 'GAMES', 'STATS', 'STANDINGS', 'BOX_SCORES', 'LINESCORES', 'SABERMETRICS'));
