import { Team } from './team';

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
