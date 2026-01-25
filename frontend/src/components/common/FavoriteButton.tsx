interface FavoriteButtonProps {
  isFavorite: boolean;
  loading: boolean;
  toggling: boolean;
  onToggle: () => void;
}

function FavoriteButton({ isFavorite, loading, toggling, onToggle }: FavoriteButtonProps) {
  if (loading) {
    return null;
  }

  return (
    <button
      onClick={onToggle}
      disabled={toggling}
      title={isFavorite ? 'Remove from favorites' : 'Add to favorites'}
      style={{
        padding: '8px 16px',
        backgroundColor: isFavorite ? 'var(--secondary-color)' : 'transparent',
        color: isFavorite ? 'white' : 'var(--secondary-color)',
        border: `2px solid var(--secondary-color)`,
        borderRadius: '4px',
        cursor: toggling ? 'wait' : 'pointer',
        display: 'flex',
        alignItems: 'center',
        gap: '6px',
        fontSize: '14px',
        fontWeight: '600',
        opacity: toggling ? 0.7 : 1,
        transition: 'all 0.2s ease',
      }}
    >
      <span style={{ fontSize: '16px' }}>{isFavorite ? '\u2605' : '\u2606'}</span>
      {isFavorite ? 'Favorited' : 'Favorite'}
    </button>
  );
}

export default FavoriteButton;
