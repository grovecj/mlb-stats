import { Team, RosterEntry, TeamStanding } from '../types/team';
import { Player } from '../types/player';
import { Game, BoxScore } from '../types/game';
import { BattingStats, PitchingStats, PageResponse } from '../types/stats';

const API_BASE = '/api';

function getCsrfToken(): string | null {
  const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
  return match ? decodeURIComponent(match[1]) : null;
}

async function fetchJson<T>(url: string): Promise<T> {
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }
  return response.json();
}

async function postJson<T>(url: string): Promise<T> {
  const csrfToken = getCsrfToken();
  const headers: HeadersInit = {};
  if (csrfToken) {
    headers['X-XSRF-TOKEN'] = csrfToken;
  }
  const response = await fetch(url, {
    method: 'POST',
    credentials: 'include',
    headers,
  });
  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }
  return response.json();
}

// Search
export interface SearchResult {
  teams: Team[];
  players: Player[];
}

export async function globalSearch(query: string, limit = 5): Promise<SearchResult> {
  const params = new URLSearchParams({ q: query, limit: String(limit) });
  return fetchJson<SearchResult>(`${API_BASE}/search?${params}`);
}

// Teams
export async function getTeams(): Promise<Team[]> {
  return fetchJson<Team[]>(`${API_BASE}/teams`);
}

export async function getTeam(id: number): Promise<Team> {
  return fetchJson<Team>(`${API_BASE}/teams/${id}`);
}

export async function getTeamRoster(id: number, season?: number): Promise<RosterEntry[]> {
  const params = season ? `?season=${season}` : '';
  return fetchJson<RosterEntry[]>(`${API_BASE}/teams/${id}/roster${params}`);
}

export async function getTeamGames(id: number, season?: number): Promise<Game[]> {
  const params = season ? `?season=${season}` : '';
  return fetchJson<Game[]>(`${API_BASE}/teams/${id}/games${params}`);
}

export async function getTeamStats(id: number, season?: number): Promise<BattingStats[]> {
  const params = season ? `?season=${season}` : '';
  return fetchJson<BattingStats[]>(`${API_BASE}/teams/${id}/stats${params}`);
}

export async function getStandings(season?: number): Promise<TeamStanding[]> {
  const params = season ? `?season=${season}` : '';
  return fetchJson<TeamStanding[]>(`${API_BASE}/teams/standings${params}`);
}

export async function getTeamStanding(id: number, season?: number): Promise<TeamStanding | null> {
  const params = season ? `?season=${season}` : '';
  try {
    return await fetchJson<TeamStanding>(`${API_BASE}/teams/${id}/standing${params}`);
  } catch {
    return null;
  }
}

// Players
export async function getPlayers(page = 0, size = 20, search?: string): Promise<PageResponse<Player>> {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  if (search) params.set('search', search);
  return fetchJson<PageResponse<Player>>(`${API_BASE}/players?${params}`);
}

export async function getPlayer(id: number): Promise<Player> {
  return fetchJson<Player>(`${API_BASE}/players/${id}`);
}

export async function getPlayerBattingStats(id: number, season?: number): Promise<BattingStats[]> {
  const params = season ? `?season=${season}` : '';
  return fetchJson<BattingStats[]>(`${API_BASE}/players/${id}/batting-stats${params}`);
}

export async function getPlayerPitchingStats(id: number, season?: number): Promise<PitchingStats[]> {
  const params = season ? `?season=${season}` : '';
  return fetchJson<PitchingStats[]>(`${API_BASE}/players/${id}/pitching-stats${params}`);
}

export async function getHomeRunLeaders(season?: number, limit = 10): Promise<BattingStats[]> {
  const params = new URLSearchParams({ limit: String(limit) });
  if (season) params.set('season', String(season));
  return fetchJson<BattingStats[]>(`${API_BASE}/players/leaders/home-runs?${params}`);
}

export async function getBattingAverageLeaders(season?: number, limit = 10): Promise<BattingStats[]> {
  const params = new URLSearchParams({ limit: String(limit), minAtBats: '100' });
  if (season) params.set('season', String(season));
  return fetchJson<BattingStats[]>(`${API_BASE}/players/leaders/batting-average?${params}`);
}

export async function getWinsLeaders(season?: number, limit = 10): Promise<PitchingStats[]> {
  const params = new URLSearchParams({ limit: String(limit) });
  if (season) params.set('season', String(season));
  return fetchJson<PitchingStats[]>(`${API_BASE}/players/leaders/wins?${params}`);
}

export async function getStrikeoutLeaders(season?: number, limit = 10): Promise<PitchingStats[]> {
  const params = new URLSearchParams({ limit: String(limit) });
  if (season) params.set('season', String(season));
  return fetchJson<PitchingStats[]>(`${API_BASE}/players/leaders/strikeouts?${params}`);
}

// Games
export async function getGames(options: {
  season?: number;
  date?: string;
  teamId?: number;
  page?: number;
  size?: number;
} = {}): Promise<Game[] | PageResponse<Game>> {
  const params = new URLSearchParams();
  if (options.season) params.set('season', String(options.season));
  if (options.date) params.set('date', options.date);
  if (options.teamId) params.set('teamId', String(options.teamId));
  if (options.page !== undefined) params.set('page', String(options.page));
  if (options.size) params.set('size', String(options.size));
  return fetchJson(`${API_BASE}/games?${params}`);
}

export async function getGame(id: number): Promise<Game> {
  return fetchJson<Game>(`${API_BASE}/games/${id}`);
}

export async function getGameBoxScore(id: number): Promise<BoxScore> {
  return fetchJson<BoxScore>(`${API_BASE}/games/${id}/boxscore`);
}

export async function getTodaysGames(): Promise<Game[]> {
  return fetchJson<Game[]>(`${API_BASE}/games/today`);
}

// Ingestion
export async function triggerFullSync(season?: number): Promise<{ status: string }> {
  const params = season ? `?season=${season}` : '';
  return postJson(`${API_BASE}/ingestion/full-sync${params}`);
}

export async function triggerTeamsSync(): Promise<{ status: string }> {
  return postJson(`${API_BASE}/ingestion/teams`);
}

export async function triggerRostersSync(season?: number): Promise<{ status: string }> {
  const params = season ? `?season=${season}` : '';
  return postJson(`${API_BASE}/ingestion/rosters${params}`);
}

export async function triggerGamesSync(season?: number): Promise<{ status: string }> {
  const params = season ? `?season=${season}` : '';
  return postJson(`${API_BASE}/ingestion/games${params}`);
}

export async function triggerStatsSync(season?: number): Promise<{ status: string }> {
  const params = season ? `?season=${season}` : '';
  return postJson(`${API_BASE}/ingestion/stats${params}`);
}

export async function triggerIncompletePlayersSync(): Promise<{ status: string; synced: string }> {
  return postJson(`${API_BASE}/ingestion/players/incomplete`);
}

export async function triggerStandingsSync(season?: number): Promise<{ status: string; season: string; teams: string }> {
  const params = season ? `?season=${season}` : '';
  return postJson(`${API_BASE}/ingestion/standings${params}`);
}

export async function triggerBoxScoresSync(season?: number): Promise<{ status: string; season: string; games: string }> {
  const params = season ? `?season=${season}` : '';
  return postJson(`${API_BASE}/ingestion/boxscores${params}`);
}

// Data Manager
export interface SeasonData {
  season: number;
  gamesCount: number;
  battingStatsCount: number;
  pitchingStatsCount: number;
  rosterEntriesCount: number;
  standingsCount: number;
  isCurrent: boolean;
}

export async function getSyncedSeasons(): Promise<SeasonData[]> {
  return fetchJson<SeasonData[]>(`${API_BASE}/data-manager/seasons`);
}

export async function getAvailableSeasons(): Promise<number[]> {
  return fetchJson<number[]>(`${API_BASE}/data-manager/seasons/available`);
}

export async function deleteSeasonData(season: number): Promise<{ status: string }> {
  const csrfToken = getCsrfToken();
  const headers: HeadersInit = {};
  if (csrfToken) {
    headers['X-XSRF-TOKEN'] = csrfToken;
  }
  const response = await fetch(`${API_BASE}/data-manager/seasons/${season}`, {
    method: 'DELETE',
    credentials: 'include',
    headers,
  });
  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }
  return response.json();
}

// Admin - User Management
export interface AdminUser {
  id: number;
  email: string;
  name: string;
  pictureUrl: string;
  role: 'USER' | 'ADMIN' | 'OWNER';
  lastLoginAt: string | null;
}

export async function getUsers(): Promise<AdminUser[]> {
  const response = await fetch(`${API_BASE}/admin/users`, {
    credentials: 'include',
  });
  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }
  return response.json();
}

export async function updateUserRole(userId: number, role: 'USER' | 'ADMIN'): Promise<{ status: string }> {
  const csrfToken = getCsrfToken();
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };
  if (csrfToken) {
    headers['X-XSRF-TOKEN'] = csrfToken;
  }
  const response = await fetch(`${API_BASE}/admin/users/${userId}/role`, {
    method: 'PUT',
    headers,
    credentials: 'include',
    body: JSON.stringify({ role }),
  });
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.error || `API error: ${response.status}`);
  }
  return response.json();
}

// Favorites
export async function getFavoriteTeams(): Promise<Team[]> {
  return fetchJson<Team[]>(`${API_BASE}/favorites/teams`);
}

export async function isTeamFavorite(teamId: number): Promise<boolean> {
  const result = await fetchJson<{ isFavorite: boolean }>(`${API_BASE}/favorites/teams/${teamId}/status`);
  return result.isFavorite;
}

export async function addTeamFavorite(teamId: number): Promise<{ status: string }> {
  return postJson(`${API_BASE}/favorites/teams/${teamId}`);
}

export async function removeTeamFavorite(teamId: number): Promise<{ status: string }> {
  const csrfToken = getCsrfToken();
  const headers: HeadersInit = {};
  if (csrfToken) {
    headers['X-XSRF-TOKEN'] = csrfToken;
  }
  const response = await fetch(`${API_BASE}/favorites/teams/${teamId}`, {
    method: 'DELETE',
    credentials: 'include',
    headers,
  });
  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }
  return response.json();
}

export async function getFavoritePlayers(): Promise<Player[]> {
  return fetchJson<Player[]>(`${API_BASE}/favorites/players`);
}

export async function isPlayerFavorite(playerId: number): Promise<boolean> {
  const result = await fetchJson<{ isFavorite: boolean }>(`${API_BASE}/favorites/players/${playerId}/status`);
  return result.isFavorite;
}

export async function addPlayerFavorite(playerId: number): Promise<{ status: string }> {
  return postJson(`${API_BASE}/favorites/players/${playerId}`);
}

export async function removePlayerFavorite(playerId: number): Promise<{ status: string }> {
  const csrfToken = getCsrfToken();
  const headers: HeadersInit = {};
  if (csrfToken) {
    headers['X-XSRF-TOKEN'] = csrfToken;
  }
  const response = await fetch(`${API_BASE}/favorites/players/${playerId}`, {
    method: 'DELETE',
    credentials: 'include',
    headers,
  });
  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }
  return response.json();
}
