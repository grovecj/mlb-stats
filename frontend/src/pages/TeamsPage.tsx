import { useState, useEffect } from 'react';
import { Team } from '../types/team';
import { getTeams } from '../services/api';
import TeamCard from '../components/team/TeamCard';

function TeamsPage() {
  const [teams, setTeams] = useState<Team[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchTeams() {
      try {
        const data = await getTeams();
        setTeams(data);
      } catch (err) {
        setError('Failed to load teams');
      } finally {
        setLoading(false);
      }
    }
    fetchTeams();
  }, []);

  if (loading) return <div className="loading">Loading teams...</div>;
  if (error) return <div className="error">{error}</div>;

  // Group teams by league and division
  const grouped = teams.reduce((acc, team) => {
    const key = `${team.league} - ${team.division}`;
    if (!acc[key]) acc[key] = [];
    acc[key].push(team);
    return acc;
  }, {} as Record<string, Team[]>);

  const divisions = [
    'American League - East',
    'American League - Central',
    'American League - West',
    'National League - East',
    'National League - Central',
    'National League - West',
  ];

  return (
    <div>
      <h1 className="page-title">MLB Teams</h1>

      {divisions.map((division) => {
        const divisionTeams = grouped[division] || [];
        if (divisionTeams.length === 0) return null;

        return (
          <div key={division}>
            <h2 className="division-header">{division}</h2>
            <div className="grid grid-3">
              {divisionTeams.map((team) => (
                <TeamCard key={team.id} team={team} />
              ))}
            </div>
          </div>
        );
      })}

      {teams.length === 0 && (
        <p>No teams found. Try syncing data from the MLB API.</p>
      )}
    </div>
  );
}

export default TeamsPage;
