import { Link } from 'react-router-dom';
import { Team } from '../../types/team';

interface TeamCardProps {
  team: Team;
}

function TeamCard({ team }: TeamCardProps) {
  return (
    <Link to={`/teams/${team.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <div className="team-card">
        <div className="team-abbr">{team.abbreviation}</div>
        <div className="team-info">
          <h3>{team.name}</h3>
          <p>{team.league} - {team.division}</p>
        </div>
      </div>
    </Link>
  );
}

export default TeamCard;
