import { Team } from './team';

export interface ProbablePitcher {
  id: number;
  mlbId: number;
  fullName: string;
  headshotUrl: string | null;
}

export interface Game {
  id: number;
  mlbId: number;
  season: number;
  gameDate: string;
  gameType: string;
  status: string;
  homeTeam: Team;
  awayTeam: Team;
  homeScore: number | null;
  awayScore: number | null;
  venueName: string;
  dayNight: string;
  scheduledInnings: number;
  homeProbablePitcher: ProbablePitcher | null;
  awayProbablePitcher: ProbablePitcher | null;
}

export interface GameBatting {
  id: number;
  playerId: number;
  playerName: string;
  headshotUrl: string | null;
  battingOrder: number | null;
  position: string | null;
  atBats: number | null;
  runs: number | null;
  hits: number | null;
  doubles: number | null;
  triples: number | null;
  homeRuns: number | null;
  rbi: number | null;
  walks: number | null;
  strikeouts: number | null;
  stolenBases: number | null;
}

export interface GamePitching {
  id: number;
  playerId: number;
  playerName: string;
  headshotUrl: string | null;
  inningsPitched: number | null;
  hitsAllowed: number | null;
  runsAllowed: number | null;
  earnedRuns: number | null;
  walks: number | null;
  strikeouts: number | null;
  homeRunsAllowed: number | null;
  pitchesThrown: number | null;
  strikes: number | null;
  isStarter: boolean | null;
  isWinner: boolean | null;
  isLoser: boolean | null;
  isSave: boolean | null;
}

export interface BoxScore {
  game: Game;
  awayBatting: GameBatting[];
  awayPitching: GamePitching[];
  homeBatting: GameBatting[];
  homePitching: GamePitching[];
}

export interface LinescoreInning {
  inning: number;
  awayRuns: number | null;
  homeRuns: number | null;
}

export interface TeamTotals {
  runs: number | null;
  hits: number | null;
  errors: number | null;
}

export interface LiveState {
  currentInning: number | null;
  inningHalf: string | null;
  outs: number | null;
  balls: number | null;
  strikes: number | null;
  runnerOnFirst: boolean | null;
  runnerOnSecond: boolean | null;
  runnerOnThird: boolean | null;
  isLive: boolean;
}

export interface Linescore {
  gameId: number;
  innings: LinescoreInning[];
  awayTotals: TeamTotals;
  homeTotals: TeamTotals;
  liveState: LiveState;
}

// Calendar view types - lightweight for week/month displays
export interface CalendarGame {
  id: number;
  gameDate: string;
  scheduledTime: string | null;
  status: string;
  homeTeamId: number;
  homeTeamAbbr: string;
  awayTeamId: number;
  awayTeamAbbr: string;
  homeScore: number | null;
  awayScore: number | null;
}

export interface GameCount {
  date: string;
  totalGames: number;
  homeGames: number;
  awayGames: number;
}
