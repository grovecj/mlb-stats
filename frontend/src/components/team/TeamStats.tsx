import { Link } from 'react-router-dom';
import { BattingStats } from '../../types/stats';
import DataTable from '../common/DataTable';

interface TeamStatsProps {
  stats: BattingStats[];
}

function formatAvg(value: number | null): string {
  if (value === null || value === undefined) return '.---';
  return value.toFixed(3).replace(/^0/, '');
}

function TeamStats({ stats }: TeamStatsProps) {
  const columns = [
    {
      key: 'name',
      header: 'Player',
      render: (s: BattingStats) => (
        <Link to={`/players/${s.player.id}`}>{s.player.fullName}</Link>
      ),
    },
    { key: 'gamesPlayed', header: 'G', className: 'number' },
    { key: 'atBats', header: 'AB', className: 'number' },
    { key: 'runs', header: 'R', className: 'number' },
    { key: 'hits', header: 'H', className: 'number' },
    { key: 'homeRuns', header: 'HR', className: 'number' },
    { key: 'rbi', header: 'RBI', className: 'number' },
    { key: 'stolenBases', header: 'SB', className: 'number' },
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
    {
      key: 'ops',
      header: 'OPS',
      className: 'number',
      render: (s: BattingStats) => formatAvg(s.ops),
    },
  ];

  return (
    <div className="card">
      <h3 className="card-title">Team Batting Stats</h3>
      {stats.length > 0 ? (
        <DataTable columns={columns} data={stats} keyExtractor={(s) => s.id} />
      ) : (
        <p>No stats available.</p>
      )}
    </div>
  );
}

export default TeamStats;
