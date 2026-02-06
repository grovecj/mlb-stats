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

function formatPct(value: number | null): string {
  if (value === null || value === undefined) return '--%';
  return `${value.toFixed(1)}%`;
}

function formatWar(value: number | null): string {
  if (value === null || value === undefined) return '--';
  return value.toFixed(1);
}

function hasAdvancedBattingStats(stats: BattingStats): boolean {
  return stats.war != null || stats.gwar != null || stats.woba != null || stats.wrcPlus != null ||
    stats.avgExitVelocity != null || stats.barrelPct != null || stats.oaa != null;
}

function hasAdvancedPitchingStats(stats: PitchingStats): boolean {
  return stats.war != null || stats.gwar != null || stats.fip != null || stats.xfip != null ||
    stats.whiffPct != null || stats.xera != null;
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

          {/* Advanced Sabermetric Stats */}
          {hasAdvancedBattingStats(latestBatting) && (
            <>
              <h4 style={{ fontSize: '14px', color: 'var(--text-light)', marginBottom: '12px' }}>Advanced Analytics</h4>
              <div className="grid grid-4" style={{ marginBottom: '24px' }}>
                <StatCard value={formatWar(latestBatting.war)} label="WAR" tooltip="Wins Above Replacement - measures total value compared to a replacement-level player" />
                <StatCard value={formatAvg(latestBatting.woba)} label="wOBA" tooltip="Weighted On-Base Average - measures overall offensive value with proper weighting" />
                <StatCard value={latestBatting.wrcPlus ?? '--'} label="wRC+" tooltip="Weighted Runs Created Plus - runs created adjusted for park and league (100 = average)" />
                <StatCard value={formatPct(latestBatting.kPct)} label="K%" tooltip="Strikeout percentage" />
                <StatCard value={formatPct(latestBatting.bbPct)} label="BB%" tooltip="Walk percentage" />
                <StatCard value={formatAvg(latestBatting.xba)} label="xBA" tooltip="Expected Batting Average based on exit velocity and launch angle" />
                <StatCard value={formatAvg(latestBatting.xslg)} label="xSLG" tooltip="Expected Slugging based on exit velocity and launch angle" />
                <StatCard value={formatAvg(latestBatting.xwoba)} label="xwOBA" tooltip="Expected wOBA based on quality of contact" />
                <StatCard value={formatRate(latestBatting.avgExitVelocity)} label="Exit Velo" tooltip="Average exit velocity in mph" />
                <StatCard value={formatRate(latestBatting.avgLaunchAngle)} label="Launch Angle" tooltip="Average launch angle in degrees" />
                <StatCard value={formatPct(latestBatting.hardHitPct)} label="Hard Hit%" tooltip="Percentage of batted balls with exit velocity 95+ mph" />
                <StatCard value={formatPct(latestBatting.barrelPct)} label="Barrel%" tooltip="Percentage of batted balls with optimal exit velocity and launch angle" />
                <StatCard value={formatRate(latestBatting.sprintSpeed)} label="Sprint Speed" tooltip="Average sprint speed in feet per second" />
              </div>

              {/* gWAR Breakdown */}
              {latestBatting.gwar != null && (
                <>
                  <h4 style={{ fontSize: '14px', color: 'var(--text-light)', marginBottom: '12px' }}>gWAR Breakdown (Grove WAR)</h4>
                  <div className="grid grid-4" style={{ marginBottom: '24px' }}>
                    <StatCard value={formatWar(latestBatting.gwar)} label="gWAR" tooltip="Grove WAR - transparent, simplified WAR calculation" />
                    <StatCard value={formatWar(latestBatting.gwarBatting)} label="Batting" tooltip="gWAR contribution from batting (wRAA)" />
                    <StatCard value={formatWar(latestBatting.gwarBaserunning)} label="Baserunning" tooltip="gWAR contribution from baserunning (wSB)" />
                    <StatCard value={formatWar(latestBatting.gwarFielding)} label="Fielding" tooltip="gWAR contribution from fielding (OAA-based)" />
                    <StatCard value={formatWar(latestBatting.gwarPositional)} label="Positional" tooltip="gWAR positional adjustment" />
                    <StatCard value={formatWar(latestBatting.gwarReplacement)} label="Replacement" tooltip="gWAR replacement level runs" />
                    <StatCard value={latestBatting.oaa ?? '--'} label="OAA" tooltip="Outs Above Average - fielding metric from Statcast" />
                  </div>
                </>
              )}
            </>
          )}
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
          <div className="grid grid-4" style={{ marginBottom: '24px' }}>
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

          {/* Advanced Sabermetric Stats */}
          {hasAdvancedPitchingStats(latestPitching) && (
            <>
              <h4 style={{ fontSize: '14px', color: 'var(--text-light)', marginBottom: '12px' }}>Advanced Analytics</h4>
              <div className="grid grid-4" style={{ marginBottom: '24px' }}>
                <StatCard value={formatWar(latestPitching.war)} label="WAR" tooltip="Wins Above Replacement - measures total value compared to a replacement-level player" />
                <StatCard value={formatEra(latestPitching.fip)} label="FIP" tooltip="Fielding Independent Pitching - ERA based only on events pitcher controls (K, BB, HR)" />
                <StatCard value={formatEra(latestPitching.xfip)} label="xFIP" tooltip="Expected FIP - FIP with normalized home run rate" />
                <StatCard value={formatEra(latestPitching.siera)} label="SIERA" tooltip="Skill-Interactive ERA - advanced ERA estimator" />
                <StatCard value={formatEra(latestPitching.xera)} label="xERA" tooltip="Expected ERA based on quality of contact allowed" />
                <StatCard value={formatPct(latestPitching.kPct)} label="K%" tooltip="Strikeout percentage" />
                <StatCard value={formatPct(latestPitching.bbPct)} label="BB%" tooltip="Walk percentage" />
                <StatCard value={formatPct(latestPitching.gbPct)} label="GB%" tooltip="Ground ball percentage" />
                <StatCard value={formatPct(latestPitching.fbPct)} label="FB%" tooltip="Fly ball percentage" />
                <StatCard value={formatPct(latestPitching.whiffPct)} label="Whiff%" tooltip="Swing-and-miss rate on pitches" />
                <StatCard value={formatPct(latestPitching.chasePct)} label="Chase%" tooltip="Rate at which batters swing at pitches outside the zone" />
                <StatCard value={formatRate(latestPitching.avgExitVelocityAgainst)} label="EV Against" tooltip="Average exit velocity allowed" />
                <StatCard value={formatPct(latestPitching.hardHitPctAgainst)} label="Hard Hit% Against" tooltip="Percentage of hard-hit balls allowed" />
                <StatCard value={latestPitching.avgSpinRate ?? '--'} label="Avg Spin" tooltip="Average spin rate across all pitches" />
              </div>

              {/* gWAR Breakdown for Pitchers */}
              {latestPitching.gwar != null && (
                <>
                  <h4 style={{ fontSize: '14px', color: 'var(--text-light)', marginBottom: '12px' }}>gWAR Breakdown (Grove WAR)</h4>
                  <div className="grid grid-4">
                    <StatCard value={formatWar(latestPitching.gwar)} label="gWAR" tooltip="Grove WAR - transparent, simplified WAR calculation for pitchers" />
                    <StatCard value={formatWar(latestPitching.gwarPitching)} label="Pitching" tooltip="gWAR contribution from pitching (FIP-based)" />
                    <StatCard value={formatWar(latestPitching.gwarReplacement)} label="Replacement" tooltip="gWAR replacement level runs" />
                  </div>
                </>
              )}
            </>
          )}
        </>
      )}

      {!latestBatting && !latestPitching && (
        <p>No stats available for this player.</p>
      )}
    </div>
  );
}

export default PlayerStats;
