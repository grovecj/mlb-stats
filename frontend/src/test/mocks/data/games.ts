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
  },
  {
    id: 3,
    mlbId: 745130,
    season: 2024,
    gameDate: new Date().toISOString().split('T')[0], // Today
    gameType: 'R',
    status: 'Preview',
    homeTeam: mockTeams[0], // Yankees
    awayTeam: mockTeams[2], // Dodgers
    homeScore: null,
    awayScore: null,
    venueName: 'Yankee Stadium',
    dayNight: 'night',
    scheduledInnings: 9,
  },
]

export const mockGamesPage = {
  content: mockGames,
  totalElements: 3,
  totalPages: 1,
  size: 20,
  number: 0,
}
