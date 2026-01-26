import { Link } from 'react-router-dom';
import { GamePitching } from '../../types/game';
import './BoxScoreTables.css';

interface PitchingTableProps {
  pitching: GamePitching[];
  teamName: string;
}

function formatDecision(p: GamePitching): string {
  if (p.isWinner) return 'W';
  if (p.isLoser) return 'L';
  if (p.isSave) return 'S';
  return '';
}

function PitchingTable({ pitching, teamName }: PitchingTableProps) {
  // Sort: starters first, then by appearance order (we use id as proxy)
  const sortedPitching = [...pitching].sort((a, b) => {
    if (a.isStarter && !b.isStarter) return -1;
    if (!a.isStarter && b.isStarter) return 1;
    return a.id - b.id;
  });

  // Calculate totals
  const totals = sortedPitching.reduce(
    (acc, p) => ({
      inningsPitched: acc.inningsPitched + (p.inningsPitched || 0),
      hitsAllowed: acc.hitsAllowed + (p.hitsAllowed || 0),
      runsAllowed: acc.runsAllowed + (p.runsAllowed || 0),
      earnedRuns: acc.earnedRuns + (p.earnedRuns || 0),
      walks: acc.walks + (p.walks || 0),
      strikeouts: acc.strikeouts + (p.strikeouts || 0),
    }),
    { inningsPitched: 0, hitsAllowed: 0, runsAllowed: 0, earnedRuns: 0, walks: 0, strikeouts: 0 }
  );

  return (
    <div className="boxscore-table-container">
      <h4 className="boxscore-table-title">{teamName} Pitching</h4>
      <table className="boxscore-table">
        <thead>
          <tr>
            <th className="player-col">Pitcher</th>
            <th className="stat-col" title="Innings Pitched">IP</th>
            <th className="stat-col" title="Hits Allowed">H</th>
            <th className="stat-col" title="Runs Allowed">R</th>
            <th className="stat-col" title="Earned Runs">ER</th>
            <th className="stat-col" title="Walks">BB</th>
            <th className="stat-col" title="Strikeouts">K</th>
          </tr>
        </thead>
        <tbody>
          {sortedPitching.map((p) => {
            const decision = formatDecision(p);
            return (
              <tr key={p.id}>
                <td className="player-col">
                  <Link to={`/players/${p.playerId}`} className="player-link">
                    <span className="player-name">{p.playerName}</span>
                    {decision && <span className={`decision decision-${decision.toLowerCase()}`}>{decision}</span>}
                  </Link>
                </td>
                <td className="stat-col">{p.inningsPitched ?? '-'}</td>
                <td className="stat-col">{p.hitsAllowed ?? '-'}</td>
                <td className="stat-col">{p.runsAllowed ?? '-'}</td>
                <td className="stat-col">{p.earnedRuns ?? '-'}</td>
                <td className="stat-col">{p.walks ?? '-'}</td>
                <td className="stat-col">{p.strikeouts ?? '-'}</td>
              </tr>
            );
          })}
        </tbody>
        <tfoot>
          <tr className="totals-row">
            <td className="player-col">Totals</td>
            <td className="stat-col">{totals.inningsPitched.toFixed(1)}</td>
            <td className="stat-col">{totals.hitsAllowed}</td>
            <td className="stat-col">{totals.runsAllowed}</td>
            <td className="stat-col">{totals.earnedRuns}</td>
            <td className="stat-col">{totals.walks}</td>
            <td className="stat-col">{totals.strikeouts}</td>
          </tr>
        </tfoot>
      </table>
    </div>
  );
}

export default PitchingTable;
