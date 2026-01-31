-- Indexes to support favorites dashboard batch queries

-- Team standings lookup by team IDs and season
CREATE INDEX IF NOT EXISTS idx_team_standings_team_season ON team_standings(team_id, season);

-- Player stats lookup by player IDs and season (for dashboard)
CREATE INDEX IF NOT EXISTS idx_batting_stats_player_season ON player_batting_stats(player_id, season);
CREATE INDEX IF NOT EXISTS idx_pitching_stats_player_season ON player_pitching_stats(player_id, season);

-- User favorites ordered by creation time (for dashboard display order)
CREATE INDEX IF NOT EXISTS idx_user_favorite_teams_user_created ON user_favorite_teams(user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_user_favorite_players_user_created ON user_favorite_players(user_id, created_at);
