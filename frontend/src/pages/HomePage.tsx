import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Game } from '../types/game';
import { BattingStats, PitchingStats } from '../types/stats';
import { getTodaysGames, getHomeRunLeaders, getWinsLeaders } from '../services/api';
import GameCard from '../components/game/GameCard';
import DataTable from '../components/common/DataTable';

function HomePage() {
  const [todaysGames, setTodaysGames] = useState<Game[]>([]);
  const [hrLeaders, setHrLeaders] = useState<BattingStats[]>([]);
  const [winLeaders, setWinLeaders] = useState<PitchingStats[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchData() {
      try {
        const [games, hrs, wins] = await Promise.all([
          getTodaysGames().catch(() => []),
          getHomeRunLeaders(undefined, 5).catch(() => []),
          getWinsLeaders(undefined, 5).catch(() => []),
        ]);
        setTodaysGames(games);
        setHrLeaders(hrs);
        setWinLeaders(wins);
      } catch (_err) {
        setError('Failed to load data');
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;

  const hrColumns = [
    {
      key: 'rank',
      header: '#',
      render: (_: BattingStats, index: number) => index + 1,
    },
    {
      key: 'player',
      header: 'Player',
      render: (s: BattingStats) => (
        <Link to={`/players/${s.player.id}`}>{s.player.fullName}</Link>
      ),
    },
    {
      key: 'team',
      header: 'Team',
      render: (s: BattingStats) => s.team?.abbreviation || '-',
    },
    { key: 'homeRuns', header: 'HR', className: 'number' },
  ];

  const winColumns = [
    {
      key: 'rank',
      header: '#',
      render: (_: PitchingStats, index: number) => index + 1,
    },
    {
      key: 'player',
      header: 'Player',
      render: (s: PitchingStats) => (
        <Link to={`/players/${s.player.id}`}>{s.player.fullName}</Link>
      ),
    },
    {
      key: 'team',
      header: 'Team',
      render: (s: PitchingStats) => s.team?.abbreviation || '-',
    },
    { key: 'wins', header: 'W', className: 'number' },
    { key: 'losses', header: 'L', className: 'number' },
  ];

  return (
    <div>
      <h1 className="page-title">MLB Stats Dashboard</h1>

      <section className="section">
        <h2 className="section-title">Today's Games</h2>
        {todaysGames.length > 0 ? (
          <div className="grid grid-3">
            {todaysGames.map((game) => (
              <GameCard key={game.id} game={game} />
            ))}
          </div>
        ) : (
          <p>No games scheduled for today. <Link to="/games">View all games</Link></p>
        )}
      </section>

      <div className="grid grid-2">
        <section className="section">
          <h2 className="section-title">Home Run Leaders</h2>
          {hrLeaders.length > 0 ? (
            <div className="card">
              <DataTable
                columns={hrColumns}
                data={hrLeaders}
                keyExtractor={(s) => s.id}
              />
            </div>
          ) : (
            <p>No data available</p>
          )}
        </section>

        <section className="section">
          <h2 className="section-title">Wins Leaders</h2>
          {winLeaders.length > 0 ? (
            <div className="card">
              <DataTable
                columns={winColumns}
                data={winLeaders}
                keyExtractor={(s) => s.id}
              />
            </div>
          ) : (
            <p>No data available</p>
          )}
        </section>
      </div>
    </div>
  );
}

export default HomePage;
