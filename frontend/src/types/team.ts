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
