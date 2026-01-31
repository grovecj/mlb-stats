import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Player } from '../types/player';
import { BattingStats, PitchingStats } from '../types/stats';
import { getPlayer, getPlayerBattingStats, getPlayerPitchingStats } from '../services/api';
import { usePlayerFavorite } from '../hooks/useFavorite';
import PlayerStats from '../components/player/PlayerStats';
import PlayerGameLog from '../components/player/PlayerGameLog';
import CareerStats from '../components/player/CareerStats';
import FavoriteButton from '../components/common/FavoriteButton';
import { getDefaultSeason } from '../utils/season';

function PlayerDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const playerId = id ? parseInt(id) : undefined;
  const [player, setPlayer] = useState<Player | null>(null);
  const [battingStats, setBattingStats] = useState<BattingStats[]>([]);
  const [pitchingStats, setPitchingStats] = useState<PitchingStats[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { isFavorite, loading: favoriteLoading, toggling, toggleFavorite } = usePlayerFavorite(playerId);

  useEffect(() => {
    async function fetchData() {
      if (!playerId) return;
      try {
        const [playerData, batting, pitching] = await Promise.all([
          getPlayer(playerId),
          getPlayerBattingStats(playerId).catch(() => []),
          getPlayerPitchingStats(playerId).catch(() => []),
        ]);
        setPlayer(playerData);
        setBattingStats(batting);
        setPitchingStats(pitching);
      } catch (_err) {
        setError('Failed to load player data');
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, [playerId]);

  if (loading) return <div className="loading">Loading player...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!player) return <div className="error">Player not found</div>;

  const formatDate = (dateStr: string | null) => {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  return (
    <div>
      <div className="card" style={{ marginBottom: '24px' }}>
        <div className="detail-header">
          {player.headshotUrl ? (
            <img
              src={player.headshotUrl}
              alt={player.fullName}
              className="detail-header-image"
            />
          ) : (
            <div className="detail-header-placeholder">
              {player.jerseyNumber || '?'}
            </div>
          )}
          <div className="detail-header-content">
            <div className="detail-header-title">
              <h1>{player.fullName}</h1>
              <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                <button
                  className="tab-btn"
                  onClick={() => navigate(`/compare?players=${playerId}&seasons=${getDefaultSeason()}`)}
                  style={{ padding: '6px 12px' }}
                >
                  Compare
                </button>
                <FavoriteButton
                  isFavorite={isFavorite}
                  loading={favoriteLoading}
                  toggling={toggling}
                  onToggle={toggleFavorite}
                />
              </div>
            </div>
            <div style={{ marginTop: '8px' }}>
              {player.position && (
                <span style={{
                  display: 'inline-block',
                  padding: '4px 12px',
                  backgroundColor: 'var(--primary-color)',
                  color: 'white',
                  borderRadius: '4px',
                  fontSize: '14px',
                  marginRight: '8px',
                }}>
                  {player.position}
                </span>
              )}
              {player.positionType && (
                <span style={{ color: 'var(--text-light)', fontSize: '14px' }}>{player.positionType}</span>
              )}
            </div>

            <div className="detail-stats-grid">
              <div className="detail-stat-item">
                <div className="detail-stat-label">Bats/Throws</div>
                <div className="detail-stat-value">{player.bats || '-'}/{player.throwsHand || '-'}</div>
              </div>
              <div className="detail-stat-item">
                <div className="detail-stat-label">Height/Weight</div>
                <div className="detail-stat-value">{player.height || '-'} / {player.weight || '-'} lbs</div>
              </div>
              <div className="detail-stat-item">
                <div className="detail-stat-label">Birth Date</div>
                <div className="detail-stat-value">{formatDate(player.birthDate)}</div>
              </div>
              <div className="detail-stat-item">
                <div className="detail-stat-label">MLB Debut</div>
                <div className="detail-stat-value">{formatDate(player.mlbDebutDate)}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <PlayerStats battingStats={battingStats} pitchingStats={pitchingStats} />

      {playerId && (
        <PlayerGameLog playerId={playerId} positionType={player.positionType} />
      )}

      <CareerStats stats={battingStats} />
    </div>
  );
}

export default PlayerDetailPage;
