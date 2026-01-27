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

export interface TeamBattingAggregate {
  gamesPlayed: number;
  atBats: number;
  runs: number;
  hits: number;
  doubles: number;
  triples: number;
  homeRuns: number;
  rbi: number;
  stolenBases: number;
  walks: number;
  strikeouts: number;
  plateAppearances: number;
  battingAvg: number | null;
  obp: number | null;
  slg: number | null;
  ops: number | null;
}

export interface TeamPitchingAggregate {
  gamesPlayed: number;
  wins: number;
  losses: number;
  saves: number;
  inningsPitched: number;
  hitsAllowed: number;
  earnedRuns: number;
  walks: number;
  strikeouts: number;
  homeRunsAllowed: number;
  qualityStarts: number;
  era: number | null;
  whip: number | null;
  kPer9: number | null;
}

export interface TeamAggregateStats {
  teamId: number;
  season: number;
  batting: TeamBattingAggregate | null;
  pitching: TeamPitchingAggregate | null;
}
