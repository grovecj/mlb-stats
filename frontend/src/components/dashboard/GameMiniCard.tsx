import { Link } from 'react-router-dom';
import { GameSummary } from '../../types/dashboard';

interface GameMiniCardProps {
  game: GameSummary;
  label: string;
}

function formatTime(timeStr: string | null): string {
  if (!timeStr) return '';
  const [hours, minutes] = timeStr.split(':');
  const hour = parseInt(hours, 10);
  const ampm = hour >= 12 ? 'PM' : 'AM';
  const hour12 = hour % 12 || 12;
  return `${hour12}:${minutes} ${ampm}`;
}

function GameMiniCard({ game, label }: GameMiniCardProps) {
  const isFinal = game.status === 'Final';
  const isLive = game.status === 'In Progress' || game.status === 'Live';

  return (
    <Link to={`/games/${game.id}`} className="game-mini-card">
      <div className="game-mini-label">{label}</div>
      <div className="game-mini-matchup">
        <span className="game-mini-vs">{game.isHome ? 'vs' : '@'}</span>
        <span className="game-mini-opponent">{game.opponent.abbreviation}</span>
        {isFinal && (
          <span className="game-mini-score">
            {game.teamScore} - {game.opponentScore}
          </span>
        )}
        {isLive && (
          <span className="game-mini-live">LIVE</span>
        )}
        {!isFinal && !isLive && game.scheduledTime && (
          <span className="game-mini-time">{formatTime(game.scheduledTime)}</span>
        )}
      </div>
    </Link>
  );
}

export default GameMiniCard;
