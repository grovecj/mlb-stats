import { useState, useEffect, useRef } from 'react';
import { getPlayers } from '../../services/api';
import { Player } from '../../types/player';
import { PlayerSelection } from '../../types/comparison';
import './PlayerSelector.css';

interface PlayerSelectorProps {
  selections: PlayerSelection[];
  onSelectionsChange: (selections: PlayerSelection[]) => void;
  maxPlayers: number;
  mode: 'season' | 'career';
  currentSeason: number;
}

function PlayerSelector({
  selections,
  onSelectionsChange,
  maxPlayers,
  mode,
  currentSeason,
}: PlayerSelectorProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<Player[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setIsDropdownOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    if (!searchQuery.trim()) {
      setSearchResults([]);
      setIsDropdownOpen(false);
      return;
    }

    const timeoutId = setTimeout(async () => {
      setIsSearching(true);
      try {
        const response = await getPlayers(0, 10, { search: searchQuery.trim() });
        // Filter out already selected players
        const selectedIds = selections.map(s => s.playerId);
        const filtered = response.content.filter(p => !selectedIds.includes(p.id));
        setSearchResults(filtered);
        setIsDropdownOpen(true);
      } catch (error) {
        console.error('Search failed:', error);
        setSearchResults([]);
      } finally {
        setIsSearching(false);
      }
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [searchQuery, selections]);

  const handleSelectPlayer = (player: Player) => {
    if (selections.length >= maxPlayers) {
      return;
    }

    const newSelection: PlayerSelection = {
      playerId: player.id,
      player,
      season: currentSeason,
    };

    onSelectionsChange([...selections, newSelection]);
    setSearchQuery('');
    setSearchResults([]);
    setIsDropdownOpen(false);
  };

  const handleRemovePlayer = (playerId: number) => {
    onSelectionsChange(selections.filter(s => s.playerId !== playerId));
  };

  const handleSeasonChange = (playerId: number, season: number) => {
    onSelectionsChange(
      selections.map(s =>
        s.playerId === playerId ? { ...s, season } : s
      )
    );
  };

  const generateSeasonOptions = () => {
    const seasons: number[] = [];
    for (let year = currentSeason; year >= 2020; year--) {
      seasons.push(year);
    }
    return seasons;
  };

  const canAddMore = selections.length < maxPlayers;

  return (
    <div className="player-selector" ref={containerRef}>
      <div className="selected-players">
        {selections.map(selection => (
          <div key={selection.playerId} className="selected-player-card">
            <div className="selected-player-info">
              {selection.player?.headshotUrl && (
                <img
                  src={selection.player.headshotUrl}
                  alt={selection.player.fullName}
                  className="selected-player-headshot"
                />
              )}
              <div className="selected-player-details">
                <span className="selected-player-name">
                  {selection.player?.fullName || `Player ${selection.playerId}`}
                </span>
                <span className="selected-player-position">
                  {selection.player?.position}
                </span>
              </div>
            </div>
            <div className="selected-player-actions">
              {mode === 'season' && (
                <select
                  value={selection.season}
                  onChange={(e) => handleSeasonChange(selection.playerId, parseInt(e.target.value))}
                  className="season-select"
                >
                  {generateSeasonOptions().map(year => (
                    <option key={year} value={year}>{year}</option>
                  ))}
                </select>
              )}
              {mode === 'career' && (
                <span className="career-badge">Career</span>
              )}
              <button
                onClick={() => handleRemovePlayer(selection.playerId)}
                className="remove-player-btn"
                aria-label="Remove player"
              >
                &times;
              </button>
            </div>
          </div>
        ))}
      </div>

      {canAddMore && (
        <div className="player-search-container">
          <input
            type="text"
            className="player-search-input"
            placeholder={`Search for a player to add (${selections.length}/${maxPlayers})...`}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onFocus={() => searchResults.length > 0 && setIsDropdownOpen(true)}
          />
          {isSearching && <span className="search-spinner" />}

          {isDropdownOpen && searchResults.length > 0 && (
            <div className="player-search-dropdown">
              {searchResults.map(player => (
                <button
                  key={player.id}
                  className="player-search-result"
                  onClick={() => handleSelectPlayer(player)}
                >
                  {player.headshotUrl && (
                    <img
                      src={player.headshotUrl}
                      alt={player.fullName}
                      className="search-result-headshot"
                    />
                  )}
                  <div className="search-result-info">
                    <span className="search-result-name">{player.fullName}</span>
                    <span className="search-result-meta">{player.position}</span>
                  </div>
                </button>
              ))}
            </div>
          )}

          {isDropdownOpen && searchQuery.trim() && searchResults.length === 0 && !isSearching && (
            <div className="player-search-dropdown">
              <div className="no-results">No players found</div>
            </div>
          )}
        </div>
      )}

      {!canAddMore && (
        <p className="max-players-message">
          Maximum of {maxPlayers} players reached
        </p>
      )}
    </div>
  );
}

export default PlayerSelector;
