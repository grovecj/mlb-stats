interface StatCardProps {
  value: string | number;
  label: string;
  tooltip?: string;
}

function StatCard({ value, label, tooltip }: StatCardProps) {
  return (
    <div className="stat-card" title={tooltip}>
      <div className="stat-value">{value}</div>
      <div className="stat-label">
        {label}
        {tooltip && <span className="stat-info-icon">?</span>}
      </div>
    </div>
  );
}

export default StatCard;
