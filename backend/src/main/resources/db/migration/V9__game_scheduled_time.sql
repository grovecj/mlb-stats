-- Add scheduled game time for calendar views
ALTER TABLE games ADD COLUMN IF NOT EXISTS scheduled_time TIME;

-- Create index for time-based ordering within a date
CREATE INDEX IF NOT EXISTS idx_games_date_time ON games(game_date, scheduled_time);
