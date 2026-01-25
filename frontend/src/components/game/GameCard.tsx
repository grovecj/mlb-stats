import { Link } from 'react-router-dom';
import { Game } from '../../types/game';

interface GameCardProps {
  game: Game;
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-US', {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
  });
}

function GameCard({ game }: GameCardProps) {
  const isFinal = game.status === 'Final';

  return (
    <Link to={`/games/${game.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <div className="game-card">
        <div className="matchup">
          <div className="team">
            <div className="team-name">{game.awayTeam?.abbreviation || 'TBD'}</div>
            {isFinal && <div className="score">{game.awayScore ?? '-'}</div>}
          </div>
          <div className="vs">@</div>
          <div className="team">
            <div className="team-name">{game.homeTeam?.abbreviation || 'TBD'}</div>
            {isFinal && <div className="score">{game.homeScore ?? '-'}</div>}
          </div>
        </div>
        <div className="game-info">
          <div>{formatDate(game.gameDate)}</div>
          <div>{game.status}</div>
          {game.venueName && <div>{game.venueName}</div>}
        </div>
      </div>
    </Link>
  );
}

export default GameCard;
