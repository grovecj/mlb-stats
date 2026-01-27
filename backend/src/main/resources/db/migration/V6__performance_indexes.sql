-- Performance indexes for Phase 2 features
-- Supports: Leaderboards (#85), Advanced Search (#89), Calendar (#92), Game Logs (#86), Team Stats (#87)

-- =============================================================================
-- LEADERBOARD INDEXES (Batting)
-- =============================================================================

-- Home run leaders
CREATE INDEX idx_batting_stats_season_hr ON player_batting_stats(season, home_runs DESC);

-- RBI leaders
CREATE INDEX idx_batting_stats_season_rbi ON player_batting_stats(season, rbi DESC);

-- Hits leaders
CREATE INDEX idx_batting_stats_season_hits ON player_batting_stats(season, hits DESC);

-- Stolen base leaders
CREATE INDEX idx_batting_stats_season_sb ON player_batting_stats(season, stolen_bases DESC);

-- OPS leaders (includes qualified check in query)
CREATE INDEX idx_batting_stats_season_ops ON player_batting_stats(season, ops DESC);

-- Batting average leaders (includes qualified check in query)
CREATE INDEX idx_batting_stats_season_avg ON player_batting_stats(season, batting_avg DESC);

-- =============================================================================
-- LEADERBOARD INDEXES (Pitching)
-- =============================================================================

-- Wins leaders
CREATE INDEX idx_pitching_stats_season_wins ON player_pitching_stats(season, wins DESC);

-- Strikeout leaders
CREATE INDEX idx_pitching_stats_season_k ON player_pitching_stats(season, strikeouts DESC);

-- Saves leaders
CREATE INDEX idx_pitching_stats_season_saves ON player_pitching_stats(season, saves DESC);

-- ERA leaders (low is better, includes qualified check in query)
CREATE INDEX idx_pitching_stats_season_era ON player_pitching_stats(season, era ASC);

-- WHIP leaders (low is better)
CREATE INDEX idx_pitching_stats_season_whip ON player_pitching_stats(season, whip ASC);

-- =============================================================================
-- PLAYER SEARCH INDEXES
-- =============================================================================

-- Filter by position
CREATE INDEX idx_players_position ON players(position);

-- Filter by position type (Pitcher, Hitter, Two-Way)
CREATE INDEX idx_players_position_type ON players(position_type);

-- Filter by active status
CREATE INDEX idx_players_active ON players(active);

-- Filter by batting hand
CREATE INDEX idx_players_bats ON players(bats);

-- Filter by throwing hand
CREATE INDEX idx_players_throws ON players(throws);

-- Composite: position + active (common filter combo)
CREATE INDEX idx_players_position_active ON players(position, active);

-- Name search (case-insensitive via application layer)
CREATE INDEX idx_players_last_name ON players(last_name);
CREATE INDEX idx_players_full_name ON players(full_name);

-- =============================================================================
-- ROSTER INDEXES
-- =============================================================================

-- Team roster by season with player lookup
CREATE INDEX idx_rosters_team_season_player ON team_rosters(team_id, season, player_id);

-- Player roster history
CREATE INDEX idx_rosters_player_season ON team_rosters(player_id, season);

-- =============================================================================
-- CALENDAR/SCHEDULE INDEXES
-- =============================================================================

-- Season + date for calendar views
CREATE INDEX idx_games_season_date ON games(season, game_date);

-- Date range queries
CREATE INDEX idx_games_date_season ON games(game_date, season);

-- Team-specific schedule (home games)
CREATE INDEX idx_games_home_date ON games(home_team_id, game_date);

-- Team-specific schedule (away games)
CREATE INDEX idx_games_away_date ON games(away_team_id, game_date);

-- =============================================================================
-- GAME LOG INDEXES
-- =============================================================================

-- Player batting game log (chronological)
CREATE INDEX idx_game_batting_player ON player_game_batting(player_id);

-- Player pitching game log (chronological)
CREATE INDEX idx_game_pitching_player ON player_game_pitching(player_id);

-- =============================================================================
-- TEAM AGGREGATE STATS INDEXES
-- =============================================================================

-- Team batting stats by season
CREATE INDEX idx_batting_stats_team_season ON player_batting_stats(team_id, season);

-- Team pitching stats by season
CREATE INDEX idx_pitching_stats_team_season ON player_pitching_stats(team_id, season);
