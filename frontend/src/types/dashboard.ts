import { Player } from './player';
import { Team } from './team';

export interface FavoritesDashboard {
  teams: FavoriteTeamDashboard[];
  players: FavoritePlayerDashboard[];
  hasMoreTeams: boolean;
  hasMorePlayers: boolean;
  totalTeamCount: number;
  totalPlayerCount: number;
}

export interface FavoriteTeamDashboard {
  team: Team;
  todaysGame: GameSummary | null;
  nextGame: GameSummary | null;
  standing: TeamStandingSnapshot | null;
}

export interface FavoritePlayerDashboard {
  player: Player;
  currentTeam: Team | null;
  playerType: 'BATTER' | 'PITCHER';
  lastBattingGame: BatterLastGame | null;
  lastPitchingGame: PitcherLastGame | null;
  seasonBatting: BatterSeasonSnapshot | null;
  seasonPitching: PitcherSeasonSnapshot | null;
}

export interface GameSummary {
  id: number;
  gameDate: string;
  scheduledTime: string | null;
  status: string;
  opponent: Team;
  isHome: boolean;
  teamScore: number | null;
  opponentScore: number | null;
  venueName: string;
}

export interface TeamStandingSnapshot {
  wins: number;
  losses: number;
  divisionRank: number | null;
  gamesBack: string | null;
  streakCode: string | null;
  winningPercentage: number | null;
}

export interface BatterLastGame {
  gameId: number;
  gameDate: string;
  opponent: string | null;
  atBats: number | null;
  hits: number | null;
  runs: number | null;
  rbi: number | null;
  homeRuns: number | null;
  walks: number | null;
  strikeouts: number | null;
}

export interface PitcherLastGame {
  gameId: number;
  gameDate: string;
  opponent: string | null;
  inningsPitched: number | null;
  hitsAllowed: number | null;
  earnedRuns: number | null;
  strikeouts: number | null;
  walks: number | null;
  isWinner: boolean | null;
  isLoser: boolean | null;
  isSave: boolean | null;
}

export interface BatterSeasonSnapshot {
  season: number;
  gamesPlayed: number | null;
  atBats: number | null;
  hits: number | null;
  homeRuns: number | null;
  rbi: number | null;
  runs: number | null;
  stolenBases: number | null;
  battingAvg: number | null;
  obp: number | null;
  slg: number | null;
  ops: number | null;
}

export interface PitcherSeasonSnapshot {
  season: number;
  gamesPlayed: number | null;
  gamesStarted: number | null;
  wins: number | null;
  losses: number | null;
  saves: number | null;
  inningsPitched: number | null;
  strikeouts: number | null;
  era: number | null;
  whip: number | null;
  kPer9: number | null;
}
