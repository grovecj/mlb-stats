import { BattingStats, PitchingStats } from '../../types/stats';
import StatCard from '../common/StatCard';

interface PlayerStatsProps {
  battingStats?: BattingStats[];
  pitchingStats?: PitchingStats[];
}

function formatAvg(value: number | null): string {
  if (value === null || value === undefined) return '.---';
  return value.toFixed(3).replace(/^0/, '');
}

function formatEra(value: number | null): string {
  if (value === null || value === undefined) return '--.--';
  return value.toFixed(2);
}

function PlayerStats({ battingStats, pitchingStats }: PlayerStatsProps) {
  const latestBatting = battingStats?.[0];
  const latestPitching = pitchingStats?.[0];

  return (
    <div className="section">
      {latestBatting && (
        <>
          <h3 className="section-title">
            {latestBatting.season} Batting Stats
            {latestBatting.team && ` - ${latestBatting.team.name}`}
          </h3>
          <div className="grid grid-4" style={{ marginBottom: '24px' }}>
            <StatCard value={latestBatting.gamesPlayed || 0} label="Games" />
            <StatCard value={formatAvg(latestBatting.battingAvg)} label="AVG" />
            <StatCard value={latestBatting.homeRuns || 0} label="HR" />
            <StatCard value={latestBatting.rbi || 0} label="RBI" />
            <StatCard value={latestBatting.runs || 0} label="Runs" />
            <StatCard value={latestBatting.hits || 0} label="Hits" />
            <StatCard value={formatAvg(latestBatting.obp)} label="OBP" />
            <StatCard value={formatAvg(latestBatting.ops)} label="OPS" />
          </div>
        </>
      )}

      {latestPitching && (
        <>
          <h3 className="section-title">
            {latestPitching.season} Pitching Stats
            {latestPitching.team && ` - ${latestPitching.team.name}`}
          </h3>
          <div className="grid grid-4">
            <StatCard value={latestPitching.gamesPlayed || 0} label="Games" />
            <StatCard value={formatEra(latestPitching.era)} label="ERA" />
            <StatCard value={`${latestPitching.wins || 0}-${latestPitching.losses || 0}`} label="W-L" />
            <StatCard value={latestPitching.strikeouts || 0} label="SO" />
            <StatCard value={latestPitching.inningsPitched || 0} label="IP" />
            <StatCard value={latestPitching.saves || 0} label="Saves" />
            <StatCard value={formatAvg(latestPitching.whip)} label="WHIP" />
            <StatCard value={latestPitching.gamesStarted || 0} label="GS" />
          </div>
        </>
      )}

      {!latestBatting && !latestPitching && (
        <p>No stats available for this player.</p>
      )}
    </div>
  );
}

export default PlayerStats;
