import { useState, useEffect } from 'react';
import { BattingSplit, PitchingSplit } from '../../types/stats';
import { getPlayerBattingSplits, getPlayerPitchingSplits } from '../../services/api';
import { getDefaultSeason } from '../../utils/season';

interface PlayerSplitsProps {
  playerId: number;
  positionType: string | null;
}

type SplitCategory = 'location' | 'handedness' | 'half' | 'situation';

function formatAvg(value: number | null): string {
  if (value === null || value === undefined) return '.---';
  return value.toFixed(3).replace(/^0/, '');
}

function formatEra(value: number | null): string {
  if (value === null || value === undefined) return '--.--';
  return value.toFixed(2);
}

function PlayerSplits({ playerId, positionType }: PlayerSplitsProps) {
  const [battingSplits, setBattingSplits] = useState<BattingSplit[]>([]);
  const [pitchingSplits, setPitchingSplits] = useState<PitchingSplit[]>([]);
  const [loading, setLoading] = useState(true);
  const [category, setCategory] = useState<SplitCategory>('location');
  const [season, setSeason] = useState(getDefaultSeason());

  const isPitcher = positionType === 'Pitcher';

  useEffect(() => {
    async function fetchSplits() {
      setLoading(true);
      try {
        if (isPitcher) {
          const splits = await getPlayerPitchingSplits(playerId, season);
          setPitchingSplits(splits);
        } else {
          const splits = await getPlayerBattingSplits(playerId, season);
          setBattingSplits(splits);
        }
      } catch {
        // Silently handle - splits may not exist
      } finally {
        setLoading(false);
      }
    }
    fetchSplits();
  }, [playerId, season, isPitcher]);

  const splitTypesByCategory: Record<SplitCategory, string[]> = {
    location: ['HOME', 'AWAY'],
    handedness: isPitcher ? ['VS_LHB', 'VS_RHB'] : ['VS_LHP', 'VS_RHP'],
    half: ['FIRST_HALF', 'SECOND_HALF'],
    situation: ['RUNNERS_ON', 'RISP', 'BASES_EMPTY'],
  };

  const categoryLabels: Record<SplitCategory, string> = {
    location: 'Home/Away',
    handedness: isPitcher ? 'vs Batter Hand' : 'vs Pitcher Hand',
    half: 'Season Half',
    situation: 'Situation',
  };

  const filteredBattingSplits = battingSplits.filter((s) =>
    splitTypesByCategory[category].includes(s.splitType)
  );

  const filteredPitchingSplits = pitchingSplits.filter((s) =>
    splitTypesByCategory[category].includes(s.splitType)
  );

  const hasSplits = isPitcher
    ? filteredPitchingSplits.length > 0
    : filteredBattingSplits.length > 0;

  if (loading) {
    return (
      <div className="section">
        <h3 className="section-title">Splits</h3>
        <p>Loading splits...</p>
      </div>
    );
  }

  if (!hasSplits && battingSplits.length === 0 && pitchingSplits.length === 0) {
    return null; // No splits data available
  }

  return (
    <div className="section">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h3 className="section-title" style={{ margin: 0 }}>Splits</h3>
        <select
          value={season}
          onChange={(e) => setSeason(parseInt(e.target.value))}
          className="form-input"
          style={{ width: 'auto' }}
        >
          {Array.from({ length: 5 }, (_, i) => getDefaultSeason() - i).map((y) => (
            <option key={y} value={y}>{y}</option>
          ))}
        </select>
      </div>

      <div className="tab-group" style={{ marginBottom: '16px' }}>
        {(Object.keys(categoryLabels) as SplitCategory[]).map((cat) => (
          <button
            key={cat}
            className={`tab-btn ${category === cat ? 'active' : ''}`}
            onClick={() => setCategory(cat)}
          >
            {categoryLabels[cat]}
          </button>
        ))}
      </div>

      {!hasSplits ? (
        <p className="text-muted">No {categoryLabels[category]} split data available.</p>
      ) : isPitcher ? (
        <table className="data-table">
          <thead>
            <tr>
              <th>Split</th>
              <th>G</th>
              <th>IP</th>
              <th>W</th>
              <th>L</th>
              <th>ERA</th>
              <th>WHIP</th>
              <th>K</th>
              <th>BB</th>
              <th>K/9</th>
            </tr>
          </thead>
          <tbody>
            {filteredPitchingSplits.map((split) => (
              <tr key={split.id}>
                <td><strong>{split.splitTypeDisplay}</strong></td>
                <td>{split.gamesPlayed}</td>
                <td>{split.inningsPitched?.toFixed(1) ?? '--'}</td>
                <td>{split.wins}</td>
                <td>{split.losses}</td>
                <td>{formatEra(split.era)}</td>
                <td>{formatAvg(split.whip)}</td>
                <td>{split.strikeouts}</td>
                <td>{split.walks}</td>
                <td>{split.kPer9?.toFixed(1) ?? '--'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Split</th>
              <th>G</th>
              <th>PA</th>
              <th>AB</th>
              <th>H</th>
              <th>HR</th>
              <th>RBI</th>
              <th>AVG</th>
              <th>OBP</th>
              <th>SLG</th>
              <th>OPS</th>
            </tr>
          </thead>
          <tbody>
            {filteredBattingSplits.map((split) => (
              <tr key={split.id}>
                <td><strong>{split.splitTypeDisplay}</strong></td>
                <td>{split.gamesPlayed}</td>
                <td>{split.plateAppearances}</td>
                <td>{split.atBats}</td>
                <td>{split.hits}</td>
                <td>{split.homeRuns}</td>
                <td>{split.rbi}</td>
                <td>{formatAvg(split.battingAvg)}</td>
                <td>{formatAvg(split.obp)}</td>
                <td>{formatAvg(split.slg)}</td>
                <td>{formatAvg(split.ops)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default PlayerSplits;
