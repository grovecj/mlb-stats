-- Player Splits Statistics
-- Stores home/away, vs LHP/RHP, monthly, and situational splits
-- See issue #122

-- =============================================================================
-- BATTING SPLITS TABLE
-- =============================================================================

CREATE TABLE player_batting_splits (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    team_id BIGINT REFERENCES teams(id),
    season INTEGER NOT NULL,
    split_type VARCHAR(30) NOT NULL,

    -- Counting stats
    games_played INTEGER,
    plate_appearances INTEGER,
    at_bats INTEGER,
    runs INTEGER,
    hits INTEGER,
    doubles INTEGER,
    triples INTEGER,
    home_runs INTEGER,
    rbi INTEGER,
    walks INTEGER,
    strikeouts INTEGER,
    stolen_bases INTEGER,
    caught_stealing INTEGER,
    hit_by_pitch INTEGER,
    sac_flies INTEGER,
    ground_into_dp INTEGER,

    -- Rate stats
    batting_avg DECIMAL(4,3),
    obp DECIMAL(4,3),
    slg DECIMAL(4,3),
    ops DECIMAL(4,3),

    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT uk_batting_splits_player_season_type UNIQUE(player_id, season, split_type)
);

CREATE INDEX idx_batting_splits_player_season ON player_batting_splits(player_id, season);
CREATE INDEX idx_batting_splits_type ON player_batting_splits(split_type);

-- =============================================================================
-- PITCHING SPLITS TABLE
-- =============================================================================

CREATE TABLE player_pitching_splits (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    team_id BIGINT REFERENCES teams(id),
    season INTEGER NOT NULL,
    split_type VARCHAR(30) NOT NULL,

    -- Counting stats
    games_played INTEGER,
    games_started INTEGER,
    innings_pitched DECIMAL(5,1),
    wins INTEGER,
    losses INTEGER,
    saves INTEGER,
    holds INTEGER,
    hits_allowed INTEGER,
    runs_allowed INTEGER,
    earned_runs INTEGER,
    home_runs_allowed INTEGER,
    walks INTEGER,
    strikeouts INTEGER,

    -- Rate stats
    era DECIMAL(5,2),
    whip DECIMAL(4,2),
    k_per_9 DECIMAL(4,2),
    bb_per_9 DECIMAL(4,2),

    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT uk_pitching_splits_player_season_type UNIQUE(player_id, season, split_type)
);

CREATE INDEX idx_pitching_splits_player_season ON player_pitching_splits(player_id, season);
CREATE INDEX idx_pitching_splits_type ON player_pitching_splits(split_type);
