import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Team, RosterEntry } from '../types/team';
import { Game } from '../types/game';
import { BattingStats } from '../types/stats';
import { getTeam, getTeamRoster, getTeamGames, getTeamStats } from '../services/api';
import { useTeamFavorite } from '../hooks/useFavorite';
import TeamRoster from '../components/team/TeamRoster';
import TeamStats from '../components/team/TeamStats';
import GameSchedule from '../components/game/GameSchedule';
import FavoriteButton from '../components/common/FavoriteButton';

function TeamDetailPage() {
  const { id } = useParams<{ id: string }>();
  const teamId = id ? parseInt(id) : undefined;
  const [team, setTeam] = useState<Team | null>(null);
  const [roster, setRoster] = useState<RosterEntry[]>([]);
  const [games, setGames] = useState<Game[]>([]);
  const [stats, setStats] = useState<BattingStats[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'roster' | 'stats' | 'games'>('roster');
  const { isFavorite, loading: favoriteLoading, toggling, toggleFavorite } = useTeamFavorite(teamId);

  useEffect(() => {
    async function fetchData() {
      if (!teamId) return;
      try {
        const [teamData, rosterData, gamesData, statsData] = await Promise.all([
          getTeam(teamId),
          getTeamRoster(teamId).catch(() => []),
          getTeamGames(teamId).catch(() => []),
          getTeamStats(teamId).catch(() => []),
        ]);
        setTeam(teamData);
        setRoster(rosterData);
        setGames(gamesData);
        setStats(statsData);
      } catch (_err) {
        setError('Failed to load team data');
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, [teamId]);

  if (loading) return <div className="loading">Loading team...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!team) return <div className="error">Team not found</div>;

  const recentGames = games.slice(0, 10);

  return (
    <div>
      <div className="card" style={{ marginBottom: '24px' }}>
        <div className="detail-header">
          {team.logoUrl ? (
            <img
              src={team.logoUrl}
              alt={`${team.name} logo`}
              className="team-detail-logo"
            />
          ) : (
            <div className="team-detail-abbrev">
              {team.abbreviation}
            </div>
          )}
          <div className="detail-header-content">
            <div className="detail-header-title">
              <h1>{team.name}</h1>
              <FavoriteButton
                isFavorite={isFavorite}
                loading={favoriteLoading}
                toggling={toggling}
                onToggle={toggleFavorite}
              />
            </div>
            <p style={{ margin: '4px 0 0', color: 'var(--text-light)' }}>
              {team.league} - {team.division} | {team.venueName}
            </p>
          </div>
        </div>
      </div>

      <div className="tab-buttons">
        <button
          onClick={() => setActiveTab('roster')}
          className={`tab-btn ${activeTab === 'roster' ? 'active' : ''}`}
        >
          Roster ({roster.length})
        </button>
        <button
          onClick={() => setActiveTab('stats')}
          className={`tab-btn ${activeTab === 'stats' ? 'active' : ''}`}
        >
          Stats
        </button>
        <button
          onClick={() => setActiveTab('games')}
          className={`tab-btn ${activeTab === 'games' ? 'active' : ''}`}
        >
          Games ({games.length})
        </button>
      </div>

      {activeTab === 'roster' && <TeamRoster roster={roster} />}
      {activeTab === 'stats' && <TeamStats stats={stats} />}
      {activeTab === 'games' && <GameSchedule games={recentGames} title="Recent Games" />}
    </div>
  );
}

export default TeamDetailPage;
