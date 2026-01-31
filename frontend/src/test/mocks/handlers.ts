import { http, HttpResponse } from 'msw'
import { mockTeams, mockStandings } from './data/teams'
import { mockPlayers, mockPlayersPage } from './data/players'
import { mockGames, mockGamesPage } from './data/games'
import type { PlayerComparisonResponse } from '../../types/comparison'
import type { FavoritesDashboard } from '../../types/dashboard'

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

  // Favorites dashboard endpoint
  http.get('/api/favorites/dashboard', () => {
    const mockDashboard: FavoritesDashboard = {
      teams: mockTeams.slice(0, 2).map(team => ({
        team,
        todaysGame: {
          id: 1,
          gameDate: new Date().toISOString().split('T')[0],
          scheduledTime: '19:05:00',
          status: 'Scheduled',
          opponent: mockTeams[2],
          isHome: true,
          teamScore: null,
          opponentScore: null,
          venueName: 'Yankee Stadium',
        },
        nextGame: null,
        standing: {
          wins: 85,
          losses: 60,
          divisionRank: 2,
          gamesBack: '3.0',
          streakCode: 'W3',
          winningPercentage: 0.586,
        },
      })),
      players: mockPlayers.slice(0, 2).map(player => ({
        player,
        currentTeam: mockTeams[0],
        playerType: player.positionType === 'Pitcher' ? 'PITCHER' as const : 'BATTER' as const,
        lastBattingGame: player.positionType !== 'Pitcher' ? {
          gameId: 1,
          gameDate: '2024-08-15',
          opponent: 'BOS',
          atBats: 4,
          hits: 2,
          runs: 1,
          rbi: 2,
          homeRuns: 1,
          walks: 1,
          strikeouts: 1,
        } : null,
        lastPitchingGame: player.positionType === 'Pitcher' ? {
          gameId: 1,
          gameDate: '2024-08-15',
          opponent: 'BOS',
          inningsPitched: 7.0,
          hitsAllowed: 4,
          earnedRuns: 2,
          strikeouts: 10,
          walks: 2,
          isWinner: true,
          isLoser: false,
          isSave: false,
        } : null,
        seasonBatting: player.positionType !== 'Pitcher' ? {
          season: 2024,
          gamesPlayed: 140,
          atBats: 500,
          hits: 155,
          homeRuns: 45,
          rbi: 110,
          runs: 95,
          stolenBases: 5,
          battingAvg: 0.310,
          obp: 0.410,
          slg: 0.650,
          ops: 1.060,
        } : null,
        seasonPitching: player.positionType === 'Pitcher' ? {
          season: 2024,
          gamesPlayed: 28,
          gamesStarted: 28,
          wins: 14,
          losses: 7,
          saves: 0,
          inningsPitched: 175.0,
          strikeouts: 200,
          era: 3.15,
          whip: 1.05,
          kPer9: 10.3,
        } : null,
      })),
      hasMoreTeams: false,
      hasMorePlayers: true,
      totalTeamCount: 2,
      totalPlayerCount: 8,
    }
    return HttpResponse.json(mockDashboard)
  }),

  // Player comparison endpoint
  http.get('/api/players/compare', ({ request }) => {
    const url = new URL(request.url)
    const playersParam = url.searchParams.get('players')
    const mode = url.searchParams.get('mode') || 'season'

    if (!playersParam) {
      return new HttpResponse(null, { status: 400 })
    }

    const playerIds = playersParam.split(',').map(id => parseInt(id))

    // Build mock comparison response
    const comparisonPlayers = playerIds.map(id => {
      const player = mockPlayers.find(p => p.id === id)
      if (!player) return null

      const isPitcher = player.positionType === 'Pitcher'

      return {
        player,
        season: mode === 'career' ? null : 2024,
        battingStats: isPitcher ? null : {
          gamesPlayed: 150,
          atBats: 500,
          runs: 100,
          hits: 150,
          doubles: 30,
          triples: 2,
          homeRuns: id === 1 ? 58 : 41,
          rbi: 120,
          stolenBases: 5,
          caughtStealing: 2,
          walks: 80,
          strikeouts: 150,
          battingAvg: 0.300,
          obp: 0.400,
          slg: 0.600,
          ops: 1.000,
          plateAppearances: 600,
          totalBases: 300,
          extraBaseHits: 60,
        },
        pitchingStats: isPitcher ? {
          gamesPlayed: 30,
          gamesStarted: 30,
          wins: 15,
          losses: 8,
          saves: 0,
          holds: 0,
          inningsPitched: 180.0,
          hitsAllowed: 150,
          runsAllowed: 60,
          earnedRuns: 55,
          homeRunsAllowed: 20,
          walks: 40,
          strikeouts: 200,
          era: 2.75,
          whip: 1.05,
          kPer9: 10.0,
          bbPer9: 2.0,
          completeGames: 2,
          shutouts: 1,
        } : null,
      }
    }).filter(Boolean)

    const response: PlayerComparisonResponse = {
      mode: mode as 'season' | 'career',
      players: comparisonPlayers as PlayerComparisonResponse['players'],
      leaders: {
        batting: {
          homeRuns: 1, // Aaron Judge
          rbi: 1,
          battingAvg: 1,
        },
        pitching: {
          wins: 2, // Gerrit Cole
          strikeouts: 2,
          era: 2,
        },
      },
    }

    return HttpResponse.json(response)
  }),
]
