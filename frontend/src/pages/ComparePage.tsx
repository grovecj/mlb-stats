import { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { comparePlayerStats, getPlayer } from '../services/api';
import { PlayerComparisonResponse, PlayerSelection } from '../types/comparison';
import PlayerSelector from '../components/compare/PlayerSelector';
import ComparisonTable from '../components/compare/ComparisonTable';
import { event } from '../utils/analytics';

function getDefaultSeason(): number {
  const now = new Date();
  const month = now.getMonth();
  return month < 3 ? now.getFullYear() - 1 : now.getFullYear();
}

function ComparePage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [selections, setSelections] = useState<PlayerSelection[]>([]);
  const [mode, setMode] = useState<'season' | 'career'>('season');
  const [comparisonData, setComparisonData] = useState<PlayerComparisonResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isMobile, setIsMobile] = useState(window.innerWidth < 768);

  const currentSeason = getDefaultSeason();
  const maxPlayers = isMobile ? 2 : 4;

  // Handle responsive detection
  useEffect(() => {
    const handleResize = () => {
      const mobile = window.innerWidth < 768;
      setIsMobile(mobile);
      // If switching to mobile and have more than 2 players, trim to 2
      if (mobile && selections.length > 2) {
        setSelections(prev => prev.slice(0, 2));
      }
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, [selections.length]);

  // Load initial state from URL on mount only
  useEffect(() => {
    const playersParam = searchParams.get('players');
    const seasonsParam = searchParams.get('seasons');
    const modeParam = searchParams.get('mode');

    if (modeParam === 'career') {
      setMode('career');
    }

    if (playersParam) {
      const playerIds = playersParam.split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id));
      const seasons = seasonsParam
        ? seasonsParam.split(',').map(s => parseInt(s.trim())).filter(s => !isNaN(s))
        : playerIds.map(() => currentSeason);

      // Load player details
      const loadPlayers = async () => {
        const loadedSelections: PlayerSelection[] = [];
        for (let i = 0; i < Math.min(playerIds.length, maxPlayers); i++) {
          try {
            const player = await getPlayer(playerIds[i]);
            loadedSelections.push({
              playerId: playerIds[i],
              player,
              season: seasons[i] || currentSeason,
            });
          } catch {
            // Player not found, skip
            console.warn(`Player ${playerIds[i]} not found`);
          }
        }
        if (loadedSelections.length > 0) {
          setSelections(loadedSelections);
        }
      };

      loadPlayers();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []); // Intentionally empty - only parse URL on initial mount

  // Sync state to URL
  const syncToUrl = useCallback((newSelections: PlayerSelection[], newMode: 'season' | 'career') => {
    const params = new URLSearchParams();
    if (newSelections.length > 0) {
      params.set('players', newSelections.map(s => s.playerId).join(','));
      if (newMode === 'season') {
        params.set('seasons', newSelections.map(s => s.season).join(','));
      }
    }
    if (newMode === 'career') {
      params.set('mode', 'career');
    }
    setSearchParams(params, { replace: true });
  }, [setSearchParams]);

  // Fetch comparison when we have 2+ players
  useEffect(() => {
    if (selections.length < 2) {
      setComparisonData(null);
      return;
    }

    const fetchComparison = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await comparePlayerStats(selections, mode);
        setComparisonData(data);
        event('player_comparison', {
          player_count: selections.length,
          mode,
          player_ids: selections.map(s => s.playerId).join(','),
        });
      } catch (err) {
        console.error('Failed to compare players:', err);
        setError('Failed to load comparison data. Please try again.');
        setComparisonData(null);
      } finally {
        setLoading(false);
      }
    };

    fetchComparison();
    syncToUrl(selections, mode);
  }, [selections, mode, syncToUrl]);

  const handleSelectionsChange = (newSelections: PlayerSelection[]) => {
    setSelections(newSelections);
  };

  const handleModeChange = (newMode: 'season' | 'career') => {
    setMode(newMode);
    event('comparison_mode_change', { mode: newMode });
  };

  return (
    <div>
      <h1 className="page-title">Compare Players</h1>

      <div className="card" style={{ marginBottom: '24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px', marginBottom: '16px' }}>
          <div style={{ display: 'flex', gap: '8px' }}>
            <button
              className={`tab-btn ${mode === 'season' ? 'active' : ''}`}
              onClick={() => handleModeChange('season')}
            >
              By Season
            </button>
            <button
              className={`tab-btn ${mode === 'career' ? 'active' : ''}`}
              onClick={() => handleModeChange('career')}
            >
              Career Totals
            </button>
          </div>
          {isMobile && (
            <span style={{ fontSize: '12px', color: 'var(--text-light)' }}>
              Mobile: max 2 players
            </span>
          )}
        </div>

        <PlayerSelector
          selections={selections}
          onSelectionsChange={handleSelectionsChange}
          maxPlayers={maxPlayers}
          mode={mode}
          currentSeason={currentSeason}
        />
      </div>

      {loading && (
        <div className="loading">Loading comparison...</div>
      )}

      {error && (
        <div className="error">{error}</div>
      )}

      {!loading && !error && selections.length < 2 && (
        <div className="card" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-light)' }}>
          <p style={{ fontSize: '16px', marginBottom: '8px' }}>
            Select at least 2 players to compare
          </p>
          <p style={{ fontSize: '14px' }}>
            Search for players above and select them to see a side-by-side comparison
          </p>
        </div>
      )}

      {!loading && !error && comparisonData && (
        <ComparisonTable data={comparisonData} />
      )}
    </div>
  );
}

export default ComparePage;
