import { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Player } from '../types/player';
import { PageResponse } from '../types/stats';
import { getPlayers, PlayerFilters } from '../services/api';
import PlayerCard from '../components/player/PlayerCard';

const POSITIONS = ['P', 'C', '1B', '2B', '3B', 'SS', 'LF', 'CF', 'RF', 'DH'];
const POSITION_TYPES = ['Pitcher', 'Catcher', 'Infielder', 'Outfielder'];

function PlayersPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [players, setPlayers] = useState<Player[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showFilters, setShowFilters] = useState(false);

  // Filter state from URL params
  const [searchInput, setSearchInput] = useState(searchParams.get('search') || '');
  const [filters, setFilters] = useState<PlayerFilters>({
    search: searchParams.get('search') || undefined,
    position: searchParams.get('position') || undefined,
    positionType: searchParams.get('positionType') || undefined,
    bats: searchParams.get('bats') || undefined,
    throws: searchParams.get('throws') || undefined,
    active: searchParams.get('active') === 'true' ? true : searchParams.get('active') === 'false' ? false : undefined,
  });

  const fetchPlayers = useCallback(async () => {
    setLoading(true);
    try {
      const data: PageResponse<Player> = await getPlayers(page, 20, filters);
      setPlayers(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (_err) {
      setError('Failed to load players');
    } finally {
      setLoading(false);
    }
  }, [page, filters]);

  useEffect(() => {
    fetchPlayers();
  }, [fetchPlayers]);

  // Sync filters to URL
  useEffect(() => {
    const params = new URLSearchParams();
    if (filters.search) params.set('search', filters.search);
    if (filters.position) params.set('position', filters.position);
    if (filters.positionType) params.set('positionType', filters.positionType);
    if (filters.bats) params.set('bats', filters.bats);
    if (filters.throws) params.set('throws', filters.throws);
    if (filters.active !== undefined) params.set('active', String(filters.active));
    setSearchParams(params, { replace: true });
  }, [filters, setSearchParams]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    setFilters((prev) => ({ ...prev, search: searchInput || undefined }));
  };

  const updateFilter = (key: keyof PlayerFilters, value: string | boolean | undefined) => {
    setPage(0);
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  const clearFilters = () => {
    setPage(0);
    setSearchInput('');
    setFilters({});
  };

  const hasActiveFilters = Object.values(filters).some((v) => v !== undefined);

  const activeFilterCount = Object.values(filters).filter((v) => v !== undefined).length;

  if (error) return <div className="error">{error}</div>;

  return (
    <div>
      <h1 className="page-title">MLB Players</h1>

      <div className="card" style={{ marginBottom: '24px' }}>
        <form onSubmit={handleSearch} style={{ display: 'flex', flexWrap: 'wrap', gap: '12px', alignItems: 'center' }}>
          <input
            type="text"
            className="search-input"
            placeholder="Search players by name..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            style={{ flex: '1 1 200px', marginBottom: 0 }}
          />
          <button type="submit" className="tab-btn active">
            Search
          </button>
          <button
            type="button"
            className={`tab-btn ${showFilters ? 'active' : ''}`}
            onClick={() => setShowFilters(!showFilters)}
          >
            Filters {activeFilterCount > 0 && `(${activeFilterCount})`}
          </button>
          {hasActiveFilters && (
            <button type="button" className="tab-btn" onClick={clearFilters}>
              Clear All
            </button>
          )}
        </form>

        {showFilters && (
          <div style={{ marginTop: '16px', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '12px' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '4px', fontSize: '12px', color: 'var(--text-light)' }}>Position</label>
              <select
                value={filters.position || ''}
                onChange={(e) => updateFilter('position', e.target.value || undefined)}
                style={{
                  width: '100%',
                  padding: '8px',
                  borderRadius: '4px',
                  border: '1px solid var(--border-color)',
                  backgroundColor: 'var(--card-background)',
                  color: 'var(--text-color)',
                }}
              >
                <option value="">All Positions</option>
                {POSITIONS.map((p) => (
                  <option key={p} value={p}>{p}</option>
                ))}
              </select>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '4px', fontSize: '12px', color: 'var(--text-light)' }}>Position Type</label>
              <select
                value={filters.positionType || ''}
                onChange={(e) => updateFilter('positionType', e.target.value || undefined)}
                style={{
                  width: '100%',
                  padding: '8px',
                  borderRadius: '4px',
                  border: '1px solid var(--border-color)',
                  backgroundColor: 'var(--card-background)',
                  color: 'var(--text-color)',
                }}
              >
                <option value="">All Types</option>
                {POSITION_TYPES.map((t) => (
                  <option key={t} value={t}>{t}</option>
                ))}
              </select>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '4px', fontSize: '12px', color: 'var(--text-light)' }}>Bats</label>
              <select
                value={filters.bats || ''}
                onChange={(e) => updateFilter('bats', e.target.value || undefined)}
                style={{
                  width: '100%',
                  padding: '8px',
                  borderRadius: '4px',
                  border: '1px solid var(--border-color)',
                  backgroundColor: 'var(--card-background)',
                  color: 'var(--text-color)',
                }}
              >
                <option value="">Any</option>
                <option value="L">Left</option>
                <option value="R">Right</option>
                <option value="S">Switch</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '4px', fontSize: '12px', color: 'var(--text-light)' }}>Throws</label>
              <select
                value={filters.throws || ''}
                onChange={(e) => updateFilter('throws', e.target.value || undefined)}
                style={{
                  width: '100%',
                  padding: '8px',
                  borderRadius: '4px',
                  border: '1px solid var(--border-color)',
                  backgroundColor: 'var(--card-background)',
                  color: 'var(--text-color)',
                }}
              >
                <option value="">Any</option>
                <option value="L">Left</option>
                <option value="R">Right</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '4px', fontSize: '12px', color: 'var(--text-light)' }}>Status</label>
              <select
                value={filters.active === undefined ? '' : String(filters.active)}
                onChange={(e) => updateFilter('active', e.target.value === '' ? undefined : e.target.value === 'true')}
                style={{
                  width: '100%',
                  padding: '8px',
                  borderRadius: '4px',
                  border: '1px solid var(--border-color)',
                  backgroundColor: 'var(--card-background)',
                  color: 'var(--text-color)',
                }}
              >
                <option value="">All</option>
                <option value="true">Active</option>
                <option value="false">Inactive</option>
              </select>
            </div>
          </div>
        )}
      </div>

      {hasActiveFilters && (
        <p style={{ marginBottom: '16px', color: 'var(--text-light)' }}>
          Showing {totalElements.toLocaleString()} player{totalElements !== 1 ? 's' : ''}
        </p>
      )}

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
        <p>No players found. {hasActiveFilters && 'Try adjusting your filters.'}</p>
      )}
    </div>
  );
}

export default PlayersPage;
