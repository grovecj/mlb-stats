import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Team, RosterEntry } from '../types/team';
import { Game } from '../types/game';
import { BattingStats } from '../types/stats';
import { getTeam, getTeamRoster, getTeamGames, getTeamStats } from '../services/api';
import TeamRoster from '../components/team/TeamRoster';
import TeamStats from '../components/team/TeamStats';
import GameSchedule from '../components/game/GameSchedule';

function TeamDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [team, setTeam] = useState<Team | null>(null);
  const [roster, setRoster] = useState<RosterEntry[]>([]);
  const [games, setGames] = useState<Game[]>([]);
  const [stats, setStats] = useState<BattingStats[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'roster' | 'stats' | 'games'>('roster');

  useEffect(() => {
    async function fetchData() {
      if (!id) return;
      try {
        const teamId = parseInt(id);
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
      } catch (err) {
        setError('Failed to load team data');
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, [id]);

  if (loading) return <div className="loading">Loading team...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!team) return <div className="error">Team not found</div>;

  const recentGames = games.slice(0, 10);

  return (
    <div>
      <div className="card" style={{ marginBottom: '24px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div style={{ fontSize: '48px', fontWeight: 'bold', color: '#002d72' }}>
            {team.abbreviation}
          </div>
          <div>
            <h1 style={{ margin: 0, fontSize: '28px' }}>{team.name}</h1>
            <p style={{ margin: '4px 0 0', color: '#666' }}>
              {team.league} - {team.division} | {team.venueName}
            </p>
          </div>
        </div>
      </div>

      <div style={{ marginBottom: '16px' }}>
        <button
          onClick={() => setActiveTab('roster')}
          style={{
            padding: '8px 16px',
            marginRight: '8px',
            backgroundColor: activeTab === 'roster' ? '#002d72' : '#fff',
            color: activeTab === 'roster' ? '#fff' : '#333',
            border: '1px solid #002d72',
            borderRadius: '4px',
            cursor: 'pointer',
          }}
        >
          Roster ({roster.length})
        </button>
        <button
          onClick={() => setActiveTab('stats')}
          style={{
            padding: '8px 16px',
            marginRight: '8px',
            backgroundColor: activeTab === 'stats' ? '#002d72' : '#fff',
            color: activeTab === 'stats' ? '#fff' : '#333',
            border: '1px solid #002d72',
            borderRadius: '4px',
            cursor: 'pointer',
          }}
        >
          Stats
        </button>
        <button
          onClick={() => setActiveTab('games')}
          style={{
            padding: '8px 16px',
            backgroundColor: activeTab === 'games' ? '#002d72' : '#fff',
            color: activeTab === 'games' ? '#fff' : '#333',
            border: '1px solid #002d72',
            borderRadius: '4px',
            cursor: 'pointer',
          }}
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
