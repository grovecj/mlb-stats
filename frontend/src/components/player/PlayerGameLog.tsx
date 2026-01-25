import { BattingStats } from '../../types/stats';
import DataTable from '../common/DataTable';

interface PlayerGameLogProps {
  stats: BattingStats[];
}

function formatAvg(value: number | null): string {
  if (value === null || value === undefined) return '.---';
  return value.toFixed(3).replace(/^0/, '');
}

function PlayerGameLog({ stats }: PlayerGameLogProps) {
  const columns = [
    { key: 'season', header: 'Season', className: 'number' },
    {
      key: 'team',
      header: 'Team',
      render: (s: BattingStats) => s.team?.abbreviation || '-',
    },
    { key: 'gamesPlayed', header: 'G', className: 'number' },
    { key: 'atBats', header: 'AB', className: 'number' },
    { key: 'runs', header: 'R', className: 'number' },
    { key: 'hits', header: 'H', className: 'number' },
    { key: 'doubles', header: '2B', className: 'number' },
    { key: 'triples', header: '3B', className: 'number' },
    { key: 'homeRuns', header: 'HR', className: 'number' },
    { key: 'rbi', header: 'RBI', className: 'number' },
    { key: 'stolenBases', header: 'SB', className: 'number' },
    { key: 'walks', header: 'BB', className: 'number' },
    { key: 'strikeouts', header: 'SO', className: 'number' },
    {
      key: 'battingAvg',
      header: 'AVG',
      className: 'number',
      render: (s: BattingStats) => formatAvg(s.battingAvg),
    },
    {
      key: 'obp',
      header: 'OBP',
      className: 'number',
      render: (s: BattingStats) => formatAvg(s.obp),
    },
    {
      key: 'slg',
      header: 'SLG',
      className: 'number',
      render: (s: BattingStats) => formatAvg(s.slg),
    },
  ];

  return (
    <div className="card">
      <h3 className="card-title">Career Batting Stats</h3>
      {stats.length > 0 ? (
        <DataTable columns={columns} data={stats} keyExtractor={(s) => `${s.season}-${s.team?.id}`} />
      ) : (
        <p>No career stats available.</p>
      )}
    </div>
  );
}

export default PlayerGameLog;
