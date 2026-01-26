import { Link } from 'react-router-dom';
import { GameBatting } from '../../types/game';
import './BoxScoreTables.css';

interface BattingTableProps {
  batting: GameBatting[];
  teamName: string;
}

function BattingTable({ batting, teamName }: BattingTableProps) {
  if (batting.length === 0) {
    return (
      <div className="boxscore-table-container">
        <h4 className="boxscore-table-title">{teamName} Batting</h4>
        <p className="boxscore-empty">No batting stats available</p>
      </div>
    );
  }

  // Sort by batting order, null values last
  const sortedBatting = [...batting].sort((a, b) => {
    if (a.battingOrder === null && b.battingOrder === null) return 0;
    if (a.battingOrder === null) return 1;
    if (b.battingOrder === null) return 1;
    return a.battingOrder - b.battingOrder;
  });

  // Calculate totals
  const totals = sortedBatting.reduce(
    (acc, b) => ({
      atBats: acc.atBats + (b.atBats || 0),
      runs: acc.runs + (b.runs || 0),
      hits: acc.hits + (b.hits || 0),
      rbi: acc.rbi + (b.rbi || 0),
      walks: acc.walks + (b.walks || 0),
      strikeouts: acc.strikeouts + (b.strikeouts || 0),
    }),
    { atBats: 0, runs: 0, hits: 0, rbi: 0, walks: 0, strikeouts: 0 }
  );

  return (
    <div className="boxscore-table-container">
      <h4 className="boxscore-table-title">{teamName} Batting</h4>
      <table className="boxscore-table">
        <thead>
          <tr>
            <th className="player-col">Batter</th>
            <th className="stat-col" title="At Bats">AB</th>
            <th className="stat-col" title="Runs">R</th>
            <th className="stat-col" title="Hits">H</th>
            <th className="stat-col" title="Runs Batted In">RBI</th>
            <th className="stat-col" title="Walks">BB</th>
            <th className="stat-col" title="Strikeouts">SO</th>
          </tr>
        </thead>
        <tbody>
          {sortedBatting.map((b) => (
            <tr key={b.id}>
              <td className="player-col">
                <Link to={`/players/${b.playerId}`} className="player-link">
                  <span className="player-name">{b.playerName}</span>
                  {b.position && <span className="player-position">{b.position}</span>}
                </Link>
              </td>
              <td className="stat-col">{b.atBats ?? '-'}</td>
              <td className="stat-col">{b.runs ?? '-'}</td>
              <td className="stat-col">{b.hits ?? '-'}</td>
              <td className="stat-col">{b.rbi ?? '-'}</td>
              <td className="stat-col">{b.walks ?? '-'}</td>
              <td className="stat-col">{b.strikeouts ?? '-'}</td>
            </tr>
          ))}
        </tbody>
        <tfoot>
          <tr className="totals-row">
            <td className="player-col">Totals</td>
            <td className="stat-col">{totals.atBats}</td>
            <td className="stat-col">{totals.runs}</td>
            <td className="stat-col">{totals.hits}</td>
            <td className="stat-col">{totals.rbi}</td>
            <td className="stat-col">{totals.walks}</td>
            <td className="stat-col">{totals.strikeouts}</td>
          </tr>
        </tfoot>
      </table>
    </div>
  );
}

export default BattingTable;
