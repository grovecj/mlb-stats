import { Link } from 'react-router-dom';
import { Game, ProbablePitcher } from '../../types/game';

interface GameCardProps {
  game: Game;
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-US', {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
  });
}

function PitcherInfo({ pitcher, label }: { pitcher: ProbablePitcher | null; label: string }) {
  if (!pitcher) {
    return (
      <div className="pitcher-info">
        <span className="pitcher-label">{label}:</span>
        <span className="pitcher-name">TBD</span>
      </div>
    );
  }

  return (
    <Link
      to={`/players/${pitcher.id}`}
      className="pitcher-info"
      onClick={(e) => e.stopPropagation()}
      style={{ textDecoration: 'none', color: 'inherit' }}
    >
      {pitcher.headshotUrl && (
        <img
          src={pitcher.headshotUrl}
          alt={pitcher.fullName}
          className="pitcher-headshot"
          style={{ width: '32px', height: '32px', borderRadius: '50%', objectFit: 'cover' }}
        />
      )}
      <span className="pitcher-name" style={{ fontSize: '12px' }}>{pitcher.fullName}</span>
    </Link>
  );
}

function GameCard({ game }: GameCardProps) {
  const isFinal = game.status === 'Final';
  const isScheduled = game.status === 'Scheduled' || game.status === 'Pre-Game';
  const hasProbablePitchers = game.homeProbablePitcher || game.awayProbablePitcher;

  return (
    <Link to={`/games/${game.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <div className="game-card">
        <div className="matchup">
          <div className="team">
            <div className="team-name">{game.awayTeam?.abbreviation || 'TBD'}</div>
            {isFinal && <div className="score">{game.awayScore ?? '-'}</div>}
          </div>
          <div className="vs">@</div>
          <div className="team">
            <div className="team-name">{game.homeTeam?.abbreviation || 'TBD'}</div>
            {isFinal && <div className="score">{game.homeScore ?? '-'}</div>}
          </div>
        </div>

        {isScheduled && hasProbablePitchers && (
          <div className="probable-pitchers" style={{
            display: 'flex',
            justifyContent: 'space-between',
            padding: '8px 12px',
            borderTop: '1px solid var(--border-color)',
            backgroundColor: 'var(--background-color)',
            fontSize: '12px',
          }}>
            <PitcherInfo pitcher={game.awayProbablePitcher} label="Away" />
            <span style={{ color: 'var(--text-light)' }}>vs</span>
            <PitcherInfo pitcher={game.homeProbablePitcher} label="Home" />
          </div>
        )}

        <div className="game-info">
          <div>{formatDate(game.gameDate)}</div>
          <div>{game.status}</div>
          {game.venueName && <div>{game.venueName}</div>}
        </div>
      </div>
    </Link>
  );
}

export default GameCard;
