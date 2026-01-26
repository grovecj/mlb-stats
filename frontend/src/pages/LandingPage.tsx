import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { getPublicStats, PublicStats } from '../services/api';
import './LandingPage.css';

function LandingPage() {
  const { login } = useAuth();
  const [stats, setStats] = useState<PublicStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchStats() {
      try {
        const data = await getPublicStats();
        setStats(data);
      } catch (err) {
        // Stats are optional, continue without them
        console.error('Failed to load stats:', err);
      } finally {
        setLoading(false);
      }
    }
    fetchStats();
  }, []);

  const formatNumber = (num: number): string => {
    return num.toLocaleString();
  };

  const formatDate = (dateStr: string | null): string => {
    if (!dateStr) return 'N/A';
    // Append time component to parse as local time, not UTC
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  };

  return (
    <div className="landing-page">
      <section className="landing-hero">
        <h1>MLB Stats Tracker</h1>
        <p className="tagline">Your source for baseball statistics</p>
      </section>

      <section className="landing-stats">
        {loading ? (
          <div className="landing-loading">Loading stats...</div>
        ) : stats && (stats.teamCount > 0 || stats.playerCount > 0 || stats.gameCount > 0) ? (
          <div className="landing-stats-grid">
            <div className="landing-stat-card">
              <div className="landing-stat-value">{formatNumber(stats.teamCount)}</div>
              <div className="landing-stat-label">Teams</div>
            </div>
            <div className="landing-stat-card">
              <div className="landing-stat-value">{formatNumber(stats.playerCount)}</div>
              <div className="landing-stat-label">Players</div>
            </div>
            <div className="landing-stat-card">
              <div className="landing-stat-value">{formatNumber(stats.gameCount)}</div>
              <div className="landing-stat-label">Games</div>
            </div>
          </div>
        ) : null}
      </section>

      <section className="landing-cta">
        <div className="landing-cta-content">
          <p>
            Track your favorite teams and players, view detailed statistics,
            and follow the season in real-time.
          </p>
          <button className="google-login-btn" onClick={login}>
            <svg viewBox="0 0 24 24" width="24" height="24">
              <path
                fill="#4285F4"
                d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
              />
              <path
                fill="#34A853"
                d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
              />
              <path
                fill="#FBBC05"
                d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
              />
              <path
                fill="#EA4335"
                d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
              />
            </svg>
            Sign in with Google
          </button>
        </div>
      </section>

      <section className="landing-features">
        <div className="landing-features-content">
          <h2>Features</h2>
          <div className="landing-features-grid">
            <div className="landing-feature-item">
              <span className="landing-feature-icon">&#9918;</span>
              <div>
                <h3>Team Rosters & Standings</h3>
                <p>View complete rosters and current division standings</p>
              </div>
            </div>
            <div className="landing-feature-item">
              <span className="landing-feature-icon">&#9917;</span>
              <div>
                <h3>Player Statistics</h3>
                <p>Detailed batting and pitching stats for every player</p>
              </div>
            </div>
            <div className="landing-feature-item">
              <span className="landing-feature-icon">&#128197;</span>
              <div>
                <h3>Game Schedules & Scores</h3>
                <p>Browse games and view detailed box scores</p>
              </div>
            </div>
            <div className="landing-feature-item">
              <span className="landing-feature-icon">&#11088;</span>
              <div>
                <h3>Personal Favorites</h3>
                <p>Save your favorite teams and players for quick access</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <footer className="landing-footer">
        {stats?.lastUpdated && (
          <p>Data last updated: {formatDate(stats.lastUpdated)}</p>
        )}
      </footer>
    </div>
  );
}

export default LandingPage;
