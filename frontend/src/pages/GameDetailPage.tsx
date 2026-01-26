import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Game, BoxScore as BoxScoreType } from '../types/game';
import { getGame, getGameBoxScore } from '../services/api';
import BoxScore from '../components/game/BoxScore';
import BattingTable from '../components/game/BattingTable';
import PitchingTable from '../components/game/PitchingTable';

function GameDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [game, setGame] = useState<Game | null>(null);
  const [boxScore, setBoxScore] = useState<BoxScoreType | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'batting' | 'pitching'>('batting');

  useEffect(() => {
    async function fetchGame() {
      if (!id) return;
      try {
        const [gameData, boxScoreData] = await Promise.all([
          getGame(parseInt(id)),
          getGameBoxScore(parseInt(id)).catch(() => null),
        ]);
        setGame(gameData);
        setBoxScore(boxScoreData);
      } catch (err) {
        setError('Failed to load game');
      } finally {
        setLoading(false);
      }
    }
    fetchGame();
  }, [id]);

  const hasBoxScore = boxScore && (
    boxScore.awayBatting.length > 0 ||
    boxScore.homeBatting.length > 0 ||
    boxScore.awayPitching.length > 0 ||
    boxScore.homePitching.length > 0
  );

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

      {hasBoxScore && (
        <div className="card" style={{ marginTop: '24px' }}>
          <div style={{ display: 'flex', gap: '12px', marginBottom: '20px' }}>
            <button
              onClick={() => setActiveTab('batting')}
              style={{
                padding: '8px 16px',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontWeight: activeTab === 'batting' ? '600' : '400',
                background: activeTab === 'batting' ? 'var(--primary-color)' : 'var(--border-color)',
                color: activeTab === 'batting' ? 'white' : 'var(--text-color)',
              }}
            >
              Batting
            </button>
            <button
              onClick={() => setActiveTab('pitching')}
              style={{
                padding: '8px 16px',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontWeight: activeTab === 'pitching' ? '600' : '400',
                background: activeTab === 'pitching' ? 'var(--primary-color)' : 'var(--border-color)',
                color: activeTab === 'pitching' ? 'white' : 'var(--text-color)',
              }}
            >
              Pitching
            </button>
          </div>

          {activeTab === 'batting' && boxScore && (
            <div className="grid grid-2">
              <BattingTable batting={boxScore.awayBatting} teamName={game.awayTeam?.name || 'Away'} />
              <BattingTable batting={boxScore.homeBatting} teamName={game.homeTeam?.name || 'Home'} />
            </div>
          )}

          {activeTab === 'pitching' && boxScore && (
            <div className="grid grid-2">
              <PitchingTable pitching={boxScore.awayPitching} teamName={game.awayTeam?.name || 'Away'} />
              <PitchingTable pitching={boxScore.homePitching} teamName={game.homeTeam?.name || 'Home'} />
            </div>
          )}
        </div>
      )}

      {game.status === 'Final' && !hasBoxScore && (
        <div className="card" style={{ marginTop: '24px', textAlign: 'center', color: 'var(--text-light)' }}>
          <p>Box score data not yet available for this game.</p>
          <p style={{ fontSize: '12px' }}>Admins can sync box scores from the Admin page.</p>
        </div>
      )}
    </div>
  );
}

export default GameDetailPage;
