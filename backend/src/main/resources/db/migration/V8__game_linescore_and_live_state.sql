-- Game linescore and live state schema
-- Stores inning-by-inning scoring data and live game state

-- Create game_innings table for linescore data
CREATE TABLE game_innings (
    id BIGSERIAL PRIMARY KEY,
    game_id BIGINT NOT NULL REFERENCES games(id) ON DELETE CASCADE,
    inning_number INTEGER NOT NULL,
    away_runs INTEGER DEFAULT 0,
    home_runs INTEGER DEFAULT 0,
    away_hits INTEGER,
    home_hits INTEGER,
    away_errors INTEGER,
    home_errors INTEGER,
    away_left_on_base INTEGER,
    home_left_on_base INTEGER,
    UNIQUE(game_id, inning_number)
);

CREATE INDEX idx_game_innings_game ON game_innings(game_id);

-- Extend games table with live state columns
ALTER TABLE games ADD COLUMN current_inning INTEGER;
ALTER TABLE games ADD COLUMN inning_half VARCHAR(10);
ALTER TABLE games ADD COLUMN outs INTEGER;
ALTER TABLE games ADD COLUMN balls INTEGER;
ALTER TABLE games ADD COLUMN strikes INTEGER;
ALTER TABLE games ADD COLUMN runner_on_first BOOLEAN DEFAULT FALSE;
ALTER TABLE games ADD COLUMN runner_on_second BOOLEAN DEFAULT FALSE;
ALTER TABLE games ADD COLUMN runner_on_third BOOLEAN DEFAULT FALSE;
ALTER TABLE games ADD COLUMN home_hits INTEGER;
ALTER TABLE games ADD COLUMN away_hits INTEGER;
ALTER TABLE games ADD COLUMN home_errors INTEGER;
ALTER TABLE games ADD COLUMN away_errors INTEGER;
