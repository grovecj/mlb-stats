import type { Game } from '../../../types/game'
import { mockTeams } from './teams'

export const mockGames: Game[] = [
  {
    id: 1,
    mlbId: 745123,
    season: 2024,
    gameDate: '2024-04-01',
    gameType: 'R',
    status: 'Final',
    homeTeam: mockTeams[0], // Yankees
    awayTeam: mockTeams[1], // Red Sox
    homeScore: 5,
    awayScore: 3,
    venueName: 'Yankee Stadium',
    dayNight: 'night',
    scheduledInnings: 9,
    homeProbablePitcher: null,
    awayProbablePitcher: null,
  },
  {
    id: 2,
    mlbId: 745124,
    season: 2024,
    gameDate: '2024-04-01',
    gameType: 'R',
    status: 'Final',
    homeTeam: mockTeams[2], // Dodgers
    awayTeam: mockTeams[1], // Red Sox
    homeScore: 7,
    awayScore: 2,
    venueName: 'Dodger Stadium',
    dayNight: 'night',
    scheduledInnings: 9,
    homeProbablePitcher: null,
    awayProbablePitcher: null,
  },
  {
    id: 3,
    mlbId: 745130,
    season: 2024,
    gameDate: new Date().toISOString().split('T')[0], // Today
    gameType: 'R',
    status: 'Scheduled',
    homeTeam: mockTeams[0], // Yankees
    awayTeam: mockTeams[2], // Dodgers
    homeScore: null,
    awayScore: null,
    venueName: 'Yankee Stadium',
    dayNight: 'night',
    scheduledInnings: 9,
    homeProbablePitcher: {
      id: 1,
      mlbId: 650402,
      fullName: 'Gerrit Cole',
      headshotUrl: 'https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/650402/headshot/67/current',
    },
    awayProbablePitcher: {
      id: 2,
      mlbId: 477132,
      fullName: 'Clayton Kershaw',
      headshotUrl: 'https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/477132/headshot/67/current',
    },
  },
]

export const mockGamesPage = {
  content: mockGames,
  totalElements: 3,
  totalPages: 1,
  size: 20,
  number: 0,
}
