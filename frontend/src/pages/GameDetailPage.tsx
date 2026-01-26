import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Game, BoxScore as BoxScoreType } from '../types/game';
import { getGame, getGameBoxScore } from '../services/api';
import BoxScore from '../components/game/BoxScore';
import BattingTable from '../components/game/BattingTable';
import PitchingTable from '../components/game/PitchingTable';
import '../components/game/BoxScoreTables.css';

function GameDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [game, setGame] = useState<Game | null>(null);
  const [boxScore, setBoxScore] = useState<BoxScoreType | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'batting' | 'pitching'>('batting');

  useEffect(() => {
    async function fetchGame() {
      if (!id) return;
      try {
        const [gameData, boxScoreData] = await Promise.all([
          getGame(parseInt(id)),
          getGameBoxScore(parseInt(id)).catch(() => null),
        ]);
        setGame(gameData);
        setBoxScore(boxScoreData);
      } catch (err) {
        setError('Failed to load game');
      } finally {
        setLoading(false);
      }
    }
    fetchGame();
  }, [id]);

  const hasBoxScore = boxScore && (
    boxScore.awayBatting.length > 0 ||
    boxScore.homeBatting.length > 0 ||
    boxScore.awayPitching.length > 0 ||
    boxScore.homePitching.length > 0
  );

  if (loading) return <div className="loading">Loading game...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!game) return <div className="error">Game not found</div>;

  return (
    <div>
      <div className="game-back-link">
        <Link to="/games">&larr; Back to Games</Link>
      </div>

      <BoxScore game={game} />

      <div className="grid grid-2" style={{ marginTop: '24px' }}>
        <div className="card">
          <h3 className="card-title">Away Team</h3>
          <Link to={`/teams/${game.awayTeam?.id}`}>
            <div className="team-card-content">
              <span className="team-abbreviation">
                {game.awayTeam?.abbreviation}
              </span>
              <div className="team-details">
                <div className="team-name">{game.awayTeam?.name}</div>
                <div className="team-division">
                  {game.awayTeam?.league} - {game.awayTeam?.division}
                </div>
              </div>
            </div>
          </Link>
        </div>

        <div className="card">
          <h3 className="card-title">Home Team</h3>
          <Link to={`/teams/${game.homeTeam?.id}`}>
            <div className="team-card-content">
              <span className="team-abbreviation">
                {game.homeTeam?.abbreviation}
              </span>
              <div className="team-details">
                <div className="team-name">{game.homeTeam?.name}</div>
                <div className="team-division">
                  {game.homeTeam?.league} - {game.homeTeam?.division}
                </div>
              </div>
            </div>
          </Link>
        </div>
      </div>

      <div className="card" style={{ marginTop: '24px' }}>
        <h3 className="card-title">Game Information</h3>
        <table className="data-table game-info-table">
          <tbody>
            <tr>
              <td className="game-info-label">Venue</td>
              <td>{game.venueName || '-'}</td>
            </tr>
            <tr>
              <td className="game-info-label">Game Type</td>
              <td>{game.gameType === 'R' ? 'Regular Season' : game.gameType === 'P' ? 'Postseason' : game.gameType}</td>
            </tr>
            <tr>
              <td className="game-info-label">Day/Night</td>
              <td>{game.dayNight || '-'}</td>
            </tr>
            <tr>
              <td className="game-info-label">Season</td>
              <td>{game.season}</td>
            </tr>
          </tbody>
        </table>
      </div>

      {hasBoxScore && (
        <div className="card" style={{ marginTop: '24px' }}>
          <div className="boxscore-tabs" role="tablist" aria-label="Box score statistics">
            <button
              role="tab"
              aria-selected={activeTab === 'batting'}
              aria-controls="batting-panel"
              id="batting-tab"
              className="boxscore-tab"
              onClick={() => setActiveTab('batting')}
            >
              Batting
            </button>
            <button
              role="tab"
              aria-selected={activeTab === 'pitching'}
              aria-controls="pitching-panel"
              id="pitching-tab"
              className="boxscore-tab"
              onClick={() => setActiveTab('pitching')}
            >
              Pitching
            </button>
          </div>

          {activeTab === 'batting' && boxScore && (
            <div className="grid grid-2" role="tabpanel" id="batting-panel" aria-labelledby="batting-tab">
              <BattingTable batting={boxScore.awayBatting} teamName={game.awayTeam?.name || 'Away'} />
              <BattingTable batting={boxScore.homeBatting} teamName={game.homeTeam?.name || 'Home'} />
            </div>
          )}

          {activeTab === 'pitching' && boxScore && (
            <div className="grid grid-2" role="tabpanel" id="pitching-panel" aria-labelledby="pitching-tab">
              <PitchingTable pitching={boxScore.awayPitching} teamName={game.awayTeam?.name || 'Away'} />
              <PitchingTable pitching={boxScore.homePitching} teamName={game.homeTeam?.name || 'Home'} />
            </div>
          )}
        </div>
      )}

      {game.status === 'Final' && !hasBoxScore && (
        <div className="card boxscore-unavailable" style={{ marginTop: '24px' }}>
          <p>Box score data not yet available for this game.</p>
          <p>Admins can sync box scores from the Admin page.</p>
        </div>
      )}
    </div>
  );
}

export default GameDetailPage;
