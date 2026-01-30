import { Linescore as LinescoreType } from '../../types/game';
import { Team } from '../../types/team';
import './Linescore.css';

interface LinescoreProps {
  linescore: LinescoreType;
  awayTeam: Team;
  homeTeam: Team;
}

function Linescore({ linescore, awayTeam, homeTeam }: LinescoreProps) {
  const { innings, awayTotals, homeTotals, liveState } = linescore;

  // Determine the minimum number of innings to display (9 for a standard game)
  const minInnings = 9;
  const displayInnings = Math.max(innings.length, minInnings);

  // Create array of inning numbers to display
  const inningNumbers = Array.from({ length: displayInnings }, (_, i) => i + 1);

  const formatRuns = (runs: number | null, inningNum: number): string => {
    // For future innings (no data yet)
    if (runs === null) {
      // Check if this is an in-progress game and this inning hasn't been played yet
      const inningData = innings.find(i => i.inning === inningNum);
      if (!inningData) {
        return '';
      }
      return '-';
    }
    return String(runs);
  };

  const isCurrentInning = (inningNum: number, isTop: boolean): boolean => {
    if (!liveState.isLive || !liveState.currentInning) return false;
    const isTopHalf = liveState.inningHalf === 'Top';
    return liveState.currentInning === inningNum && isTop === isTopHalf;
  };

  return (
    <div className="linescore-wrapper">
      <div className="linescore-scroll">
        <table className="linescore-table">
          <thead>
            <tr>
              <th className="linescore-team-col">Team</th>
              {inningNumbers.map(num => (
                <th
                  key={num}
                  className={`linescore-inning-col ${
                    liveState.isLive && liveState.currentInning === num ? 'linescore-current-inning' : ''
                  }`}
                >
                  {num}
                </th>
              ))}
              <th className="linescore-total-col">R</th>
              <th className="linescore-total-col">H</th>
              <th className="linescore-total-col">E</th>
            </tr>
          </thead>
          <tbody>
            {/* Away team row */}
            <tr>
              <td className="linescore-team-cell">
                <span className="linescore-team-abbr">{awayTeam.abbreviation}</span>
              </td>
              {inningNumbers.map(num => {
                const inning = innings.find(i => i.inning === num);
                const runs = inning?.awayRuns ?? null;
                return (
                  <td
                    key={num}
                    className={`linescore-inning-cell ${
                      isCurrentInning(num, true) ? 'linescore-active-cell' : ''
                    } ${runs !== null && runs > 0 ? 'linescore-scored' : ''}`}
                  >
                    {formatRuns(runs, num)}
                  </td>
                );
              })}
              <td className="linescore-total-cell">{awayTotals.runs ?? '-'}</td>
              <td className="linescore-total-cell">{awayTotals.hits ?? '-'}</td>
              <td className="linescore-total-cell">{awayTotals.errors ?? '-'}</td>
            </tr>
            {/* Home team row */}
            <tr>
              <td className="linescore-team-cell">
                <span className="linescore-team-abbr">{homeTeam.abbreviation}</span>
              </td>
              {inningNumbers.map(num => {
                const inning = innings.find(i => i.inning === num);
                const runs = inning?.homeRuns ?? null;
                // Show 'X' for bottom of 9th+ if home team is winning and game is final
                const isWalkOff = num >= 9 &&
                  !liveState.isLive &&
                  runs === null &&
                  (homeTotals.runs ?? 0) > (awayTotals.runs ?? 0);
                return (
                  <td
                    key={num}
                    className={`linescore-inning-cell ${
                      isCurrentInning(num, false) ? 'linescore-active-cell' : ''
                    } ${runs !== null && runs > 0 ? 'linescore-scored' : ''}`}
                  >
                    {isWalkOff ? 'X' : formatRuns(runs, num)}
                  </td>
                );
              })}
              <td className="linescore-total-cell">{homeTotals.runs ?? '-'}</td>
              <td className="linescore-total-cell">{homeTotals.hits ?? '-'}</td>
              <td className="linescore-total-cell">{homeTotals.errors ?? '-'}</td>
            </tr>
          </tbody>
        </table>
      </div>
      {liveState.isLive && (
        <div className="linescore-live-indicator">
          <span className="linescore-live-dot"></span>
          {liveState.inningHalf} {liveState.currentInning}
          {liveState.outs !== null && ` | ${liveState.outs} out${liveState.outs !== 1 ? 's' : ''}`}
        </div>
      )}
    </div>
  );
}

export default Linescore;
