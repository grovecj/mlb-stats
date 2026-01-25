-- Team standings table for storing season standings
CREATE TABLE team_standings (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    season INTEGER NOT NULL,
    wins INTEGER NOT NULL DEFAULT 0,
    losses INTEGER NOT NULL DEFAULT 0,
    winning_percentage DECIMAL(4,3),
    games_back VARCHAR(10),
    wild_card_games_back VARCHAR(10),
    division_rank INTEGER,
    league_rank INTEGER,
    wild_card_rank INTEGER,
    runs_scored INTEGER,
    runs_allowed INTEGER,
    run_differential INTEGER,
    streak_code VARCHAR(10),
    home_wins INTEGER,
    home_losses INTEGER,
    away_wins INTEGER,
    away_losses INTEGER,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(team_id, season)
);

CREATE INDEX idx_team_standings_season ON team_standings(season);
CREATE INDEX idx_team_standings_team ON team_standings(team_id);
