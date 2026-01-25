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
  winningPercentage: number | null;
  gamesBack: string | null;
  wildCardGamesBack: string | null;
  divisionRank: number | null;
  leagueRank: number | null;
  wildCardRank: number | null;
  runsScored: number | null;
  runsAllowed: number | null;
  runDifferential: number | null;
  streakCode: string | null;
  homeWins: number | null;
  homeLosses: number | null;
  awayWins: number | null;
  awayLosses: number | null;
}
