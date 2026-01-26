import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import {
  triggerFullSync,
  triggerTeamsSync,
  triggerRostersSync,
  triggerGamesSync,
  triggerStatsSync,
  triggerStandingsSync,
  triggerBoxScoresSync,
  triggerIncompletePlayersSync,
  getUsers,
  updateUserRole,
  AdminUser,
  getSyncedSeasons,
  getAvailableSeasons,
  deleteSeasonData,
  SeasonData,
} from '../services/api';
import './AdminPage.css';

type Tab = 'sync' | 'data' | 'users';

function AdminPage() {
  const { isAdmin, isOwner } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<Tab>('sync');
  const [syncStatus, setSyncStatus] = useState<Record<string, string>>({});
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [usersError, setUsersError] = useState<string | null>(null);
  const [seasons, setSeasons] = useState<SeasonData[]>([]);
  const [availableSeasons, setAvailableSeasons] = useState<number[]>([]);
  const [seasonsLoading, setSeasonsLoading] = useState(false);
  const [seasonsError, setSeasonsError] = useState<string | null>(null);
  const [selectedSeason, setSelectedSeason] = useState<number | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);

  useEffect(() => {
    if (!isAdmin) {
      navigate('/');
    }
  }, [isAdmin, navigate]);

  useEffect(() => {
    if (activeTab === 'users' && isOwner) {
      loadUsers();
    }
    if (activeTab === 'data') {
      loadSeasons();
    }
  }, [activeTab, isOwner]);

  const loadSeasons = async () => {
    setSeasonsLoading(true);
    setSeasonsError(null);
    try {
      const [syncedData, availableData] = await Promise.all([
        getSyncedSeasons(),
        getAvailableSeasons(),
      ]);
      setSeasons(syncedData);
      setAvailableSeasons(availableData);
    } catch (error) {
      setSeasonsError(error instanceof Error ? error.message : 'Failed to load seasons');
    } finally {
      setSeasonsLoading(false);
    }
  };

  const handleSyncSeason = async (season: number) => {
    setSyncStatus((prev) => ({ ...prev, [`season-${season}`]: 'running' }));
    try {
      await triggerFullSync(season);
      setSyncStatus((prev) => ({ ...prev, [`season-${season}`]: 'completed' }));
      loadSeasons();
    } catch (error) {
      setSyncStatus((prev) => ({
        ...prev,
        [`season-${season}`]: error instanceof Error ? error.message : 'failed',
      }));
    }
  };

  const handleDeleteSeason = async (season: number) => {
    try {
      await deleteSeasonData(season);
      setDeleteConfirm(null);
      loadSeasons();
    } catch (error) {
      alert(error instanceof Error ? error.message : 'Failed to delete season');
    }
  };

  const loadUsers = async () => {
    setUsersLoading(true);
    setUsersError(null);
    try {
      const data = await getUsers();
      setUsers(data);
    } catch (error) {
      setUsersError(error instanceof Error ? error.message : 'Failed to load users');
    } finally {
      setUsersLoading(false);
    }
  };

  const handleSync = async (
    syncFn: (season?: number) => Promise<{ status: string }>,
    key: string
  ) => {
    setSyncStatus((prev) => ({ ...prev, [key]: 'running' }));
    try {
      await syncFn();
      setSyncStatus((prev) => ({ ...prev, [key]: 'completed' }));
    } catch (error) {
      setSyncStatus((prev) => ({
        ...prev,
        [key]: error instanceof Error ? error.message : 'failed',
      }));
    }
  };

  const handleRoleChange = async (userId: number, newRole: 'USER' | 'ADMIN') => {
    try {
      await updateUserRole(userId, newRole);
      setUsers((prev) =>
        prev.map((user) =>
          user.id === userId ? { ...user, role: newRole } : user
        )
      );
    } catch (error) {
      alert(error instanceof Error ? error.message : 'Failed to update role');
    }
  };

  const formatDate = (dateStr: string | null) => {
    if (!dateStr) return 'Never';
    return new Date(dateStr).toLocaleString();
  };

  if (!isAdmin) {
    return null;
  }

  return (
    <div className="admin-page">
      <h1>Admin Dashboard</h1>

      <div className="admin-tabs">
        <button
          className={`tab-button ${activeTab === 'sync' ? 'active' : ''}`}
          onClick={() => setActiveTab('sync')}
        >
          Data Sync
        </button>
        <button
          className={`tab-button ${activeTab === 'data' ? 'active' : ''}`}
          onClick={() => setActiveTab('data')}
        >
          Data Manager
        </button>
        {isOwner && (
          <button
            className={`tab-button ${activeTab === 'users' ? 'active' : ''}`}
            onClick={() => setActiveTab('users')}
          >
            User Management
          </button>
        )}
      </div>

      <div className="tab-content">
        {activeTab === 'sync' && (
          <div className="sync-panel">
            <h2>Data Synchronization</h2>
            <p className="sync-description">
              Trigger data synchronization from the MLB API. Full sync will update
              all data; individual syncs will update specific data types.
            </p>

            <div className="sync-buttons">
              <SyncButton
                label="Full Sync"
                status={syncStatus['full']}
                onClick={() => handleSync(triggerFullSync, 'full')}
              />
              <SyncButton
                label="Teams"
                status={syncStatus['teams']}
                onClick={() => handleSync(triggerTeamsSync, 'teams')}
              />
              <SyncButton
                label="Rosters"
                status={syncStatus['rosters']}
                onClick={() => handleSync(triggerRostersSync, 'rosters')}
              />
              <SyncButton
                label="Games"
                status={syncStatus['games']}
                onClick={() => handleSync(triggerGamesSync, 'games')}
              />
              <SyncButton
                label="Stats"
                status={syncStatus['stats']}
                onClick={() => handleSync(triggerStatsSync, 'stats')}
              />
              <SyncButton
                label="Standings"
                status={syncStatus['standings']}
                onClick={() => handleSync(triggerStandingsSync, 'standings')}
              />
              <SyncButton
                label="Box Scores"
                status={syncStatus['boxscores']}
                onClick={() => handleSync(triggerBoxScoresSync, 'boxscores')}
              />
              <SyncButton
                label="Incomplete Players"
                status={syncStatus['incompletePlayers']}
                onClick={() => handleSync(triggerIncompletePlayersSync, 'incompletePlayers')}
              />
            </div>
          </div>
        )}

        {activeTab === 'data' && (
          <div className="data-panel">
            <h2>Season Data Manager</h2>
            <p className="sync-description">
              Manage synced seasons. You can sync new seasons or delete old season data.
            </p>

            {seasonsLoading && <p>Loading seasons...</p>}
            {seasonsError && <p className="error">{seasonsError}</p>}

            {!seasonsLoading && !seasonsError && (
              <>
                <h3>Synced Seasons</h3>
                {seasons.length === 0 ? (
                  <p>No seasons synced yet. Use the selector below to sync a season.</p>
                ) : (
                  <table className="seasons-table">
                    <thead>
                      <tr>
                        <th>Season</th>
                        <th>Games</th>
                        <th>Batting Stats</th>
                        <th>Pitching Stats</th>
                        <th>Roster Entries</th>
                        <th>Standings</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {seasons.map((season) => (
                        <tr key={season.season}>
                          <td>
                            {season.season}
                            {season.isCurrent && (
                              <span className="current-badge">Current</span>
                            )}
                          </td>
                          <td>{season.gamesCount.toLocaleString()}</td>
                          <td>{season.battingStatsCount.toLocaleString()}</td>
                          <td>{season.pitchingStatsCount.toLocaleString()}</td>
                          <td>{season.rosterEntriesCount.toLocaleString()}</td>
                          <td>{season.standingsCount.toLocaleString()}</td>
                          <td>
                            <button
                              className="sync-button small"
                              onClick={() => handleSyncSeason(season.season)}
                              disabled={syncStatus[`season-${season.season}`] === 'running'}
                            >
                              {syncStatus[`season-${season.season}`] === 'running'
                                ? 'Syncing...'
                                : 'Re-sync'}
                            </button>
                            {!season.isCurrent && (
                              <>
                                {deleteConfirm === season.season ? (
                                  <>
                                    <button
                                      className="delete-button confirm"
                                      onClick={() => handleDeleteSeason(season.season)}
                                    >
                                      Confirm
                                    </button>
                                    <button
                                      className="cancel-button"
                                      onClick={() => setDeleteConfirm(null)}
                                    >
                                      Cancel
                                    </button>
                                  </>
                                ) : (
                                  <button
                                    className="delete-button"
                                    onClick={() => setDeleteConfirm(season.season)}
                                  >
                                    Delete
                                  </button>
                                )}
                              </>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}

                <h3>Sync New Season</h3>
                <div className="sync-new-season">
                  <select
                    value={selectedSeason || ''}
                    onChange={(e) => setSelectedSeason(Number(e.target.value) || null)}
                  >
                    <option value="">Select a season...</option>
                    {availableSeasons
                      .filter((s) => !seasons.some((ss) => ss.season === s))
                      .map((season) => (
                        <option key={season} value={season}>
                          {season}
                        </option>
                      ))}
                  </select>
                  <button
                    className="sync-button"
                    onClick={() => selectedSeason && handleSyncSeason(selectedSeason)}
                    disabled={!selectedSeason || syncStatus[`season-${selectedSeason}`] === 'running'}
                  >
                    {selectedSeason && syncStatus[`season-${selectedSeason}`] === 'running'
                      ? 'Syncing...'
                      : 'Sync Season'}
                  </button>
                </div>
              </>
            )}
          </div>
        )}

        {activeTab === 'users' && isOwner && (
          <div className="users-panel">
            <h2>User Management</h2>

            {usersLoading && <p>Loading users...</p>}
            {usersError && <p className="error">{usersError}</p>}

            {!usersLoading && !usersError && (
              <table className="users-table">
                <thead>
                  <tr>
                    <th>User</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Last Login</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user) => (
                    <tr key={user.id}>
                      <td className="user-cell">
                        {user.pictureUrl && (
                          <img
                            src={user.pictureUrl}
                            alt=""
                            className="user-avatar"
                          />
                        )}
                        <span>{user.name || 'Unknown'}</span>
                      </td>
                      <td>{user.email}</td>
                      <td>
                        {user.role === 'OWNER' ? (
                          <span className="role-badge owner">OWNER</span>
                        ) : (
                          <select
                            value={user.role}
                            onChange={(e) =>
                              handleRoleChange(
                                user.id,
                                e.target.value as 'USER' | 'ADMIN'
                              )
                            }
                            className="role-select"
                          >
                            <option value="USER">USER</option>
                            <option value="ADMIN">ADMIN</option>
                          </select>
                        )}
                      </td>
                      <td>{formatDate(user.lastLoginAt)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

interface SyncButtonProps {
  label: string;
  status?: string;
  onClick: () => void;
}

function SyncButton({ label, status, onClick }: SyncButtonProps) {
  const isRunning = status === 'running';
  const isCompleted = status === 'completed';
  const isFailed = status && status !== 'running' && status !== 'completed';

  return (
    <button
      className={`sync-button ${isCompleted ? 'completed' : ''} ${isFailed ? 'failed' : ''}`}
      onClick={onClick}
      disabled={isRunning}
    >
      {isRunning ? 'Running...' : label}
      {isCompleted && <span className="status-icon">&#x2713;</span>}
      {isFailed && <span className="status-icon">&#x2717;</span>}
    </button>
  );
}

export default AdminPage;
