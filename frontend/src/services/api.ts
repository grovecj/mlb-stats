import { Team, RosterEntry } from '../types/team';
import { Player } from '../types/player';
import { Game } from '../types/game';
import { BattingStats, PitchingStats, PageResponse } from '../types/stats';

const API_BASE = '/api';

async function fetchJson<T>(url: string): Promise<T> {
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }
  return response.json();
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

export async function getTodaysGames(): Promise<Game[]> {
  return fetchJson<Game[]>(`${API_BASE}/games/today`);
}

// Ingestion
export async function triggerFullSync(season?: number): Promise<{ status: string }> {
  const params = season ? `?season=${season}` : '';
  const response = await fetch(`${API_BASE}/ingestion/full-sync${params}`, {
    method: 'POST',
  });
  return response.json();
}

export async function triggerTeamsSync(): Promise<{ status: string }> {
  const response = await fetch(`${API_BASE}/ingestion/teams`, { method: 'POST' });
  return response.json();
}
