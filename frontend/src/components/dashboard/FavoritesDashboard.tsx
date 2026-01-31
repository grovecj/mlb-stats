import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FavoritesDashboard as DashboardData } from '../../types/dashboard';
import { getFavoritesDashboard } from '../../services/api';
import FavoriteTeamCard from './FavoriteTeamCard';
import FavoritePlayerCard from './FavoritePlayerCard';
import EmptyFavoritesState from './EmptyFavoritesState';

function FavoritesDashboard() {
  const [dashboard, setDashboard] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadDashboard() {
      try {
        setLoading(true);
        const data = await getFavoritesDashboard();
        setDashboard(data);
        setError(null);
      } catch (err) {
        setError('Failed to load dashboard');
        console.error('Dashboard load error:', err);
      } finally {
        setLoading(false);
      }
    }
    loadDashboard();
  }, []);

  if (loading) {
    return (
      <div className="favorites-dashboard loading">
        <div className="loading-spinner">Loading your favorites...</div>
      </div>
    );
  }

  if (error) {
    return null;
  }

  if (!dashboard || (dashboard.teams.length === 0 && dashboard.players.length === 0)) {
    return (
      <div className="favorites-dashboard">
        <EmptyFavoritesState type="both" />
      </div>
    );
  }

  return (
    <div className="favorites-dashboard">
      {dashboard.teams.length > 0 && (
        <section className="dashboard-section">
          <div className="section-header">
            <h2>My Teams</h2>
            {dashboard.hasMoreTeams && (
              <Link to="/account" className="view-all-link">
                View all {dashboard.totalTeamCount}
              </Link>
            )}
          </div>
          <div className="favorite-teams-grid">
            {dashboard.teams.map((teamData) => (
              <FavoriteTeamCard key={teamData.team.id} data={teamData} />
            ))}
          </div>
        </section>
      )}

      {dashboard.players.length > 0 && (
        <section className="dashboard-section">
          <div className="section-header">
            <h2>My Players</h2>
            {dashboard.hasMorePlayers && (
              <Link to="/account" className="view-all-link">
                View all {dashboard.totalPlayerCount}
              </Link>
            )}
          </div>
          <div className="favorite-players-grid">
            {dashboard.players.map((playerData) => (
              <FavoritePlayerCard key={playerData.player.id} data={playerData} />
            ))}
          </div>
        </section>
      )}

      {dashboard.teams.length === 0 && (
        <section className="dashboard-section">
          <EmptyFavoritesState type="teams" />
        </section>
      )}

      {dashboard.players.length === 0 && (
        <section className="dashboard-section">
          <EmptyFavoritesState type="players" />
        </section>
      )}
    </div>
  );
}

export default FavoritesDashboard;
