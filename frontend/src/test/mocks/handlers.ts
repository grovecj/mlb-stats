import { http, HttpResponse } from 'msw'
import { mockTeams, mockStandings } from './data/teams'
import { mockPlayers, mockPlayersPage } from './data/players'
import { mockGames, mockGamesPage } from './data/games'

export const handlers = [
  // Auth endpoints
  http.get('/api/auth/me', () => {
    return HttpResponse.json({
      authenticated: true,
      user: {
        id: 1,
        email: 'test@example.com',
        name: 'Test User',
        role: 'USER',
      },
    })
  }),

  // Teams endpoints
  http.get('/api/teams', () => {
    return HttpResponse.json(mockTeams)
  }),

  http.get('/api/teams/:id', ({ params }) => {
    const team = mockTeams.find(t => t.id === Number(params.id))
    if (!team) {
      return new HttpResponse(null, { status: 404 })
    }
    return HttpResponse.json(team)
  }),

  http.get('/api/teams/standings', () => {
    return HttpResponse.json(mockStandings)
  }),

  // Players endpoints
  http.get('/api/players', ({ request }) => {
    const url = new URL(request.url)
    const search = url.searchParams.get('search')

    if (search) {
      const filtered = mockPlayers.filter(p =>
        p.fullName.toLowerCase().includes(search.toLowerCase())
      )
      return HttpResponse.json({
        ...mockPlayersPage,
        content: filtered,
        totalElements: filtered.length,
      })
    }

    return HttpResponse.json(mockPlayersPage)
  }),

  http.get('/api/players/:id', ({ params }) => {
    const player = mockPlayers.find(p => p.id === Number(params.id))
    if (!player) {
      return new HttpResponse(null, { status: 404 })
    }
    return HttpResponse.json(player)
  }),

  // Games endpoints
  http.get('/api/games', ({ request }) => {
    const url = new URL(request.url)
    const date = url.searchParams.get('date')

    if (date) {
      const filtered = mockGames.filter(g => g.gameDate === date)
      return HttpResponse.json(filtered)
    }

    return HttpResponse.json(mockGamesPage)
  }),

  http.get('/api/games/today', () => {
    const today = new Date().toISOString().split('T')[0]
    const todaysGames = mockGames.filter(g => g.gameDate === today)
    return HttpResponse.json(todaysGames)
  }),

  http.get('/api/games/:id', ({ params }) => {
    const game = mockGames.find(g => g.id === Number(params.id))
    if (!game) {
      return new HttpResponse(null, { status: 404 })
    }
    return HttpResponse.json(game)
  }),

  // Calendar endpoints
  http.get('/api/games/calendar', () => {
    // Return lightweight calendar game data
    return HttpResponse.json(mockGames.map(g => ({
      id: g.id,
      gameDate: g.gameDate,
      scheduledTime: '19:05:00',
      status: g.status,
      homeTeamId: g.homeTeam?.id || 147,
      homeTeamAbbr: g.homeTeam?.abbreviation || 'NYY',
      awayTeamId: g.awayTeam?.id || 111,
      awayTeamAbbr: g.awayTeam?.abbreviation || 'BOS',
      homeScore: g.homeScore,
      awayScore: g.awayScore,
    })))
  }),

  http.get('/api/games/calendar/counts', () => {
    // Return game counts per day
    const countsByDate = mockGames.reduce((acc, g) => {
      if (!acc[g.gameDate]) {
        acc[g.gameDate] = { date: g.gameDate, totalGames: 0, homeGames: 0, awayGames: 0 }
      }
      acc[g.gameDate].totalGames++
      return acc
    }, {} as Record<string, { date: string; totalGames: number; homeGames: number; awayGames: number }>)
    return HttpResponse.json(Object.values(countsByDate))
  }),

  // Public stats endpoint
  http.get('/api/public/stats', () => {
    return HttpResponse.json({
      teamCount: mockTeams.length,
      playerCount: mockPlayers.length,
      gameCount: mockGames.length,
    })
  }),

  // Leaders endpoints
  http.get('/api/players/leaders/home-runs', () => {
    return HttpResponse.json([
      { playerId: 1, playerName: 'Aaron Judge', homeRuns: 58 },
      { playerId: 3, playerName: 'Juan Soto', homeRuns: 41 },
    ])
  }),

  http.get('/api/players/leaders/batting-average', () => {
    return HttpResponse.json([
      { playerId: 1, playerName: 'Aaron Judge', battingAvg: 0.310 },
    ])
  }),

  http.get('/api/players/leaders/wins', () => {
    return HttpResponse.json([
      { playerId: 2, playerName: 'Gerrit Cole', wins: 15 },
    ])
  }),

  http.get('/api/players/leaders/era', () => {
    return HttpResponse.json([
      { playerId: 2, playerName: 'Gerrit Cole', era: 2.75 },
    ])
  }),
]
