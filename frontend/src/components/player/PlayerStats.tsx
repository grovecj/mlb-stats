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

function formatRate(value: number | null): string {
  if (value === null || value === undefined) return '--';
  return value.toFixed(1);
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

          {/* Primary Stats */}
          <div className="grid grid-4" style={{ marginBottom: '16px' }}>
            <StatCard value={latestBatting.gamesPlayed || 0} label="Games" />
            <StatCard value={formatAvg(latestBatting.battingAvg)} label="AVG" />
            <StatCard value={latestBatting.homeRuns || 0} label="HR" />
            <StatCard value={latestBatting.rbi || 0} label="RBI" />
            <StatCard value={latestBatting.runs || 0} label="Runs" />
            <StatCard value={latestBatting.hits || 0} label="Hits" />
            <StatCard value={formatAvg(latestBatting.obp)} label="OBP" />
            <StatCard value={formatAvg(latestBatting.ops)} label="OPS" />
          </div>

          {/* Additional Stats */}
          <h4 style={{ fontSize: '14px', color: 'var(--text-light)', marginBottom: '12px' }}>Additional Stats</h4>
          <div className="grid grid-4" style={{ marginBottom: '24px' }}>
            <StatCard value={formatAvg(latestBatting.slg)} label="SLG" />
            <StatCard value={formatAvg(latestBatting.babip)} label="BABIP" />
            <StatCard value={formatAvg(latestBatting.iso)} label="ISO" />
            <StatCard value={latestBatting.plateAppearances || 0} label="PA" />
            <StatCard value={latestBatting.doubles || 0} label="2B" />
            <StatCard value={latestBatting.triples || 0} label="3B" />
            <StatCard value={latestBatting.stolenBases || 0} label="SB" />
            <StatCard value={latestBatting.caughtStealing || 0} label="CS" />
            <StatCard value={latestBatting.walks || 0} label="BB" />
            <StatCard value={latestBatting.strikeouts || 0} label="SO" />
            <StatCard value={latestBatting.totalBases || 0} label="TB" />
            <StatCard value={latestBatting.extraBaseHits || 0} label="XBH" />
          </div>
        </>
      )}

      {latestPitching && (
        <>
          <h3 className="section-title">
            {latestPitching.season} Pitching Stats
            {latestPitching.team && ` - ${latestPitching.team.name}`}
          </h3>

          {/* Primary Stats */}
          <div className="grid grid-4" style={{ marginBottom: '16px' }}>
            <StatCard value={latestPitching.gamesPlayed || 0} label="Games" />
            <StatCard value={formatEra(latestPitching.era)} label="ERA" />
            <StatCard value={`${latestPitching.wins || 0}-${latestPitching.losses || 0}`} label="W-L" />
            <StatCard value={latestPitching.strikeouts || 0} label="SO" />
            <StatCard value={latestPitching.inningsPitched || 0} label="IP" />
            <StatCard value={latestPitching.saves || 0} label="Saves" />
            <StatCard value={formatAvg(latestPitching.whip)} label="WHIP" />
            <StatCard value={latestPitching.gamesStarted || 0} label="GS" />
          </div>

          {/* Additional Stats */}
          <h4 style={{ fontSize: '14px', color: 'var(--text-light)', marginBottom: '12px' }}>Additional Stats</h4>
          <div className="grid grid-4">
            <StatCard value={formatRate(latestPitching.kPer9)} label="K/9" />
            <StatCard value={formatRate(latestPitching.bbPer9)} label="BB/9" />
            <StatCard value={formatRate(latestPitching.hPer9)} label="H/9" />
            <StatCard value={latestPitching.holds || 0} label="Holds" />
            <StatCard value={latestPitching.completeGames || 0} label="CG" />
            <StatCard value={latestPitching.shutouts || 0} label="SHO" />
            <StatCard value={latestPitching.hitsAllowed || 0} label="H" />
            <StatCard value={latestPitching.walks || 0} label="BB" />
            <StatCard value={latestPitching.earnedRuns || 0} label="ER" />
            <StatCard value={latestPitching.homeRunsAllowed || 0} label="HR" />
            <StatCard value={latestPitching.runsAllowed || 0} label="R" />
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
