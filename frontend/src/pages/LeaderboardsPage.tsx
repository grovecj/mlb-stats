import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { BattingStats, PitchingStats } from '../types/stats';
import {
  getHomeRunLeaders,
  getBattingAverageLeaders,
  getRbiLeaders,
  getRunsLeaders,
  getHitsLeaders,
  getStolenBaseLeaders,
  getOpsLeaders,
  getWinsLeaders,
  getStrikeoutLeaders,
  getEraLeaders,
  getSavesLeaders,
  getWhipLeaders,
  getBattingGwarLeaders,
  getPitchingGwarLeaders,
  getOaaLeaders,
  getWarLeaders,
  getPitchingWarLeaders,
} from '../services/api';

type LeaderboardCategory =
  | 'home-runs' | 'batting-average' | 'rbi' | 'runs' | 'hits' | 'stolen-bases' | 'ops'
  | 'war' | 'gwar' | 'oaa'
  | 'wins' | 'strikeouts' | 'era' | 'saves' | 'whip' | 'pitching-war' | 'pitching-gwar';

interface CategoryConfig {
  label: string;
  type: 'batting' | 'pitching';
  fetch: (season: number, limit: number) => Promise<BattingStats[] | PitchingStats[]>;
  statKey: keyof BattingStats | keyof PitchingStats;
  format: (value: number) => string;
}

const categories: Record<LeaderboardCategory, CategoryConfig> = {
  'home-runs': {
    label: 'Home Runs',
    type: 'batting',
    fetch: getHomeRunLeaders,
    statKey: 'homeRuns',
    format: (v) => String(v),
  },
  'batting-average': {
    label: 'Batting Average',
    type: 'batting',
    fetch: getBattingAverageLeaders,
    statKey: 'battingAvg',
    format: (v) => v.toFixed(3).replace(/^0/, ''),
  },
  'rbi': {
    label: 'RBI',
    type: 'batting',
    fetch: getRbiLeaders,
    statKey: 'rbi',
    format: (v) => String(v),
  },
  'runs': {
    label: 'Runs',
    type: 'batting',
    fetch: getRunsLeaders,
    statKey: 'runs',
    format: (v) => String(v),
  },
  'hits': {
    label: 'Hits',
    type: 'batting',
    fetch: getHitsLeaders,
    statKey: 'hits',
    format: (v) => String(v),
  },
  'stolen-bases': {
    label: 'Stolen Bases',
    type: 'batting',
    fetch: getStolenBaseLeaders,
    statKey: 'stolenBases',
    format: (v) => String(v),
  },
  'ops': {
    label: 'OPS',
    type: 'batting',
    fetch: getOpsLeaders,
    statKey: 'ops',
    format: (v) => v.toFixed(3),
  },
  'wins': {
    label: 'Wins',
    type: 'pitching',
    fetch: getWinsLeaders,
    statKey: 'wins',
    format: (v) => String(v),
  },
  'strikeouts': {
    label: 'Strikeouts',
    type: 'pitching',
    fetch: getStrikeoutLeaders,
    statKey: 'strikeouts',
    format: (v) => String(v),
  },
  'era': {
    label: 'ERA',
    type: 'pitching',
    fetch: getEraLeaders,
    statKey: 'era',
    format: (v) => v.toFixed(2),
  },
  'saves': {
    label: 'Saves',
    type: 'pitching',
    fetch: getSavesLeaders,
    statKey: 'saves',
    format: (v) => String(v),
  },
  'whip': {
    label: 'WHIP',
    type: 'pitching',
    fetch: getWhipLeaders,
    statKey: 'whip',
    format: (v) => v.toFixed(2),
  },
  'war': {
    label: 'WAR',
    type: 'batting',
    fetch: getWarLeaders,
    statKey: 'war',
    format: (v) => v?.toFixed(1) ?? '--',
  },
  'gwar': {
    label: 'gWAR',
    type: 'batting',
    fetch: getBattingGwarLeaders,
    statKey: 'gwar',
    format: (v) => v?.toFixed(1) ?? '--',
  },
  'oaa': {
    label: 'OAA',
    type: 'batting',
    fetch: getOaaLeaders,
    statKey: 'oaa',
    format: (v) => String(v ?? '--'),
  },
  'pitching-war': {
    label: 'WAR',
    type: 'pitching',
    fetch: getPitchingWarLeaders,
    statKey: 'war',
    format: (v) => v?.toFixed(1) ?? '--',
  },
  'pitching-gwar': {
    label: 'gWAR',
    type: 'pitching',
    fetch: getPitchingGwarLeaders,
    statKey: 'gwar',
    format: (v) => v?.toFixed(1) ?? '--',
  },
};

const battingCategories: LeaderboardCategory[] = ['home-runs', 'batting-average', 'rbi', 'runs', 'hits', 'stolen-bases', 'ops', 'war', 'gwar', 'oaa'];
const pitchingCategories: LeaderboardCategory[] = ['wins', 'strikeouts', 'era', 'saves', 'whip', 'pitching-war', 'pitching-gwar'];

function getDefaultSeason(): number {
  const now = new Date();
  const month = now.getMonth();
  return month < 3 ? now.getFullYear() - 1 : now.getFullYear();
}

function LeaderboardsPage() {
  const [activeCategory, setActiveCategory] = useState<LeaderboardCategory>('home-runs');
  const [season, setSeason] = useState<number>(getDefaultSeason);
  const [limit, setLimit] = useState<number>(25);
  const [data, setData] = useState<(BattingStats | PitchingStats)[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'batting' | 'pitching'>('batting');

  useEffect(() => {
    async function fetchData() {
      setLoading(true);
      try {
        const config = categories[activeCategory];
        const result = await config.fetch(season, limit);
        setData(result);
      } catch (error) {
        console.error('Failed to load leaderboard:', error);
        setData([]);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, [activeCategory, season, limit]);

  const config = categories[activeCategory];

  return (
    <div>
      <h1 className="page-title">Leaderboards</h1>

      <div className="card" style={{ marginBottom: '24px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
          <div style={{ display: 'flex', gap: '8px' }}>
            <button
              className={`tab-btn ${activeTab === 'batting' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('batting');
                setActiveCategory('home-runs');
              }}
            >
              Batting
            </button>
            <button
              className={`tab-btn ${activeTab === 'pitching' ? 'active' : ''}`}
              onClick={() => {
                setActiveTab('pitching');
                setActiveCategory('wins');
              }}
            >
              Pitching
            </button>
          </div>
          <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
            <select
              value={season}
              onChange={(e) => setSeason(Number(e.target.value))}
              style={{
                padding: '6px 12px',
                borderRadius: '4px',
                border: '1px solid var(--border-color)',
                backgroundColor: 'var(--card-background)',
                color: 'var(--text-color)',
              }}
            >
              {Array.from({ length: 10 }, (_, i) => getDefaultSeason() - i).map((y) => (
                <option key={y} value={y}>{y}</option>
              ))}
            </select>
            <select
              value={limit}
              onChange={(e) => setLimit(Number(e.target.value))}
              style={{
                padding: '6px 12px',
                borderRadius: '4px',
                border: '1px solid var(--border-color)',
                backgroundColor: 'var(--card-background)',
                color: 'var(--text-color)',
              }}
            >
              <option value={10}>Top 10</option>
              <option value={25}>Top 25</option>
              <option value={50}>Top 50</option>
              <option value={100}>Top 100</option>
            </select>
          </div>
        </div>
      </div>

      <div className="card" style={{ marginBottom: '24px' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
          {(activeTab === 'batting' ? battingCategories : pitchingCategories).map((cat) => (
            <button
              key={cat}
              className={`tab-btn ${activeCategory === cat ? 'active' : ''}`}
              onClick={() => setActiveCategory(cat)}
            >
              {categories[cat].label}
            </button>
          ))}
        </div>
      </div>

      <div className="card">
        <h2 className="card-title">{config.label} Leaders - {season}</h2>

        {loading ? (
          <div className="loading">Loading leaderboard...</div>
        ) : data.length === 0 ? (
          <p>No data available for this category.</p>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th style={{ width: '50px' }}>Rank</th>
                  <th>Player</th>
                  <th>Team</th>
                  <th className="number">{config.label}</th>
                </tr>
              </thead>
              <tbody>
                {data.map((stat, index) => (
                  <tr key={stat.id}>
                    <td>{index + 1}</td>
                    <td>
                      <Link to={`/players/${stat.player.id}`}>
                        {stat.player.fullName}
                      </Link>
                    </td>
                    <td>
                      <Link to={`/teams/${stat.team.id}`}>
                        {stat.team.abbreviation}
                      </Link>
                    </td>
                    <td className="number">
                      {config.format((stat as unknown as Record<string, number>)[config.statKey as string])}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default LeaderboardsPage;
