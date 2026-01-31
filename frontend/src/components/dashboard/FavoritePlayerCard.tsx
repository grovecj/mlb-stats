import { Link } from 'react-router-dom';
import { FavoritePlayerDashboard } from '../../types/dashboard';

interface FavoritePlayerCardProps {
  data: FavoritePlayerDashboard;
}

function formatAvg(value: number | null): string {
  if (value === null) return '-';
  return value.toFixed(3).replace(/^0/, '');
}

function formatEra(value: number | null): string {
  if (value === null) return '-';
  return value.toFixed(2);
}

function FavoritePlayerCard({ data }: FavoritePlayerCardProps) {
  const { player, currentTeam, playerType, lastBattingGame, lastPitchingGame, seasonBatting, seasonPitching } = data;
  const isPitcher = playerType === 'PITCHER';

  return (
    <div className="favorite-player-card">
      <Link to={`/players/${player.id}`} className="favorite-player-header">
        {player.headshotUrl ? (
          <img
            src={player.headshotUrl}
            alt={player.fullName}
            className="favorite-player-headshot"
          />
        ) : (
          <div className="favorite-player-placeholder">
            {player.jerseyNumber || '?'}
          </div>
        )}
        <div className="favorite-player-info">
          <h3 className="favorite-player-name">{player.fullName}</h3>
          <div className="favorite-player-details">
            <span className="position">{player.position}</span>
            {currentTeam && <span className="team">{currentTeam.abbreviation}</span>}
          </div>
        </div>
      </Link>

      <div className="favorite-player-stats">
        {isPitcher ? (
          <PitcherStats lastGame={lastPitchingGame} season={seasonPitching} />
        ) : (
          <BatterStats lastGame={lastBattingGame} season={seasonBatting} />
        )}
      </div>
    </div>
  );
}

function BatterStats({ lastGame, season }: {
  lastGame: FavoritePlayerDashboard['lastBattingGame'];
  season: FavoritePlayerDashboard['seasonBatting'];
}) {
  return (
    <>
      {lastGame && (
        <div className="last-game-stats">
          <div className="stats-label">Last Game {lastGame.opponent && `vs ${lastGame.opponent}`}</div>
          <div className="stats-row">
            <span>{lastGame.hits ?? 0}-{lastGame.atBats ?? 0}</span>
            {(lastGame.homeRuns ?? 0) > 0 && <span className="highlight">{lastGame.homeRuns} HR</span>}
            {(lastGame.rbi ?? 0) > 0 && <span>{lastGame.rbi} RBI</span>}
          </div>
        </div>
      )}
      {season && (
        <div className="season-stats">
          <div className="stats-label">{season.season} Season</div>
          <div className="stats-row">
            <span className="stat-item">
              <span className="stat-value">{formatAvg(season.battingAvg)}</span>
              <span className="stat-label">AVG</span>
            </span>
            <span className="stat-item">
              <span className="stat-value">{season.homeRuns ?? 0}</span>
              <span className="stat-label">HR</span>
            </span>
            <span className="stat-item">
              <span className="stat-value">{season.rbi ?? 0}</span>
              <span className="stat-label">RBI</span>
            </span>
            <span className="stat-item">
              <span className="stat-value">{formatAvg(season.ops)}</span>
              <span className="stat-label">OPS</span>
            </span>
          </div>
        </div>
      )}
      {!lastGame && !season && (
        <div className="no-stats">No stats available</div>
      )}
    </>
  );
}

function PitcherStats({ lastGame, season }: {
  lastGame: FavoritePlayerDashboard['lastPitchingGame'];
  season: FavoritePlayerDashboard['seasonPitching'];
}) {
  return (
    <>
      {lastGame && (
        <div className="last-game-stats">
          <div className="stats-label">Last Game {lastGame.opponent && `vs ${lastGame.opponent}`}</div>
          <div className="stats-row">
            <span>{lastGame.inningsPitched ?? 0} IP</span>
            <span>{lastGame.strikeouts ?? 0} K</span>
            <span>{lastGame.earnedRuns ?? 0} ER</span>
            {lastGame.isWinner && <span className="highlight">W</span>}
            {lastGame.isLoser && <span>L</span>}
            {lastGame.isSave && <span className="highlight">SV</span>}
          </div>
        </div>
      )}
      {season && (
        <div className="season-stats">
          <div className="stats-label">{season.season} Season</div>
          <div className="stats-row">
            <span className="stat-item">
              <span className="stat-value">{season.wins ?? 0}-{season.losses ?? 0}</span>
              <span className="stat-label">W-L</span>
            </span>
            <span className="stat-item">
              <span className="stat-value">{formatEra(season.era)}</span>
              <span className="stat-label">ERA</span>
            </span>
            <span className="stat-item">
              <span className="stat-value">{season.strikeouts ?? 0}</span>
              <span className="stat-label">K</span>
            </span>
            <span className="stat-item">
              <span className="stat-value">{formatEra(season.whip)}</span>
              <span className="stat-label">WHIP</span>
            </span>
          </div>
        </div>
      )}
      {!lastGame && !season && (
        <div className="no-stats">No stats available</div>
      )}
    </>
  );
}

export default FavoritePlayerCard;
