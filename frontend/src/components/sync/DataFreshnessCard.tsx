import { DataFreshness, SyncJobType } from '../../services/api';
import './sync.css';

interface DataFreshnessCardProps {
  freshness: DataFreshness;
  onSync?: (type: SyncJobType) => void;
  isSyncing?: boolean;
}

function DataFreshnessCard({ freshness, onSync, isSyncing }: DataFreshnessCardProps) {
  const levelClass = freshness.level.toLowerCase();

  return (
    <div className={`freshness-card ${levelClass}`}>
      <div className="freshness-header">
        <span className="freshness-type">{freshness.typeDisplay}</span>
        <span className={`freshness-indicator ${levelClass}`} />
      </div>
      <div className="freshness-description">{freshness.description}</div>
      {onSync && freshness.type !== 'FULL_SYNC' && (
        <button
          className="freshness-sync-btn"
          onClick={() => onSync(freshness.type)}
          disabled={isSyncing}
        >
          {isSyncing ? 'Syncing...' : 'Sync Now'}
        </button>
      )}
    </div>
  );
}

export default DataFreshnessCard;
