-- Add probable pitcher columns to games table
-- These track the expected starting pitchers for each game

ALTER TABLE games ADD COLUMN home_probable_pitcher_id BIGINT;
ALTER TABLE games ADD COLUMN away_probable_pitcher_id BIGINT;

-- Add foreign key constraints
ALTER TABLE games ADD CONSTRAINT fk_games_home_probable_pitcher
    FOREIGN KEY (home_probable_pitcher_id) REFERENCES players(id);
ALTER TABLE games ADD CONSTRAINT fk_games_away_probable_pitcher
    FOREIGN KEY (away_probable_pitcher_id) REFERENCES players(id);

-- Indexes for looking up games by probable pitcher
CREATE INDEX idx_games_home_probable_pitcher ON games(home_probable_pitcher_id);
CREATE INDEX idx_games_away_probable_pitcher ON games(away_probable_pitcher_id);
