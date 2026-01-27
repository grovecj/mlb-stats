import { TeamAggregateStats as TeamAggregateStatsType } from '../../types/team';

interface TeamAggregateStatsProps {
  stats: TeamAggregateStatsType | null;
  loading: boolean;
}

function formatAvg(value: number | null): string {
  if (value === null || value === undefined) return '.---';
  return value.toFixed(3).replace(/^0/, '');
}

function formatRate(value: number | null): string {
  if (value === null || value === undefined) return '-';
  return value.toFixed(2);
}

function TeamAggregateStats({ stats, loading }: TeamAggregateStatsProps) {
  if (loading) {
    return <div className="loading">Loading team stats...</div>;
  }

  if (!stats) {
    return null;
  }

  const { batting, pitching } = stats;

  return (
    <div style={{ display: 'grid', gap: '16px', marginBottom: '24px' }}>
      {batting && (
        <div className="card">
          <h3 className="card-title">Team Batting</h3>
          <div className="detail-stats-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))' }}>
            <StatItem label="AVG" value={formatAvg(batting.battingAvg)} />
            <StatItem label="OBP" value={formatAvg(batting.obp)} />
            <StatItem label="SLG" value={formatAvg(batting.slg)} />
            <StatItem label="OPS" value={formatAvg(batting.ops)} />
            <StatItem label="Runs" value={batting.runs.toLocaleString()} />
            <StatItem label="Hits" value={batting.hits.toLocaleString()} />
            <StatItem label="HR" value={batting.homeRuns.toLocaleString()} />
            <StatItem label="RBI" value={batting.rbi.toLocaleString()} />
            <StatItem label="SB" value={batting.stolenBases.toLocaleString()} />
            <StatItem label="BB" value={batting.walks.toLocaleString()} />
            <StatItem label="SO" value={batting.strikeouts.toLocaleString()} />
            <StatItem label="2B" value={batting.doubles.toLocaleString()} />
          </div>
        </div>
      )}

      {pitching && (
        <div className="card">
          <h3 className="card-title">Team Pitching</h3>
          <div className="detail-stats-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))' }}>
            <StatItem label="ERA" value={formatRate(pitching.era)} />
            <StatItem label="WHIP" value={formatRate(pitching.whip)} />
            <StatItem label="K/9" value={formatRate(pitching.kPer9)} />
            <StatItem label="Record" value={`${pitching.wins}-${pitching.losses}`} />
            <StatItem label="Saves" value={pitching.saves.toLocaleString()} />
            <StatItem label="IP" value={pitching.inningsPitched.toLocaleString()} />
            <StatItem label="SO" value={pitching.strikeouts.toLocaleString()} />
            <StatItem label="BB" value={pitching.walks.toLocaleString()} />
            <StatItem label="H" value={pitching.hitsAllowed.toLocaleString()} />
            <StatItem label="ER" value={pitching.earnedRuns.toLocaleString()} />
            <StatItem label="HR" value={pitching.homeRunsAllowed.toLocaleString()} />
            <StatItem label="QS" value={pitching.qualityStarts.toLocaleString()} />
          </div>
        </div>
      )}
    </div>
  );
}

function StatItem({ label, value }: { label: string; value: string }) {
  return (
    <div className="detail-stat-item">
      <div className="detail-stat-label">{label}</div>
      <div className="detail-stat-value">{value}</div>
    </div>
  );
}

export default TeamAggregateStats;
