-- User favorite teams
CREATE TABLE user_favorite_teams (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    team_id BIGINT NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, team_id)
);

-- User favorite players
CREATE TABLE user_favorite_players (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, player_id)
);

-- Indexes for faster queries
CREATE INDEX idx_user_favorite_teams_user ON user_favorite_teams(user_id);
CREATE INDEX idx_user_favorite_players_user ON user_favorite_players(user_id);
