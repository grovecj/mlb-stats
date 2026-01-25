import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Game } from '../types/game';
import { getGame } from '../services/api';
import BoxScore from '../components/game/BoxScore';

function GameDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [game, setGame] = useState<Game | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchGame() {
      if (!id) return;
      try {
        const data = await getGame(parseInt(id));
        setGame(data);
      } catch (err) {
        setError('Failed to load game');
      } finally {
        setLoading(false);
      }
    }
    fetchGame();
  }, [id]);

  if (loading) return <div className="loading">Loading game...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!game) return <div className="error">Game not found</div>;

  return (
    <div>
      <div style={{ marginBottom: '16px' }}>
        <Link to="/games" style={{ color: '#666', fontSize: '14px' }}>
          &larr; Back to Games
        </Link>
      </div>

      <BoxScore game={game} />

      <div className="grid grid-2" style={{ marginTop: '24px' }}>
        <div className="card">
          <h3 className="card-title">Away Team</h3>
          <Link to={`/teams/${game.awayTeam?.id}`}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <span style={{ fontSize: '32px', fontWeight: 'bold', color: '#002d72' }}>
                {game.awayTeam?.abbreviation}
              </span>
              <div>
                <div style={{ fontWeight: '600' }}>{game.awayTeam?.name}</div>
                <div style={{ fontSize: '12px', color: '#666' }}>
                  {game.awayTeam?.league} - {game.awayTeam?.division}
                </div>
              </div>
            </div>
          </Link>
        </div>

        <div className="card">
          <h3 className="card-title">Home Team</h3>
          <Link to={`/teams/${game.homeTeam?.id}`}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <span style={{ fontSize: '32px', fontWeight: 'bold', color: '#002d72' }}>
                {game.homeTeam?.abbreviation}
              </span>
              <div>
                <div style={{ fontWeight: '600' }}>{game.homeTeam?.name}</div>
                <div style={{ fontSize: '12px', color: '#666' }}>
                  {game.homeTeam?.league} - {game.homeTeam?.division}
                </div>
              </div>
            </div>
          </Link>
        </div>
      </div>

      <div className="card" style={{ marginTop: '24px' }}>
        <h3 className="card-title">Game Information</h3>
        <table className="data-table" style={{ maxWidth: '400px' }}>
          <tbody>
            <tr>
              <td style={{ fontWeight: '600' }}>Venue</td>
              <td>{game.venueName || '-'}</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600' }}>Game Type</td>
              <td>{game.gameType === 'R' ? 'Regular Season' : game.gameType === 'P' ? 'Postseason' : game.gameType}</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600' }}>Day/Night</td>
              <td>{game.dayNight || '-'}</td>
            </tr>
            <tr>
              <td style={{ fontWeight: '600' }}>Season</td>
              <td>{game.season}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default GameDetailPage;
