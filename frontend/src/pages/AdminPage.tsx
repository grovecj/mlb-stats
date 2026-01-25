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
  triggerIncompletePlayersSync,
  getUsers,
  updateUserRole,
  AdminUser,
} from '../services/api';
import './AdminPage.css';

type Tab = 'sync' | 'users';

function AdminPage() {
  const { isAdmin, isOwner } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<Tab>('sync');
  const [syncStatus, setSyncStatus] = useState<Record<string, string>>({});
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [usersError, setUsersError] = useState<string | null>(null);

  useEffect(() => {
    if (!isAdmin) {
      navigate('/');
    }
  }, [isAdmin, navigate]);

  useEffect(() => {
    if (activeTab === 'users' && isOwner) {
      loadUsers();
    }
  }, [activeTab, isOwner]);

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
                label="Incomplete Players"
                status={syncStatus['incompletePlayers']}
                onClick={() => handleSync(triggerIncompletePlayersSync, 'incompletePlayers')}
              />
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
