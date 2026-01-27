import { useEffect, useState, useRef } from 'react';
import { SyncJob, subscribeSyncJobProgress, cancelSyncJob, getSyncJob } from '../../services/api';
import './sync.css';

interface SyncProgressCardProps {
  job: SyncJob;
  onComplete?: (job: SyncJob) => void;
}

function SyncProgressCard({ job: initialJob, onComplete }: SyncProgressCardProps) {
  const [job, setJob] = useState<SyncJob>(initialJob);
  const [cancelling, setCancelling] = useState(false);
  const completedRef = useRef(false);

  useEffect(() => {
    if (job.status !== 'RUNNING' && job.status !== 'PENDING') {
      return;
    }

    // Reset completed flag when job starts
    completedRef.current = false;

    const handleComplete = (completedJob: SyncJob) => {
      if (!completedRef.current) {
        completedRef.current = true;
        onComplete?.(completedJob);
      }
    };

    const unsubscribe = subscribeSyncJobProgress(
      job.id,
      (updatedJob) => {
        // Merge with existing state to preserve any fields not in the SSE payload
        setJob(prev => {
          const merged = { ...prev, ...updatedJob };
          if (merged.status === 'COMPLETED' || merged.status === 'FAILED' || merged.status === 'CANCELLED') {
            handleComplete(merged);
          }
          return merged;
        });
      },
      () => {
        // Error handler - connection issues
        console.error('SSE connection error for job', job.id);
      },
      async () => {
        // Connection closed - fetch final status as fallback
        try {
          const finalJob = await getSyncJob(job.id);
          setJob(finalJob);
          if (finalJob.status === 'COMPLETED' || finalJob.status === 'FAILED' || finalJob.status === 'CANCELLED') {
            handleComplete(finalJob);
          }
        } catch (error) {
          console.error('Failed to fetch final job status:', error);
        }
      }
    );

    return () => {
      unsubscribe();
    };
  }, [job.id, job.status, onComplete]);

  const handleCancel = async () => {
    setCancelling(true);
    try {
      const cancelled = await cancelSyncJob(job.id);
      setJob(cancelled);
      onComplete?.(cancelled);
    } catch (error) {
      console.error('Failed to cancel job:', error);
    } finally {
      setCancelling(false);
    }
  };

  const formatDuration = (seconds: number | null) => {
    if (!seconds) return '0s';
    if (seconds < 60) return `${seconds}s`;
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}m ${remainingSeconds}s`;
  };

  const isActive = job.status === 'RUNNING' || job.status === 'PENDING';
  const statusClass = job.status.toLowerCase();

  return (
    <div className={`sync-progress-card ${statusClass}`}>
      <div className="sync-progress-header">
        <div className="sync-progress-title">
          <span className="sync-job-type">{job.jobTypeDisplay}</span>
          {job.season && <span className="sync-season">({job.season})</span>}
        </div>
        <span className={`sync-status-badge ${statusClass}`}>{job.status}</span>
      </div>

      {isActive && (
        <>
          <div className="sync-progress-bar-container">
            <div
              className="sync-progress-bar"
              style={{ width: `${job.progressPercentage}%` }}
            />
          </div>
          <div className="sync-progress-details">
            <span className="sync-current-step">{job.currentStep || 'Initializing...'}</span>
            <span className="sync-progress-percentage">{job.progressPercentage}%</span>
          </div>
        </>
      )}

      {job.status === 'COMPLETED' && (
        <div className="sync-results">
          <div className="sync-result-item">
            <span className="sync-result-label">Records:</span>
            <span className="sync-result-value">{job.recordsCreated.toLocaleString()}</span>
          </div>
          {job.durationSeconds !== null && (
            <div className="sync-result-item">
              <span className="sync-result-label">Duration:</span>
              <span className="sync-result-value">{formatDuration(job.durationSeconds)}</span>
            </div>
          )}
        </div>
      )}

      {job.status === 'FAILED' && job.errorMessage && (
        <div className="sync-error-message">{job.errorMessage}</div>
      )}

      <div className="sync-progress-footer">
        <span className="sync-triggered-by">
          {job.triggeredBy === 'MANUAL' ? 'Manual' : 'Scheduled'}
          {job.startedByUserEmail && ` by ${job.startedByUserEmail}`}
        </span>
        {isActive && (
          <button
            className="sync-cancel-btn"
            onClick={handleCancel}
            disabled={cancelling}
          >
            {cancelling ? 'Cancelling...' : 'Cancel'}
          </button>
        )}
      </div>
    </div>
  );
}

export default SyncProgressCard;
