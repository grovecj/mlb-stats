import { Game } from '../../types/game';
import GameCard from './GameCard';

interface GameScheduleProps {
  games: Game[];
  title?: string;
}

function GameSchedule({ games, title }: GameScheduleProps) {
  return (
    <div className="section">
      {title && <h3 className="section-title">{title}</h3>}
      {games.length > 0 ? (
        <div className="grid grid-3">
          {games.map((game) => (
            <GameCard key={game.id} game={game} />
          ))}
        </div>
      ) : (
        <p>No games found.</p>
      )}
    </div>
  );
}

export default GameSchedule;
