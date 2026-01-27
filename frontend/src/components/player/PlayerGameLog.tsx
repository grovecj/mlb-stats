import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { BattingGameLog, PitchingGameLog } from '../../types/stats';
import { getPlayerBattingGameLog, getPlayerPitchingGameLog } from '../../services/api';
import DataTable from '../common/DataTable';

interface PlayerGameLogProps {
  playerId: number;
  positionType: string | null;
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
  });
}

function PlayerGameLog({ playerId, positionType }: PlayerGameLogProps) {
  const [battingLog, setBattingLog] = useState<BattingGameLog[]>([]);
  const [pitchingLog, setPitchingLog] = useState<PitchingGameLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'batting' | 'pitching'>('batting');
  const [season, setSeason] = useState<number>(new Date().getFullYear());

  const isPitcher = positionType === 'Pitcher';

  useEffect(() => {
    async function fetchGameLogs() {
      setLoading(true);
      try {
        if (isPitcher) {
          const [pitching, batting] = await Promise.all([
            getPlayerPitchingGameLog(playerId, season),
            getPlayerBattingGameLog(playerId, season).catch(() => []),
          ]);
          setPitchingLog(pitching);
          setBattingLog(batting);
          setActiveTab(pitching.length > 0 ? 'pitching' : 'batting');
        } else {
          const [batting, pitching] = await Promise.all([
            getPlayerBattingGameLog(playerId, season),
            getPlayerPitchingGameLog(playerId, season).catch(() => []),
          ]);
          setBattingLog(batting);
          setPitchingLog(pitching);
          setActiveTab(batting.length > 0 ? 'batting' : 'pitching');
        }
      } catch (error) {
        console.error('Failed to load game log:', error);
      } finally {
        setLoading(false);
      }
    }
    fetchGameLogs();
  }, [playerId, season, isPitcher]);

  const battingColumns = [
    {
      key: 'gameDate',
      header: 'Date',
      render: (g: BattingGameLog) => (
        <Link to={`/games/${g.gameId}`}>{formatDate(g.gameDate)}</Link>
      ),
    },
    {
      key: 'opponent',
      header: 'Opp',
      render: (g: BattingGameLog) => (
        <Link to={`/teams/${g.opponentId}`}>
          {g.isHome ? 'vs' : '@'} {g.opponentAbbreviation}
        </Link>
      ),
    },
    { key: 'result', header: 'Result' },
    { key: 'atBats', header: 'AB', className: 'number' },
    { key: 'runs', header: 'R', className: 'number' },
    { key: 'hits', header: 'H', className: 'number' },
    { key: 'doubles', header: '2B', className: 'number' },
    { key: 'triples', header: '3B', className: 'number' },
    { key: 'homeRuns', header: 'HR', className: 'number' },
    { key: 'rbi', header: 'RBI', className: 'number' },
    { key: 'walks', header: 'BB', className: 'number' },
    { key: 'strikeouts', header: 'SO', className: 'number' },
    { key: 'stolenBases', header: 'SB', className: 'number' },
  ];

  const pitchingColumns = [
    {
      key: 'gameDate',
      header: 'Date',
      render: (g: PitchingGameLog) => (
        <Link to={`/games/${g.gameId}`}>{formatDate(g.gameDate)}</Link>
      ),
    },
    {
      key: 'opponent',
      header: 'Opp',
      render: (g: PitchingGameLog) => (
        <Link to={`/teams/${g.opponentId}`}>
          {g.isHome ? 'vs' : '@'} {g.opponentAbbreviation}
        </Link>
      ),
    },
    { key: 'result', header: 'Result' },
    {
      key: 'decision',
      header: 'Dec',
      render: (g: PitchingGameLog) => g.decision || '-',
    },
    { key: 'inningsPitched', header: 'IP', className: 'number' },
    { key: 'hitsAllowed', header: 'H', className: 'number' },
    { key: 'runsAllowed', header: 'R', className: 'number' },
    { key: 'earnedRuns', header: 'ER', className: 'number' },
    { key: 'walks', header: 'BB', className: 'number' },
    { key: 'strikeouts', header: 'K', className: 'number' },
    { key: 'homeRunsAllowed', header: 'HR', className: 'number' },
    {
      key: 'pitchesThrown',
      header: 'P',
      className: 'number',
      render: (g: PitchingGameLog) => g.pitchesThrown ?? '-',
    },
  ];

  const hasBatting = battingLog.length > 0;
  const hasPitching = pitchingLog.length > 0;

  if (loading) {
    return (
      <div className="card">
        <h3 className="card-title">Game Log</h3>
        <div className="loading">Loading game log...</div>
      </div>
    );
  }

  if (!hasBatting && !hasPitching) {
    return (
      <div className="card">
        <h3 className="card-title">Game Log</h3>
        <p style={{ color: 'var(--text-light)' }}>No game data available for {season}.</p>
      </div>
    );
  }

  return (
    <div className="card">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', flexWrap: 'wrap', gap: '12px' }}>
        <h3 className="card-title" style={{ margin: 0 }}>Game Log</h3>
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
            {[2024, 2023, 2022, 2021, 2020].map((y) => (
              <option key={y} value={y}>{y}</option>
            ))}
          </select>
        </div>
      </div>

      {hasBatting && hasPitching && (
        <div className="tab-buttons" style={{ marginBottom: '16px' }}>
          <button
            className={`tab-btn ${activeTab === 'batting' ? 'active' : ''}`}
            onClick={() => setActiveTab('batting')}
          >
            Batting ({battingLog.length})
          </button>
          <button
            className={`tab-btn ${activeTab === 'pitching' ? 'active' : ''}`}
            onClick={() => setActiveTab('pitching')}
          >
            Pitching ({pitchingLog.length})
          </button>
        </div>
      )}

      <div className="table-responsive">
        {activeTab === 'batting' && hasBatting && (
          <DataTable
            columns={battingColumns}
            data={battingLog}
            keyExtractor={(g) => g.gameId}
          />
        )}
        {activeTab === 'pitching' && hasPitching && (
          <DataTable
            columns={pitchingColumns}
            data={pitchingLog}
            keyExtractor={(g) => g.gameId}
          />
        )}
      </div>
    </div>
  );
}

export default PlayerGameLog;
