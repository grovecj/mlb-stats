-- gWAR (Grove WAR) and Sabermetrics Enhancement
-- Adds gWAR calculation columns and league constants for transparent WAR calculations
-- See issue #170

-- =============================================================================
-- LEAGUE CONSTANTS TABLE
-- Stores season-specific constants needed for gWAR calculations
-- =============================================================================

CREATE TABLE league_constants (
    id BIGSERIAL PRIMARY KEY,
    season INTEGER NOT NULL UNIQUE,
    lg_woba DECIMAL(5,4) NOT NULL,
    woba_scale DECIMAL(5,4) NOT NULL,
    lg_r_per_pa DECIMAL(6,5) NOT NULL,
    fip_constant DECIMAL(4,2) NOT NULL,
    runs_per_win DECIMAL(4,2) DEFAULT 10.0,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Seed recent years with league constants
-- Source: FanGraphs guts! constants
-- Note: 2025/2026 values are estimates based on recent trends; update when official data available
INSERT INTO league_constants (season, lg_woba, woba_scale, lg_r_per_pa, fip_constant) VALUES
(2026, 0.3120, 1.180, 0.1130, 3.14),
(2025, 0.3110, 1.178, 0.1125, 3.14),
(2024, 0.3100, 1.177, 0.1120, 3.15),
(2023, 0.3180, 1.169, 0.1100, 3.10),
(2022, 0.3100, 1.157, 0.1060, 3.15),
(2021, 0.3180, 1.153, 0.1110, 3.13),
(2020, 0.3200, 1.167, 0.1100, 3.16);

-- =============================================================================
-- BATTING STATS: gWAR COMPONENTS
-- =============================================================================

-- gWAR total for position players
ALTER TABLE player_batting_stats ADD COLUMN gwar DECIMAL(4,1);

-- gWAR components (stored in runs above average)
ALTER TABLE player_batting_stats ADD COLUMN gwar_batting DECIMAL(5,1);
ALTER TABLE player_batting_stats ADD COLUMN gwar_baserunning DECIMAL(5,1);
ALTER TABLE player_batting_stats ADD COLUMN gwar_fielding DECIMAL(5,1);
ALTER TABLE player_batting_stats ADD COLUMN gwar_positional DECIMAL(5,1);
ALTER TABLE player_batting_stats ADD COLUMN gwar_replacement DECIMAL(5,1);

-- OAA (Outs Above Average) from Baseball Savant - used for fielding component
ALTER TABLE player_batting_stats ADD COLUMN oaa INTEGER;

-- =============================================================================
-- PITCHING STATS: gWAR COMPONENTS
-- =============================================================================

-- gWAR total for pitchers
ALTER TABLE player_pitching_stats ADD COLUMN gwar DECIMAL(4,1);

-- gWAR components (stored in runs above average)
ALTER TABLE player_pitching_stats ADD COLUMN gwar_pitching DECIMAL(5,1);
ALTER TABLE player_pitching_stats ADD COLUMN gwar_replacement DECIMAL(5,1);

-- =============================================================================
-- LEADERBOARD INDEXES
-- =============================================================================

-- Batting gWAR leaderboard
CREATE INDEX idx_batting_gwar ON player_batting_stats(season, gwar DESC NULLS LAST);

-- Pitching gWAR leaderboard
CREATE INDEX idx_pitching_gwar ON player_pitching_stats(season, gwar DESC NULLS LAST);

-- OAA leaderboard
CREATE INDEX idx_batting_oaa ON player_batting_stats(season, oaa DESC NULLS LAST);
