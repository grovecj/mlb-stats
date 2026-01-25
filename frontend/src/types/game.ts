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
