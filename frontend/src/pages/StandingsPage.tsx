import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { TeamStanding } from '../types/team';
import { getStandings } from '../services/api';

function StandingsPage() {
  const currentYear = new Date().getFullYear();
  const [standings, setStandings] = useState<TeamStanding[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [season, setSeason] = useState(currentYear);

  useEffect(() => {
    async function fetchStandings() {
      setLoading(true);
      try {
        const data = await getStandings(season);
        setStandings(data);
        setError(null);
      } catch (err) {
        setError('Failed to load standings');
      } finally {
        setLoading(false);
      }
    }
    fetchStandings();
  }, [season]);

  // Group standings by league and division
  const grouped = standings.reduce((acc, standing) => {
    const key = `${standing.team.league} - ${standing.team.division}`;
    if (!acc[key]) acc[key] = [];
    acc[key].push(standing);
    return acc;
  }, {} as Record<string, TeamStanding[]>);

  // Sort each division by division rank
  Object.values(grouped).forEach((divisionStandings) => {
    divisionStandings.sort(
      (a, b) => (a.divisionRank ?? 999) - (b.divisionRank ?? 999)
    );
  });

  const divisions = [
    'American League - East',
    'American League - Central',
    'American League - West',
    'National League - East',
    'National League - Central',
    'National League - West',
  ];

  const years = Array.from({ length: 10 }, (_, i) => currentYear - i);

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <h1 className="page-title" style={{ margin: 0 }}>MLB Standings</h1>
        <select
          value={season}
          onChange={(e) => setSeason(Number(e.target.value))}
          style={{
            padding: '8px 12px',
            borderRadius: '4px',
            border: '1px solid var(--border-color)',
            background: 'var(--card-background)',
            color: 'var(--text-color)',
            fontSize: '14px',
          }}
        >
          {years.map((year) => (
            <option key={year} value={year}>
              {year}
            </option>
          ))}
        </select>
      </div>

      {loading && <div className="loading">Loading standings...</div>}
      {error && <div className="error">{error}</div>}

      {!loading && !error && standings.length === 0 && (
        <p>No standings data available. Try syncing standings from the Admin page.</p>
      )}

      {!loading && !error && divisions.map((division) => {
        const divisionStandings = grouped[division] || [];
        if (divisionStandings.length === 0) return null;

        return (
          <div key={division} className="card" style={{ marginBottom: '24px' }}>
            <h2 style={{ fontSize: '18px', marginBottom: '16px', color: 'var(--primary-color)' }}>
              {division}
            </h2>
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
                <thead>
                  <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
                    <th style={{ textAlign: 'left', padding: '8px', whiteSpace: 'nowrap' }}>Team</th>
                    <th style={{ textAlign: 'center', padding: '8px' }}>W</th>
                    <th style={{ textAlign: 'center', padding: '8px' }}>L</th>
                    <th style={{ textAlign: 'center', padding: '8px' }}>PCT</th>
                    <th style={{ textAlign: 'center', padding: '8px' }}>GB</th>
                    <th style={{ textAlign: 'center', padding: '8px' }}>WC GB</th>
                    <th style={{ textAlign: 'center', padding: '8px' }}>Strk</th>
                    <th style={{ textAlign: 'center', padding: '8px', whiteSpace: 'nowrap' }}>Home</th>
                    <th style={{ textAlign: 'center', padding: '8px', whiteSpace: 'nowrap' }}>Away</th>
                    <th style={{ textAlign: 'center', padding: '8px' }}>RS</th>
                    <th style={{ textAlign: 'center', padding: '8px' }}>RA</th>
                    <th style={{ textAlign: 'center', padding: '8px' }}>Diff</th>
                  </tr>
                </thead>
                <tbody>
                  {divisionStandings.map((standing) => (
                    <tr
                      key={standing.id}
                      style={{ borderBottom: '1px solid var(--border-color)' }}
                    >
                      <td style={{ padding: '8px', whiteSpace: 'nowrap' }}>
                        <Link
                          to={`/teams/${standing.team.id}`}
                          style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '8px',
                            color: 'var(--text-color)',
                            textDecoration: 'none',
                          }}
                        >
                          {standing.team.logoUrl && (
                            <img
                              src={standing.team.logoUrl}
                              alt=""
                              style={{ width: '24px', height: '24px' }}
                            />
                          )}
                          <span>{standing.team.name}</span>
                        </Link>
                      </td>
                      <td style={{ textAlign: 'center', padding: '8px', fontWeight: 'bold' }}>
                        {standing.wins}
                      </td>
                      <td style={{ textAlign: 'center', padding: '8px' }}>
                        {standing.losses}
                      </td>
                      <td style={{ textAlign: 'center', padding: '8px' }}>
                        {standing.winningPercentage?.toFixed(3) || '-'}
                      </td>
                      <td style={{ textAlign: 'center', padding: '8px' }}>
                        {standing.gamesBack || '-'}
                      </td>
                      <td style={{ textAlign: 'center', padding: '8px' }}>
                        {standing.wildCardGamesBack || '-'}
                      </td>
                      <td style={{ textAlign: 'center', padding: '8px' }}>
                        <span
                          style={{
                            padding: '2px 6px',
                            borderRadius: '4px',
                            fontSize: '12px',
                            backgroundColor: standing.streakCode?.startsWith('W')
                              ? 'var(--success-color)'
                              : standing.streakCode?.startsWith('L')
                              ? 'var(--secondary-color)'
                              : 'var(--border-color)',
                            color: standing.streakCode?.startsWith('W') || standing.streakCode?.startsWith('L')
                              ? 'white'
                              : 'var(--text-color)',
                          }}
                        >
                          {standing.streakCode || '-'}
                        </span>
                      </td>
                      <td style={{ textAlign: 'center', padding: '8px' }}>
                        {standing.homeWins ?? 0}-{standing.homeLosses ?? 0}
                      </td>
                      <td style={{ textAlign: 'center', padding: '8px' }}>
                        {standing.awayWins ?? 0}-{standing.awayLosses ?? 0}
                      </td>
                      <td style={{ textAlign: 'center', padding: '8px' }}>
                        {standing.runsScored || 0}
                      </td>
                      <td style={{ textAlign: 'center', padding: '8px' }}>
                        {standing.runsAllowed || 0}
                      </td>
                      <td
                        style={{
                          textAlign: 'center',
                          padding: '8px',
                          color:
                            (standing.runDifferential || 0) > 0
                              ? 'var(--success-color)'
                              : (standing.runDifferential || 0) < 0
                              ? 'var(--secondary-color)'
                              : 'var(--text-color)',
                          fontWeight: 'bold',
                        }}
                      >
                        {(standing.runDifferential || 0) > 0 ? '+' : ''}
                        {standing.runDifferential || 0}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default StandingsPage;
