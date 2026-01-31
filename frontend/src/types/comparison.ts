import { Player } from './player';

export interface ComparisonBattingStats {
  gamesPlayed: number | null;
  atBats: number | null;
  runs: number | null;
  hits: number | null;
  doubles: number | null;
  triples: number | null;
  homeRuns: number | null;
  rbi: number | null;
  stolenBases: number | null;
  caughtStealing: number | null;
  walks: number | null;
  strikeouts: number | null;
  battingAvg: number | null;
  obp: number | null;
  slg: number | null;
  ops: number | null;
  plateAppearances: number | null;
  totalBases: number | null;
  extraBaseHits: number | null;
}

export interface ComparisonPitchingStats {
  gamesPlayed: number | null;
  gamesStarted: number | null;
  wins: number | null;
  losses: number | null;
  saves: number | null;
  holds: number | null;
  inningsPitched: number | null;
  hitsAllowed: number | null;
  runsAllowed: number | null;
  earnedRuns: number | null;
  homeRunsAllowed: number | null;
  walks: number | null;
  strikeouts: number | null;
  era: number | null;
  whip: number | null;
  kPer9: number | null;
  bbPer9: number | null;
  completeGames: number | null;
  shutouts: number | null;
}

export interface PlayerComparisonEntry {
  player: Player;
  season: number | null;
  battingStats: ComparisonBattingStats | null;
  pitchingStats: ComparisonPitchingStats | null;
}

export interface ComparisonLeaders {
  batting: Record<string, number>;
  pitching: Record<string, number>;
}

export interface PlayerComparisonResponse {
  mode: 'season' | 'career';
  players: PlayerComparisonEntry[];
  leaders: ComparisonLeaders;
}

export interface PlayerSelection {
  playerId: number;
  player?: Player;
  season: number;
}
