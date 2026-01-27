import { SyncJob } from '../../services/api';
import './sync.css';

interface SyncHistoryTableProps {
  jobs: SyncJob[];
}

function SyncHistoryTable({ jobs }: SyncHistoryTableProps) {
  const formatDate = (dateStr: string | null) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleString();
  };

  const formatDuration = (seconds: number | null) => {
    if (!seconds) return '-';
    if (seconds < 60) return `${seconds}s`;
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    if (minutes < 60) return `${minutes}m ${remainingSeconds}s`;
    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;
    return `${hours}h ${remainingMinutes}m`;
  };

  if (jobs.length === 0) {
    return <p className="sync-history-empty">No sync history available.</p>;
  }

  return (
    <div className="sync-history-wrapper">
      <table className="sync-history-table">
        <thead>
          <tr>
            <th>Type</th>
            <th>Status</th>
            <th>Season</th>
            <th>Started</th>
            <th>Duration</th>
            <th>Records</th>
            <th>Trigger</th>
          </tr>
        </thead>
        <tbody>
          {jobs.map((job) => (
            <tr key={job.id} className={job.status.toLowerCase()}>
              <td>{job.jobTypeDisplay}</td>
              <td>
                <span className={`sync-status-badge small ${job.status.toLowerCase()}`}>
                  {job.status}
                </span>
              </td>
              <td>{job.season || '-'}</td>
              <td>{formatDate(job.startedAt)}</td>
              <td>{formatDuration(job.durationSeconds)}</td>
              <td>
                {job.status === 'COMPLETED' ? job.recordsCreated.toLocaleString() : '-'}
                {job.errorCount > 0 && (
                  <span className="sync-error-count">({job.errorCount} errors)</span>
                )}
              </td>
              <td className="sync-trigger-cell">
                {job.triggeredBy === 'MANUAL' ? 'Manual' : 'Scheduled'}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default SyncHistoryTable;
