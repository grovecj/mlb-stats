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
  // Advanced Sabermetric Stats
  war: number | null;
  woba: number | null;
  wrcPlus: number | null;
  hardHitPct: number | null;
  barrelPct: number | null;
  avgExitVelocity: number | null;
  avgLaunchAngle: number | null;
  sprintSpeed: number | null;
  xba: number | null;
  xslg: number | null;
  xwoba: number | null;
  kPct: number | null;
  bbPct: number | null;
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
  // Advanced Sabermetric Stats
  war: number | null;
  fip: number | null;
  xfip: number | null;
  siera: number | null;
  kPct: number | null;
  bbPct: number | null;
  gbPct: number | null;
  fbPct: number | null;
  hardHitPctAgainst: number | null;
  avgExitVelocityAgainst: number | null;
  xera: number | null;
  avgSpinRate: number | null;
  whiffPct: number | null;
  chasePct: number | null;
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
