export interface Team {
  id: number;
  mlbId: number;
  name: string;
  abbreviation: string;
  locationName: string;
  venueName: string;
  league: string;
  division: string;
  logoUrl: string | null;
}

export interface RosterEntry {
  id: number;
  player: import('./player').Player;
  season: number;
  status: string;
  jerseyNumber: string;
  position: string;
}

export interface TeamStanding {
  id: number;
  team: Team;
  season: number;
  wins: number;
  losses: number;
  winningPercentage: number;
  gamesBack: string;
  wildCardGamesBack: string;
  divisionRank: number;
  leagueRank: number;
  wildCardRank: number;
  runsScored: number;
  runsAllowed: number;
  runDifferential: number;
  streakCode: string;
  homeWins: number;
  homeLosses: number;
  awayWins: number;
  awayLosses: number;
}
