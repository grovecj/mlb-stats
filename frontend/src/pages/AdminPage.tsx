import { useState, useEffect, useCallback } from 'react';
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
  getUsers,
  updateUserRole,
  AdminUser,
  getSyncedSeasons,
  getAvailableSeasons,
  deleteSeasonData,
  SeasonData,
  getDataFreshness,
  getActiveSyncJobs,
  getRecentSyncJobs,
  DataFreshness,
  SyncJob,
  SyncJobType,
} from '../services/api';
import DataFreshnessCard from '../components/sync/DataFreshnessCard';
import SyncProgressCard from '../components/sync/SyncProgressCard';
import SyncHistoryTable from '../components/sync/SyncHistoryTable';
import './AdminPage.css';

type Tab = 'sync' | 'users';

function AdminPage() {
  const { isAdmin, isOwner } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<Tab>('sync');

  // Sync state
  const [freshness, setFreshness] = useState<DataFreshness[]>([]);
  const [activeJobs, setActiveJobs] = useState<SyncJob[]>([]);
  const [recentJobs, setRecentJobs] = useState<SyncJob[]>([]);
  const [syncLoading, setSyncLoading] = useState(false);
  const [syncingTypes, setSyncingTypes] = useState<Set<SyncJobType>>(new Set());

  // Season data state
  const [seasons, setSeasons] = useState<SeasonData[]>([]);
  const [availableSeasons, setAvailableSeasons] = useState<number[]>([]);
  const [selectedSeason, setSelectedSeason] = useState<number | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);

  // Users state
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [usersError, setUsersError] = useState<string | null>(null);

  useEffect(() => {
    if (!isAdmin) {
      navigate('/');
    }
  }, [isAdmin, navigate]);

  const loadSyncData = useCallback(async () => {
    setSyncLoading(true);
    try {
      const [freshnessData, activeJobsData, recentJobsData, syncedSeasons, availableSeasonsData] =
        await Promise.all([
          getDataFreshness(),
          getActiveSyncJobs(),
          getRecentSyncJobs(10),
          getSyncedSeasons(),
          getAvailableSeasons(),
        ]);
      setFreshness(freshnessData);
      setActiveJobs(activeJobsData);
      setRecentJobs(recentJobsData);
      setSeasons(syncedSeasons);
      setAvailableSeasons(availableSeasonsData);
    } catch (error) {
      console.error('Failed to load sync data:', error);
    } finally {
      setSyncLoading(false);
    }
  }, []);

  useEffect(() => {
    if (activeTab === 'sync') {
      loadSyncData();
    }
    if (activeTab === 'users' && isOwner) {
      loadUsers();
    }
  }, [activeTab, isOwner, loadSyncData]);

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

  const handleSyncByType = async (type: SyncJobType) => {
    setSyncingTypes(prev => new Set(prev).add(type));
    try {
      let job: SyncJob;
      switch (type) {
        case 'FULL_SYNC':
          job = await triggerFullSync();
          break;
        case 'TEAMS':
          job = await triggerTeamsSync();
          break;
        case 'ROSTERS':
          job = await triggerRostersSync();
          break;
        case 'GAMES':
          job = await triggerGamesSync();
          break;
        case 'STATS':
          job = await triggerStatsSync();
          break;
        case 'STANDINGS':
          job = await triggerStandingsSync();
          break;
        case 'BOX_SCORES':
          job = await triggerBoxScoresSync();
          break;
        default:
          return;
      }
      setActiveJobs(prev => [...prev, job]);
    } catch (error) {
      console.error('Failed to start sync:', error);
      alert(error instanceof Error ? error.message : 'Failed to start sync');
    } finally {
      setSyncingTypes(prev => {
        const next = new Set(prev);
        next.delete(type);
        return next;
      });
    }
  };

  const handleSyncSeason = async (season: number) => {
    setSyncingTypes(prev => new Set(prev).add('FULL_SYNC'));
    try {
      const job = await triggerFullSync(season);
      setActiveJobs(prev => [...prev, job]);
    } catch (error) {
      console.error('Failed to start season sync:', error);
      alert(error instanceof Error ? error.message : 'Failed to start season sync');
    } finally {
      setSyncingTypes(prev => {
        const next = new Set(prev);
        next.delete('FULL_SYNC');
        return next;
      });
    }
  };

  const handleJobComplete = useCallback((completedJob: SyncJob) => {
    setActiveJobs(prev => prev.filter(j => j.id !== completedJob.id));
    setRecentJobs(prev => [completedJob, ...prev.slice(0, 9)]);
    // Refresh freshness data
    getDataFreshness().then(setFreshness).catch(console.error);
    getSyncedSeasons().then(setSeasons).catch(console.error);
  }, []);

  const handleDeleteSeason = async (season: number) => {
    try {
      await deleteSeasonData(season);
      setDeleteConfirm(null);
      const [syncedSeasons, availableSeasonsData] = await Promise.all([
        getSyncedSeasons(),
        getAvailableSeasons(),
      ]);
      setSeasons(syncedSeasons);
      setAvailableSeasons(availableSeasonsData);
    } catch (error) {
      alert(error instanceof Error ? error.message : 'Failed to delete season');
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
            {/* Active Syncs */}
            {activeJobs.length > 0 && (
              <div className="active-syncs-section">
                <h3>Active Syncs</h3>
                {activeJobs.map(job => (
                  <SyncProgressCard
                    key={job.id}
                    job={job}
                    onComplete={handleJobComplete}
                  />
                ))}
              </div>
            )}

            {/* Full Sync Button */}
            <div className="full-sync-section">
              <button
                className="sync-button full-sync-btn"
                onClick={() => handleSyncByType('FULL_SYNC')}
                disabled={syncingTypes.has('FULL_SYNC') || activeJobs.length > 0}
              >
                {syncingTypes.has('FULL_SYNC') ? 'Starting...' : 'Run Full Sync'}
              </button>
              <p className="sync-description">
                Sync all data types for the current season. Individual data types can be synced using the cards below.
              </p>
            </div>

            {/* Data Freshness Grid */}
            <div className="section">
              <h3>Data Freshness</h3>
              {syncLoading ? (
                <p>Loading...</p>
              ) : (
                <div className="freshness-grid">
                  {freshness
                    .filter(f => f.type !== 'FULL_SYNC')
                    .map(f => (
                      <DataFreshnessCard
                        key={f.type}
                        freshness={f}
                        onSync={handleSyncByType}
                        isSyncing={syncingTypes.has(f.type) || activeJobs.some(j => j.jobType === f.type)}
                      />
                    ))}
                </div>
              )}
            </div>

            {/* Season Data Overview */}
            <div className="section">
              <h3>Season Data</h3>
              {syncLoading ? (
                <p>Loading seasons...</p>
              ) : seasons.length === 0 ? (
                <p>No seasons synced yet. Use the selector below to sync a season.</p>
              ) : (
                <table className="seasons-table">
                  <thead>
                    <tr>
                      <th>Season</th>
                      <th>Games</th>
                      <th>Batting</th>
                      <th>Pitching</th>
                      <th>Rosters</th>
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
                            disabled={syncingTypes.has('FULL_SYNC') || activeJobs.length > 0}
                          >
                            Re-sync
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

              {/* Sync New Season */}
              <div className="sync-new-season">
                <select
                  value={selectedSeason || ''}
                  onChange={(e) => setSelectedSeason(Number(e.target.value) || null)}
                >
                  <option value="">Select a season to sync...</option>
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
                  disabled={!selectedSeason || syncingTypes.has('FULL_SYNC') || activeJobs.length > 0}
                >
                  Sync Season
                </button>
              </div>
            </div>

            {/* Sync History */}
            <div className="section">
              <h3>Sync History</h3>
              <SyncHistoryTable jobs={recentJobs} />
            </div>
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

export default AdminPage;
