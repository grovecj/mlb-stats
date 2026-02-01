import { Player } from './player';
import { Team } from './team';

export interface BattingStats {
  id: number;
  player: Player;
  team: Team;
  season: number;
  gameType: string;
  gamesPlayed: number;
  atBats: number;
  runs: number;
  hits: number;
  doubles: number;
  triples: number;
  homeRuns: number;
  rbi: number;
  stolenBases: number;
  caughtStealing: number;
  walks: number;
  strikeouts: number;
  battingAvg: number;
  obp: number;
  slg: number;
  ops: number;
  babip: number;
  iso: number;
  plateAppearances: number;
  totalBases: number;
  extraBaseHits: number;
}

export interface PitchingStats {
  id: number;
  player: Player;
  team: Team;
  season: number;
  gameType: string;
  gamesPlayed: number;
  gamesStarted: number;
  wins: number;
  losses: number;
  saves: number;
  holds: number;
  inningsPitched: number;
  hitsAllowed: number;
  runsAllowed: number;
  earnedRuns: number;
  homeRunsAllowed: number;
  walks: number;
  strikeouts: number;
  era: number;
  whip: number;
  kPer9: number;
  bbPer9: number;
  hPer9: number;
  completeGames: number;
  shutouts: number;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface BattingGameLog {
  gameId: number;
  gameDate: string;
  opponent: string | null;
  opponentAbbreviation: string | null;
  opponentId: number | null;
  isHome: boolean;
  result: string | null;
  teamScore: number | null;
  opponentScore: number | null;
  atBats: number;
  runs: number;
  hits: number;
  doubles: number;
  triples: number;
  homeRuns: number;
  rbi: number;
  walks: number;
  strikeouts: number;
  stolenBases: number;
  battingOrder: number | null;
  position: string | null;
}

export interface PitchingGameLog {
  gameId: number;
  gameDate: string;
  opponent: string | null;
  opponentAbbreviation: string | null;
  opponentId: number | null;
  isHome: boolean;
  result: string | null;
  teamScore: number | null;
  opponentScore: number | null;
  decision: string | null;
  inningsPitched: number;
  hitsAllowed: number;
  runsAllowed: number;
  earnedRuns: number;
  walks: number;
  strikeouts: number;
  homeRunsAllowed: number;
  pitchesThrown: number | null;
  strikes: number | null;
  isStarter: boolean;
}

export type SplitType =
  | 'HOME' | 'AWAY'
  | 'VS_LHP' | 'VS_RHP'
  | 'VS_LHB' | 'VS_RHB'
  | 'FIRST_HALF' | 'SECOND_HALF'
  | 'MONTH_MAR' | 'MONTH_APR' | 'MONTH_MAY' | 'MONTH_JUN'
  | 'MONTH_JUL' | 'MONTH_AUG' | 'MONTH_SEP' | 'MONTH_OCT'
  | 'DAY' | 'NIGHT'
  | 'RUNNERS_ON' | 'RISP' | 'BASES_EMPTY';

export interface BattingSplit {
  id: number;
  playerId: number;
  teamId: number | null;
  season: number;
  splitType: SplitType;
  splitTypeDisplay: string;
  gamesPlayed: number;
  plateAppearances: number;
  atBats: number;
  runs: number;
  hits: number;
  doubles: number;
  triples: number;
  homeRuns: number;
  rbi: number;
  walks: number;
  strikeouts: number;
  stolenBases: number;
  battingAvg: number | null;
  obp: number | null;
  slg: number | null;
  ops: number | null;
}

export interface PitchingSplit {
  id: number;
  playerId: number;
  teamId: number | null;
  season: number;
  splitType: SplitType;
  splitTypeDisplay: string;
  gamesPlayed: number;
  gamesStarted: number;
  inningsPitched: number | null;
  wins: number;
  losses: number;
  saves: number;
  holds: number;
  hitsAllowed: number;
  earnedRuns: number;
  walks: number;
  strikeouts: number;
  era: number | null;
  whip: number | null;
  kPer9: number | null;
  bbPer9: number | null;
}
