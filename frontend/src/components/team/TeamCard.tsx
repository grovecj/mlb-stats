import { Link } from 'react-router-dom';
import { Team } from '../../types/team';

interface TeamCardProps {
  team: Team;
}

function TeamCard({ team }: TeamCardProps) {
  return (
    <Link to={`/teams/${team.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <div className="team-card">
        {team.logoUrl ? (
          <img
            src={team.logoUrl}
            alt={`${team.name} logo`}
            className="team-logo"
            style={{ width: '48px', height: '48px', objectFit: 'contain' }}
          />
        ) : (
          <div className="team-abbr">{team.abbreviation}</div>
        )}
        <div className="team-info">
          <h3>{team.name}</h3>
          <p>{team.league} - {team.division}</p>
        </div>
      </div>
    </Link>
  );
}

export default TeamCard;
