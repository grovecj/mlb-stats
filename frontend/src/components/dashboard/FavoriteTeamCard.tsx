import { Link } from 'react-router-dom';
import { FavoriteTeamDashboard } from '../../types/dashboard';
import GameMiniCard from './GameMiniCard';

interface FavoriteTeamCardProps {
  data: FavoriteTeamDashboard;
}

function FavoriteTeamCard({ data }: FavoriteTeamCardProps) {
  const { team, todaysGame, nextGame, standing } = data;

  return (
    <div className="favorite-team-card">
      <Link to={`/teams/${team.id}`} className="favorite-team-header">
        {team.logoUrl && (
          <img
            src={team.logoUrl}
            alt={team.name}
            className="favorite-team-logo"
          />
        )}
        <div className="favorite-team-info">
          <h3 className="favorite-team-name">{team.name}</h3>
          {standing && (
            <div className="favorite-team-record">
              <span className="record">{standing.wins}-{standing.losses}</span>
              {standing.divisionRank && (
                <span className="rank">{getOrdinal(standing.divisionRank)} in {team.division}</span>
              )}
              {standing.streakCode && (
                <span className={`streak ${standing.streakCode.startsWith('W') ? 'winning' : 'losing'}`}>
                  {standing.streakCode}
                </span>
              )}
            </div>
          )}
        </div>
      </Link>

      <div className="favorite-team-games">
        {todaysGame ? (
          <GameMiniCard game={todaysGame} label="Today" />
        ) : nextGame ? (
          <GameMiniCard game={nextGame} label="Next" />
        ) : (
          <div className="no-game">No upcoming games</div>
        )}
      </div>
    </div>
  );
}

function getOrdinal(n: number): string {
  const s = ['th', 'st', 'nd', 'rd'];
  const v = n % 100;
  return n + (s[(v - 20) % 10] || s[v] || s[0]);
}

export default FavoriteTeamCard;
