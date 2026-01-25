-- MLB Stats Initial Schema

-- Core entities
CREATE TABLE teams (
    id SERIAL PRIMARY KEY,
    mlb_id INTEGER UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(10) NOT NULL,
    location_name VARCHAR(100),
    venue_name VARCHAR(100),
    league VARCHAR(50),
    division VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE players (
    id SERIAL PRIMARY KEY,
    mlb_id INTEGER UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    jersey_number VARCHAR(10),
    position VARCHAR(20),
    position_type VARCHAR(20),
    bats VARCHAR(10),
    throws VARCHAR(10),
    birth_date DATE,
    height VARCHAR(20),
    weight INTEGER,
    mlb_debut_date DATE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Roster relationship (season-aware)
CREATE TABLE team_rosters (
    id SERIAL PRIMARY KEY,
    team_id INTEGER REFERENCES teams(id),
    player_id INTEGER REFERENCES players(id),
    season INTEGER NOT NULL,
    status VARCHAR(50),
    jersey_number VARCHAR(10),
    position VARCHAR(20),
    start_date DATE,
    end_date DATE,
    UNIQUE(team_id, player_id, season, start_date)
);

CREATE TABLE games (
    id SERIAL PRIMARY KEY,
    mlb_id INTEGER UNIQUE NOT NULL,
    season INTEGER NOT NULL,
    game_date DATE NOT NULL,
    game_type VARCHAR(10),
    status VARCHAR(20),
    home_team_id INTEGER REFERENCES teams(id),
    away_team_id INTEGER REFERENCES teams(id),
    home_score INTEGER,
    away_score INTEGER,
    venue_name VARCHAR(100),
    day_night VARCHAR(10),
    scheduled_innings INTEGER DEFAULT 9,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Player season statistics
CREATE TABLE player_batting_stats (
    id SERIAL PRIMARY KEY,
    player_id INTEGER REFERENCES players(id),
    team_id INTEGER REFERENCES teams(id),
    season INTEGER NOT NULL,
    game_type VARCHAR(10) DEFAULT 'R',
    games_played INTEGER,
    at_bats INTEGER,
    runs INTEGER,
    hits INTEGER,
    doubles INTEGER,
    triples INTEGER,
    home_runs INTEGER,
    rbi INTEGER,
    stolen_bases INTEGER,
    caught_stealing INTEGER,
    walks INTEGER,
    strikeouts INTEGER,
    batting_avg DECIMAL(4,3),
    obp DECIMAL(4,3),
    slg DECIMAL(4,3),
    ops DECIMAL(4,3),
    babip DECIMAL(4,3),
    iso DECIMAL(4,3),
    plate_appearances INTEGER,
    total_bases INTEGER,
    extra_base_hits INTEGER,
    intentional_walks INTEGER,
    hit_by_pitch INTEGER,
    sac_flies INTEGER,
    ground_into_dp INTEGER,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(player_id, team_id, season, game_type)
);

CREATE TABLE player_pitching_stats (
    id SERIAL PRIMARY KEY,
    player_id INTEGER REFERENCES players(id),
    team_id INTEGER REFERENCES teams(id),
    season INTEGER NOT NULL,
    game_type VARCHAR(10) DEFAULT 'R',
    games_played INTEGER,
    games_started INTEGER,
    wins INTEGER,
    losses INTEGER,
    saves INTEGER,
    holds INTEGER,
    innings_pitched DECIMAL(5,1),
    hits_allowed INTEGER,
    runs_allowed INTEGER,
    earned_runs INTEGER,
    home_runs_allowed INTEGER,
    walks INTEGER,
    strikeouts INTEGER,
    era DECIMAL(5,2),
    whip DECIMAL(4,2),
    k_per_9 DECIMAL(4,2),
    bb_per_9 DECIMAL(4,2),
    h_per_9 DECIMAL(4,2),
    pitches_thrown INTEGER,
    strikes INTEGER,
    balls INTEGER,
    complete_games INTEGER,
    shutouts INTEGER,
    quality_starts INTEGER,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(player_id, team_id, season, game_type)
);

-- Game-level player stats
CREATE TABLE player_game_batting (
    id SERIAL PRIMARY KEY,
    player_id INTEGER REFERENCES players(id),
    game_id INTEGER REFERENCES games(id),
    team_id INTEGER REFERENCES teams(id),
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
    batting_order INTEGER,
    position_played VARCHAR(20),
    UNIQUE(player_id, game_id)
);

CREATE TABLE player_game_pitching (
    id SERIAL PRIMARY KEY,
    player_id INTEGER REFERENCES players(id),
    game_id INTEGER REFERENCES games(id),
    team_id INTEGER REFERENCES teams(id),
    innings_pitched DECIMAL(4,1),
    hits_allowed INTEGER,
    runs_allowed INTEGER,
    earned_runs INTEGER,
    walks INTEGER,
    strikeouts INTEGER,
    home_runs_allowed INTEGER,
    pitches_thrown INTEGER,
    strikes INTEGER,
    is_starter BOOLEAN,
    is_winner BOOLEAN,
    is_loser BOOLEAN,
    is_save BOOLEAN,
    UNIQUE(player_id, game_id)
);

-- Indexes for common queries
CREATE INDEX idx_games_season ON games(season);
CREATE INDEX idx_games_date ON games(game_date);
CREATE INDEX idx_games_home_team ON games(home_team_id);
CREATE INDEX idx_games_away_team ON games(away_team_id);
CREATE INDEX idx_batting_stats_player_season ON player_batting_stats(player_id, season);
CREATE INDEX idx_pitching_stats_player_season ON player_pitching_stats(player_id, season);
CREATE INDEX idx_team_rosters_season ON team_rosters(team_id, season);
CREATE INDEX idx_game_batting_game ON player_game_batting(game_id);
CREATE INDEX idx_game_pitching_game ON player_game_pitching(game_id);
