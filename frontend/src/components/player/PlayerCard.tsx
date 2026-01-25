import { Link } from 'react-router-dom';
import { Player } from '../../types/player';

interface PlayerCardProps {
  player: Player;
}

function PlayerCard({ player }: PlayerCardProps) {
  return (
    <Link to={`/players/${player.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <div className="player-card">
        <h3>{player.fullName}</h3>
        <div>
          {player.position && <span className="position">{player.position}</span>}
          {player.jerseyNumber && <span className="jersey">#{player.jerseyNumber}</span>}
        </div>
        <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
          {player.bats && <span>Bats: {player.bats}</span>}
          {player.throwsHand && <span style={{ marginLeft: '12px' }}>Throws: {player.throwsHand}</span>}
        </div>
      </div>
    </Link>
  );
}

export default PlayerCard;
