import { Link } from 'react-router-dom';
import { PlayerComparisonResponse, ComparisonBattingStats, ComparisonPitchingStats } from '../../types/comparison';
import './ComparisonTable.css';

interface ComparisonTableProps {
  data: PlayerComparisonResponse;
}

interface StatRow {
  label: string;
  key: string;
  format?: (value: number | null) => string;
}

const formatAvg = (value: number | null): string => {
  if (value === null) return 'N/A';
  return value.toFixed(3).replace(/^0/, '');
};

const formatEra = (value: number | null): string => {
  if (value === null) return 'N/A';
  return value.toFixed(2);
};

const formatInt = (value: number | null): string => {
  if (value === null) return 'N/A';
  return value.toString();
};

const formatIp = (value: number | null): string => {
  if (value === null) return 'N/A';
  return value.toFixed(1);
};

const battingStats: StatRow[] = [
  { label: 'Games', key: 'gamesPlayed', format: formatInt },
  { label: 'AB', key: 'atBats', format: formatInt },
  { label: 'R', key: 'runs', format: formatInt },
  { label: 'H', key: 'hits', format: formatInt },
  { label: '2B', key: 'doubles', format: formatInt },
  { label: '3B', key: 'triples', format: formatInt },
  { label: 'HR', key: 'homeRuns', format: formatInt },
  { label: 'RBI', key: 'rbi', format: formatInt },
  { label: 'SB', key: 'stolenBases', format: formatInt },
  { label: 'BB', key: 'walks', format: formatInt },
  { label: 'SO', key: 'strikeouts', format: formatInt },
  { label: 'AVG', key: 'battingAvg', format: formatAvg },
  { label: 'OBP', key: 'obp', format: formatAvg },
  { label: 'SLG', key: 'slg', format: formatAvg },
  { label: 'OPS', key: 'ops', format: formatAvg },
];

const pitchingStats: StatRow[] = [
  { label: 'Games', key: 'gamesPlayed', format: formatInt },
  { label: 'GS', key: 'gamesStarted', format: formatInt },
  { label: 'W', key: 'wins', format: formatInt },
  { label: 'L', key: 'losses', format: formatInt },
  { label: 'SV', key: 'saves', format: formatInt },
  { label: 'IP', key: 'inningsPitched', format: formatIp },
  { label: 'H', key: 'hitsAllowed', format: formatInt },
  { label: 'ER', key: 'earnedRuns', format: formatInt },
  { label: 'BB', key: 'walks', format: formatInt },
  { label: 'SO', key: 'strikeouts', format: formatInt },
  { label: 'ERA', key: 'era', format: formatEra },
  { label: 'WHIP', key: 'whip', format: formatEra },
  { label: 'K/9', key: 'kPer9', format: formatEra },
];

function ComparisonTable({ data }: ComparisonTableProps) {
  const { players, leaders, mode } = data;

  const hasBattingStats = players.some(p => p.battingStats !== null);
  const hasPitchingStats = players.some(p => p.pitchingStats !== null);

  const isLeader = (playerId: number, statKey: string, type: 'batting' | 'pitching'): boolean => {
    const leaderMap = type === 'batting' ? leaders.batting : leaders.pitching;
    return leaderMap[statKey] === playerId;
  };

  const getBattingValue = (stats: ComparisonBattingStats | null, key: string): number | null => {
    if (!stats) return null;
    return stats[key as keyof ComparisonBattingStats] as number | null;
  };

  const getPitchingValue = (stats: ComparisonPitchingStats | null, key: string): number | null => {
    if (!stats) return null;
    return stats[key as keyof ComparisonPitchingStats] as number | null;
  };

  return (
    <div className="comparison-container">
      {/* Player Headers */}
      <div className="comparison-header">
        <div className="header-spacer"></div>
        {players.map(entry => (
          <div key={entry.player.id} className="player-header-card">
            {entry.player.headshotUrl && (
              <img
                src={entry.player.headshotUrl}
                alt={entry.player.fullName}
                className="player-header-headshot"
              />
            )}
            <Link to={`/players/${entry.player.id}`} className="player-header-name">
              {entry.player.fullName}
            </Link>
            <span className="player-header-position">{entry.player.position}</span>
            {mode === 'season' && entry.season && (
              <span className="player-header-season">{entry.season}</span>
            )}
            {mode === 'career' && (
              <span className="player-header-career">Career</span>
            )}
          </div>
        ))}
      </div>

      {/* Batting Stats Table */}
      {hasBattingStats && (
        <div className="comparison-section">
          <h3 className="comparison-section-title">Batting</h3>
          <div className="table-responsive">
            <table className="comparison-table">
              <thead>
                <tr>
                  <th className="stat-label-col">Stat</th>
                  {players.map(entry => (
                    <th key={entry.player.id}>{entry.player.lastName}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {battingStats.map(stat => (
                  <tr key={stat.key}>
                    <td className="stat-label-col">{stat.label}</td>
                    {players.map(entry => {
                      const value = getBattingValue(entry.battingStats, stat.key);
                      const formattedValue = stat.format ? stat.format(value) : (value ?? 'N/A');
                      const leader = isLeader(entry.player.id, stat.key, 'batting');
                      return (
                        <td
                          key={entry.player.id}
                          className={`stat-value ${leader ? 'leader' : ''}`}
                        >
                          {formattedValue}
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Pitching Stats Table */}
      {hasPitchingStats && (
        <div className="comparison-section">
          <h3 className="comparison-section-title">Pitching</h3>
          <div className="table-responsive">
            <table className="comparison-table">
              <thead>
                <tr>
                  <th className="stat-label-col">Stat</th>
                  {players.map(entry => (
                    <th key={entry.player.id}>{entry.player.lastName}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {pitchingStats.map(stat => (
                  <tr key={stat.key}>
                    <td className="stat-label-col">{stat.label}</td>
                    {players.map(entry => {
                      const value = getPitchingValue(entry.pitchingStats, stat.key);
                      const formattedValue = stat.format ? stat.format(value) : (value ?? 'N/A');
                      const leader = isLeader(entry.player.id, stat.key, 'pitching');
                      return (
                        <td
                          key={entry.player.id}
                          className={`stat-value ${leader ? 'leader' : ''}`}
                        >
                          {formattedValue}
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {!hasBattingStats && !hasPitchingStats && (
        <div className="no-stats-message">
          No stats available for the selected players and seasons.
        </div>
      )}
    </div>
  );
}

export default ComparisonTable;
