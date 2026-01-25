import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Team } from '../types/team';
import { Player } from '../types/player';
import { getFavoriteTeams, getFavoritePlayers } from '../services/api';
import './AccountPage.css';

type Tab = 'teams' | 'players';

function AccountPage() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<Tab>('teams');
  const [favoriteTeams, setFavoriteTeams] = useState<Team[]>([]);
  const [favoritePlayers, setFavoritePlayers] = useState<Player[]>([]);
  const [teamsLoading, setTeamsLoading] = useState(true);
  const [playersLoading, setPlayersLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadFavoriteTeams();
    loadFavoritePlayers();
  }, []);

  const loadFavoriteTeams = async () => {
    try {
      const teams = await getFavoriteTeams();
      setFavoriteTeams(teams);
    } catch (err) {
      setError('Failed to load favorite teams');
    } finally {
      setTeamsLoading(false);
    }
  };

  const loadFavoritePlayers = async () => {
    try {
      const players = await getFavoritePlayers();
      setFavoritePlayers(players);
    } catch (err) {
      setError('Failed to load favorite players');
    } finally {
      setPlayersLoading(false);
    }
  };

  return (
    <div className="account-page">
      <div className="account-header card">
        <div className="account-profile">
          {user?.picture && (
            <img
              src={user.picture}
              alt=""
              className="account-avatar"
            />
          )}
          <div className="account-info">
            <h1>{user?.name || 'User'}</h1>
            <p className="account-email">{user?.email}</p>
          </div>
        </div>
      </div>

      <div className="favorites-section">
        <h2>My Favorites</h2>

        <div className="favorites-tabs">
          <button
            className={`tab-button ${activeTab === 'teams' ? 'active' : ''}`}
            onClick={() => setActiveTab('teams')}
          >
            Teams ({favoriteTeams.length})
          </button>
          <button
            className={`tab-button ${activeTab === 'players' ? 'active' : ''}`}
            onClick={() => setActiveTab('players')}
          >
            Players ({favoritePlayers.length})
          </button>
        </div>

        {error && <p className="error">{error}</p>}

        <div className="favorites-content">
          {activeTab === 'teams' && (
            <div className="favorites-list">
              {teamsLoading ? (
                <p className="loading-text">Loading teams...</p>
              ) : favoriteTeams.length === 0 ? (
                <div className="empty-state">
                  <p>You haven't added any favorite teams yet.</p>
                  <Link to="/teams" className="browse-link">Browse teams</Link>
                </div>
              ) : (
                <div className="favorites-grid">
                  {favoriteTeams.map((team) => (
                    <Link
                      key={team.id}
                      to={`/teams/${team.id}`}
                      className="favorite-card"
                    >
                      {team.logoUrl ? (
                        <img
                          src={team.logoUrl}
                          alt={`${team.name} logo`}
                          className="favorite-logo"
                        />
                      ) : (
                        <div className="favorite-abbrev">{team.abbreviation}</div>
                      )}
                      <div className="favorite-name">{team.name}</div>
                      <div className="favorite-meta">
                        {team.league} - {team.division}
                      </div>
                    </Link>
                  ))}
                </div>
              )}
            </div>
          )}

          {activeTab === 'players' && (
            <div className="favorites-list">
              {playersLoading ? (
                <p className="loading-text">Loading players...</p>
              ) : favoritePlayers.length === 0 ? (
                <div className="empty-state">
                  <p>You haven't added any favorite players yet.</p>
                  <Link to="/players" className="browse-link">Browse players</Link>
                </div>
              ) : (
                <div className="favorites-grid">
                  {favoritePlayers.map((player) => (
                    <Link
                      key={player.id}
                      to={`/players/${player.id}`}
                      className="favorite-card"
                    >
                      {player.headshotUrl ? (
                        <img
                          src={player.headshotUrl}
                          alt={player.fullName}
                          className="favorite-headshot"
                        />
                      ) : (
                        <div className="favorite-number">{player.jerseyNumber || '?'}</div>
                      )}
                      <div className="favorite-name">{player.fullName}</div>
                      <div className="favorite-meta">
                        {player.position || 'Position unknown'}
                      </div>
                    </Link>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default AccountPage;
