-- Advanced Sabermetric Statistics
-- Adds WAR, wOBA, FIP, Statcast metrics, and other modern analytics columns
-- See issue #123

-- =============================================================================
-- BATTING ADVANCED STATS
-- =============================================================================

-- WAR (Wins Above Replacement)
ALTER TABLE player_batting_stats ADD COLUMN war DECIMAL(4,1);

-- Weighted metrics
ALTER TABLE player_batting_stats ADD COLUMN woba DECIMAL(4,3);
ALTER TABLE player_batting_stats ADD COLUMN wrc_plus INTEGER;

-- Statcast metrics
ALTER TABLE player_batting_stats ADD COLUMN hard_hit_pct DECIMAL(4,1);
ALTER TABLE player_batting_stats ADD COLUMN barrel_pct DECIMAL(4,1);
ALTER TABLE player_batting_stats ADD COLUMN avg_exit_velocity DECIMAL(4,1);
ALTER TABLE player_batting_stats ADD COLUMN avg_launch_angle DECIMAL(4,1);
ALTER TABLE player_batting_stats ADD COLUMN sprint_speed DECIMAL(4,1);

-- Expected stats (xStats)
ALTER TABLE player_batting_stats ADD COLUMN xba DECIMAL(4,3);
ALTER TABLE player_batting_stats ADD COLUMN xslg DECIMAL(4,3);
ALTER TABLE player_batting_stats ADD COLUMN xwoba DECIMAL(4,3);

-- Rate stats (percentage form)
ALTER TABLE player_batting_stats ADD COLUMN k_pct DECIMAL(4,1);
ALTER TABLE player_batting_stats ADD COLUMN bb_pct DECIMAL(4,1);

-- =============================================================================
-- PITCHING ADVANCED STATS
-- =============================================================================

-- WAR (Wins Above Replacement)
ALTER TABLE player_pitching_stats ADD COLUMN war DECIMAL(4,1);

-- FIP family (Fielding Independent Pitching)
ALTER TABLE player_pitching_stats ADD COLUMN fip DECIMAL(5,2);
ALTER TABLE player_pitching_stats ADD COLUMN xfip DECIMAL(5,2);
ALTER TABLE player_pitching_stats ADD COLUMN siera DECIMAL(5,2);

-- Rate stats (percentage form)
ALTER TABLE player_pitching_stats ADD COLUMN k_pct DECIMAL(4,1);
ALTER TABLE player_pitching_stats ADD COLUMN bb_pct DECIMAL(4,1);

-- Batted ball stats
ALTER TABLE player_pitching_stats ADD COLUMN gb_pct DECIMAL(4,1);
ALTER TABLE player_pitching_stats ADD COLUMN fb_pct DECIMAL(4,1);

-- Statcast metrics (against)
ALTER TABLE player_pitching_stats ADD COLUMN hard_hit_pct_against DECIMAL(4,1);
ALTER TABLE player_pitching_stats ADD COLUMN avg_exit_velocity_against DECIMAL(4,1);

-- Expected ERA
ALTER TABLE player_pitching_stats ADD COLUMN xera DECIMAL(5,2);

-- Pitch quality metrics
ALTER TABLE player_pitching_stats ADD COLUMN avg_spin_rate INTEGER;
ALTER TABLE player_pitching_stats ADD COLUMN whiff_pct DECIMAL(4,1);
ALTER TABLE player_pitching_stats ADD COLUMN chase_pct DECIMAL(4,1);

-- =============================================================================
-- LEADERBOARD INDEXES
-- =============================================================================

-- Batting leaderboard indexes (advanced stats)
CREATE INDEX IF NOT EXISTS idx_batting_stats_season_war ON player_batting_stats(season, war DESC);
CREATE INDEX IF NOT EXISTS idx_batting_stats_season_woba ON player_batting_stats(season, woba DESC);
CREATE INDEX IF NOT EXISTS idx_batting_stats_season_wrc_plus ON player_batting_stats(season, wrc_plus DESC);
CREATE INDEX IF NOT EXISTS idx_batting_stats_season_exit_velo ON player_batting_stats(season, avg_exit_velocity DESC);
CREATE INDEX IF NOT EXISTS idx_batting_stats_season_barrel ON player_batting_stats(season, barrel_pct DESC);

-- Pitching leaderboard indexes (advanced stats)
CREATE INDEX IF NOT EXISTS idx_pitching_stats_season_war ON player_pitching_stats(season, war DESC);
CREATE INDEX IF NOT EXISTS idx_pitching_stats_season_fip ON player_pitching_stats(season, fip ASC);
CREATE INDEX IF NOT EXISTS idx_pitching_stats_season_xfip ON player_pitching_stats(season, xfip ASC);
CREATE INDEX IF NOT EXISTS idx_pitching_stats_season_xera ON player_pitching_stats(season, xera ASC);
CREATE INDEX IF NOT EXISTS idx_pitching_stats_season_whiff ON player_pitching_stats(season, whiff_pct DESC);
