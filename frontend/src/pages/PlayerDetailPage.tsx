import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Player } from '../types/player';
import { BattingStats, PitchingStats } from '../types/stats';
import { getPlayer, getPlayerBattingStats, getPlayerPitchingStats } from '../services/api';
import PlayerStats from '../components/player/PlayerStats';
import PlayerGameLog from '../components/player/PlayerGameLog';

function PlayerDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [player, setPlayer] = useState<Player | null>(null);
  const [battingStats, setBattingStats] = useState<BattingStats[]>([]);
  const [pitchingStats, setPitchingStats] = useState<PitchingStats[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchData() {
      if (!id) return;
      try {
        const playerId = parseInt(id);
        const [playerData, batting, pitching] = await Promise.all([
          getPlayer(playerId),
          getPlayerBattingStats(playerId).catch(() => []),
          getPlayerPitchingStats(playerId).catch(() => []),
        ]);
        setPlayer(playerData);
        setBattingStats(batting);
        setPitchingStats(pitching);
      } catch (err) {
        setError('Failed to load player data');
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, [id]);

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
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '24px' }}>
          <div style={{
            width: '80px',
            height: '80px',
            backgroundColor: '#002d72',
            color: 'white',
            borderRadius: '50%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '32px',
            fontWeight: 'bold',
          }}>
            {player.jerseyNumber || '?'}
          </div>
          <div style={{ flex: 1 }}>
            <h1 style={{ margin: 0, fontSize: '28px' }}>{player.fullName}</h1>
            <div style={{ marginTop: '8px' }}>
              {player.position && (
                <span style={{
                  display: 'inline-block',
                  padding: '4px 12px',
                  backgroundColor: '#002d72',
                  color: 'white',
                  borderRadius: '4px',
                  fontSize: '14px',
                  marginRight: '8px',
                }}>
                  {player.position}
                </span>
              )}
              {player.positionType && (
                <span style={{ color: '#666', fontSize: '14px' }}>{player.positionType}</span>
              )}
            </div>

            <div style={{ marginTop: '16px', display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '16px' }}>
              <div>
                <div style={{ fontSize: '12px', color: '#666', textTransform: 'uppercase' }}>Bats/Throws</div>
                <div style={{ fontWeight: '600' }}>{player.bats || '-'}/{player.throwsHand || '-'}</div>
              </div>
              <div>
                <div style={{ fontSize: '12px', color: '#666', textTransform: 'uppercase' }}>Height/Weight</div>
                <div style={{ fontWeight: '600' }}>{player.height || '-'} / {player.weight || '-'} lbs</div>
              </div>
              <div>
                <div style={{ fontSize: '12px', color: '#666', textTransform: 'uppercase' }}>Birth Date</div>
                <div style={{ fontWeight: '600' }}>{formatDate(player.birthDate)}</div>
              </div>
              <div>
                <div style={{ fontSize: '12px', color: '#666', textTransform: 'uppercase' }}>MLB Debut</div>
                <div style={{ fontWeight: '600' }}>{formatDate(player.mlbDebutDate)}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <PlayerStats battingStats={battingStats} pitchingStats={pitchingStats} />

      {battingStats.length > 1 && (
        <PlayerGameLog stats={battingStats} />
      )}
    </div>
  );
}

export default PlayerDetailPage;
