import { Link } from 'react-router-dom';
import { Player } from '../../types/player';

interface PlayerCardProps {
  player: Player;
}

function PlayerCard({ player }: PlayerCardProps) {
  return (
    <Link to={`/players/${player.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <div className="player-card">
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          {player.headshotUrl ? (
            <img
              src={player.headshotUrl}
              alt={player.fullName}
              style={{
                width: '48px',
                height: '48px',
                borderRadius: '50%',
                objectFit: 'cover',
                backgroundColor: 'var(--border-color)',
              }}
            />
          ) : (
            <div style={{
              width: '48px',
              height: '48px',
              borderRadius: '50%',
              backgroundColor: 'var(--primary-color)',
              color: 'white',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 'bold',
              fontSize: '14px',
            }}>
              {player.jerseyNumber || '?'}
            </div>
          )}
          <div style={{ flex: 1 }}>
            <h3 style={{ margin: 0 }}>{player.fullName}</h3>
            <div>
              {player.position && <span className="position">{player.position}</span>}
              {player.jerseyNumber && <span className="jersey">#{player.jerseyNumber}</span>}
            </div>
          </div>
        </div>
        <div style={{ marginTop: '8px', fontSize: '12px', color: 'var(--text-light)' }}>
          {player.bats && <span>Bats: {player.bats}</span>}
          {player.throwsHand && <span style={{ marginLeft: '12px' }}>Throws: {player.throwsHand}</span>}
        </div>
      </div>
    </Link>
  );
}

export default PlayerCard;
