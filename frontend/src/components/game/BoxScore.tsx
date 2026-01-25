import { Link } from 'react-router-dom';
import { Game } from '../../types/game';

interface BoxScoreProps {
  game: Game;
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
}

function BoxScore({ game }: BoxScoreProps) {
  const isFinal = game.status === 'Final';

  return (
    <div className="card">
      <div style={{ textAlign: 'center', marginBottom: '24px' }}>
        <div style={{ fontSize: '14px', color: '#666', marginBottom: '8px' }}>
          {formatDate(game.gameDate)} | {game.venueName}
        </div>
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '32px' }}>
          <div style={{ textAlign: 'center' }}>
            <Link to={`/teams/${game.awayTeam?.id}`}>
              <div style={{ fontSize: '24px', fontWeight: 'bold' }}>{game.awayTeam?.abbreviation}</div>
              <div style={{ fontSize: '14px', color: '#666' }}>{game.awayTeam?.name}</div>
            </Link>
            {isFinal && (
              <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#002d72', marginTop: '8px' }}>
                {game.awayScore}
              </div>
            )}
          </div>

          <div style={{ fontSize: '24px', color: '#666' }}>@</div>

          <div style={{ textAlign: 'center' }}>
            <Link to={`/teams/${game.homeTeam?.id}`}>
              <div style={{ fontSize: '24px', fontWeight: 'bold' }}>{game.homeTeam?.abbreviation}</div>
              <div style={{ fontSize: '14px', color: '#666' }}>{game.homeTeam?.name}</div>
            </Link>
            {isFinal && (
              <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#002d72', marginTop: '8px' }}>
                {game.homeScore}
              </div>
            )}
          </div>
        </div>
        <div style={{ marginTop: '16px', fontSize: '16px', fontWeight: '600' }}>
          {game.status}
        </div>
      </div>

      <div style={{ borderTop: '1px solid #e0e0e0', paddingTop: '16px' }}>
        <table className="data-table" style={{ maxWidth: '400px', margin: '0 auto' }}>
          <thead>
            <tr>
              <th>Team</th>
              <th style={{ textAlign: 'right' }}>Runs</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{game.awayTeam?.name}</td>
              <td style={{ textAlign: 'right', fontWeight: 'bold' }}>{game.awayScore ?? '-'}</td>
            </tr>
            <tr>
              <td>{game.homeTeam?.name}</td>
              <td style={{ textAlign: 'right', fontWeight: 'bold' }}>{game.homeScore ?? '-'}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default BoxScore;
