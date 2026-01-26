import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { globalSearch, SearchResult } from '../../services/api';
import { event } from '../../utils/analytics';
import './SearchBar.css';

function SearchBar() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<SearchResult | null>(null);
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    if (!query.trim()) {
      setResults(null);
      setIsOpen(false);
      return;
    }

    const timeoutId = setTimeout(async () => {
      setIsLoading(true);
      try {
        const data = await globalSearch(query.trim());
        setResults(data);
        setIsOpen(true);
      } catch (error) {
        console.error('Search failed:', error);
        setResults(null);
      } finally {
        setIsLoading(false);
      }
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [query]);

  const handleSelect = (type: 'team' | 'player', id: number, name: string) => {
    event('search_select', { type, item_id: id, item_name: name, search_term: query });
    setQuery('');
    setIsOpen(false);
    navigate(type === 'team' ? `/teams/${id}` : `/players/${id}`);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (query.trim()) {
      event('search_submit', { search_term: query });
      navigate(`/players?search=${encodeURIComponent(query.trim())}`);
      setQuery('');
      setIsOpen(false);
    }
  };

  const hasResults = results && (results.teams.length > 0 || results.players.length > 0);

  return (
    <div className="search-bar-container" ref={containerRef}>
      <form onSubmit={handleSubmit} className="search-form">
        <input
          type="text"
          className="search-input"
          placeholder="Search teams & players..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => hasResults && setIsOpen(true)}
        />
        {isLoading && <span className="search-spinner" />}
      </form>

      {isOpen && results && (
        <div className="search-dropdown">
          {results.teams.length > 0 && (
            <div className="search-section">
              <div className="search-section-header">Teams</div>
              {results.teams.map((team) => (
                <button
                  key={team.id}
                  className="search-result-item"
                  onClick={() => handleSelect('team', team.id, team.name)}
                >
                  <span className="search-result-name">{team.name}</span>
                  <span className="search-result-meta">{team.abbreviation}</span>
                </button>
              ))}
            </div>
          )}

          {results.players.length > 0 && (
            <div className="search-section">
              <div className="search-section-header">Players</div>
              {results.players.map((player) => (
                <button
                  key={player.id}
                  className="search-result-item"
                  onClick={() => handleSelect('player', player.id, player.fullName)}
                >
                  <span className="search-result-name">{player.fullName}</span>
                  <span className="search-result-meta">{player.position}</span>
                </button>
              ))}
            </div>
          )}

          {!hasResults && query.trim() && (
            <div className="search-no-results">No results found</div>
          )}
        </div>
      )}
    </div>
  );
}

export default SearchBar;
