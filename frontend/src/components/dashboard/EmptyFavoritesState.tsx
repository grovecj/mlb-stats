import { Link } from 'react-router-dom';

interface EmptyFavoritesStateProps {
  type: 'teams' | 'players' | 'both';
}

function EmptyFavoritesState({ type }: EmptyFavoritesStateProps) {
  return (
    <div className="empty-favorites">
      <div className="empty-favorites-icon">
        {type === 'teams' ? (
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" />
          </svg>
        ) : type === 'players' ? (
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" />
          </svg>
        ) : (
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" />
          </svg>
        )}
      </div>
      <h3 className="empty-favorites-title">
        {type === 'teams' && 'No favorite teams yet'}
        {type === 'players' && 'No favorite players yet'}
        {type === 'both' && 'No favorites yet'}
      </h3>
      <p className="empty-favorites-text">
        {type === 'teams' && 'Add your favorite teams to see their games and standings here.'}
        {type === 'players' && 'Add your favorite players to track their stats here.'}
        {type === 'both' && 'Add teams and players to your favorites to see personalized stats and updates.'}
      </p>
      <div className="empty-favorites-actions">
        {(type === 'teams' || type === 'both') && (
          <Link to="/teams" className="btn-primary">
            Browse Teams
          </Link>
        )}
        {(type === 'players' || type === 'both') && (
          <Link to="/players" className="btn-secondary">
            Browse Players
          </Link>
        )}
      </div>
    </div>
  );
}

export default EmptyFavoritesState;
