import { useState, useEffect, useCallback } from 'react';
import { Player } from '../types/player';
import { PageResponse } from '../types/stats';
import { getPlayers } from '../services/api';
import PlayerCard from '../components/player/PlayerCard';

function PlayersPage() {
  const [players, setPlayers] = useState<Player[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchPlayers = useCallback(async () => {
    setLoading(true);
    try {
      const data: PageResponse<Player> = await getPlayers(page, 20, search || undefined);
      setPlayers(data.content);
      setTotalPages(data.totalPages);
    } catch (_err) {
      setError('Failed to load players');
    } finally {
      setLoading(false);
    }
  }, [page, search]);

  useEffect(() => {
    fetchPlayers();
  }, [fetchPlayers]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    setSearch(searchInput);
  };

  if (error) return <div className="error">{error}</div>;

  return (
    <div>
      <h1 className="page-title">MLB Players</h1>

      <form onSubmit={handleSearch} className="search-form" style={{ marginBottom: '20px', display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
        <input
          type="text"
          className="search-input"
          placeholder="Search players by name..."
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          style={{ marginBottom: 0 }}
        />
        <button type="submit" className="tab-btn active">
          Search
        </button>
      </form>

      {loading ? (
        <div className="loading">Loading players...</div>
      ) : players.length > 0 ? (
        <>
          <div className="grid grid-4">
            {players.map((player) => (
              <PlayerCard key={player.id} player={player} />
            ))}
          </div>

          <div className="pagination">
            <button
              disabled={page === 0}
              onClick={() => setPage((p) => p - 1)}
            >
              Previous
            </button>
            <span style={{ padding: '8px 16px' }}>
              Page {page + 1} of {totalPages}
            </span>
            <button
              disabled={page >= totalPages - 1}
              onClick={() => setPage((p) => p + 1)}
            >
              Next
            </button>
          </div>
        </>
      ) : (
        <p>No players found. {search && 'Try a different search term.'}</p>
      )}
    </div>
  );
}

export default PlayersPage;
