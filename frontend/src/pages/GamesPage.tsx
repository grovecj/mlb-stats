import { useState, useEffect } from 'react';
import { Game } from '../types/game';
import { getGames } from '../services/api';
import GameCard from '../components/game/GameCard';

function GamesPage() {
  const [games, setGames] = useState<Game[]>([]);
  const [date, setDate] = useState(() => new Date().toISOString().split('T')[0]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchGames() {
      setLoading(true);
      setError(null);
      try {
        const data = await getGames({ date });
        setGames(Array.isArray(data) ? data : (data as { content: Game[] }).content || []);
      } catch (_err) {
        setError('Failed to load games');
        setGames([]);
      } finally {
        setLoading(false);
      }
    }
    fetchGames();
  }, [date]);

  const changeDate = (days: number) => {
    const currentDate = new Date(date);
    currentDate.setDate(currentDate.getDate() + days);
    setDate(currentDate.toISOString().split('T')[0]);
  };

  const formatDisplayDate = (dateStr: string) => {
    return new Date(dateStr).toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  return (
    <div>
      <h1 className="page-title">MLB Games</h1>

      <div className="date-picker-container" style={{ marginBottom: '24px' }}>
        <button
          onClick={() => changeDate(-1)}
          style={{
            padding: '8px 16px',
            backgroundColor: '#fff',
            border: '1px solid #e0e0e0',
            borderRadius: '4px',
            cursor: 'pointer',
          }}
        >
          &larr; Previous
        </button>

        <input
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          style={{
            padding: '8px 12px',
            border: '1px solid #e0e0e0',
            borderRadius: '4px',
            fontSize: '14px',
          }}
        />

        <button
          onClick={() => changeDate(1)}
          style={{
            padding: '8px 16px',
            backgroundColor: '#fff',
            border: '1px solid #e0e0e0',
            borderRadius: '4px',
            cursor: 'pointer',
          }}
        >
          Next &rarr;
        </button>

        <button
          onClick={() => setDate(new Date().toISOString().split('T')[0])}
          style={{
            padding: '8px 16px',
            backgroundColor: '#002d72',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            marginLeft: '8px',
          }}
        >
          Today
        </button>
      </div>

      <h2 className="section-title">{formatDisplayDate(date)}</h2>

      {loading ? (
        <div className="loading">Loading games...</div>
      ) : error ? (
        <div className="error">{error}</div>
      ) : games.length > 0 ? (
        <div className="grid grid-3">
          {games.map((game) => (
            <GameCard key={game.id} game={game} />
          ))}
        </div>
      ) : (
        <p>No games scheduled for this date.</p>
      )}
    </div>
  );
}

export default GamesPage;
