# MLB Stats API Reference

This document describes the MLB Stats API used by this application for data ingestion.

## Base URL

```
https://statsapi.mlb.com/api/v1
```

## Endpoints Used

### Teams

**Get all MLB teams**
```
GET /teams?sportId=1
```

Response includes: `id`, `name`, `abbreviation`, `locationName`, `venue`, `league`, `division`

### Rosters

**Get team roster**
```
GET /teams/{teamId}/roster?season={season}&rosterType=40Man
```

Response includes player references with `id`, `fullName`, `jerseyNumber`, `position`, `status`

### Players

**Get player details**
```
GET /people/{playerId}
```

Response includes: `id`, `fullName`, `firstName`, `lastName`, `primaryNumber`, `primaryPosition`, `batSide`, `pitchHand`, `birthDate`, `height`, `weight`, `mlbDebutDate`, `active`

### Player Stats

**Get season stats**
```
GET /people/{playerId}/stats?stats=season&season={season}&group={hitting|pitching}
```

Response includes detailed batting or pitching statistics for the specified season.

### Games/Schedule

**Get game schedule**
```
GET /schedule?sportId=1&startDate={YYYY-MM-DD}&endDate={YYYY-MM-DD}&gameType=R,P
```

Response includes: `gamePk`, `gameDate`, `teams` (home/away with scores), `status`, `venue`

## Image CDN URLs

The MLB provides static image assets via CDN. These URLs are deterministic based on IDs:

### Player Headshots

```
https://img.mlbstatic.com/mlb-photos/image/upload/d_people:generic:headshot:67:current.png/w_213,q_auto:best/v1/people/{mlbId}/headshot/67/current
```

- `{mlbId}` - The player's MLB ID
- Falls back to a generic silhouette if no photo exists
- `w_213` - Width in pixels (adjustable)
- `q_auto:best` - Quality setting

### Team Logos

```
https://www.mlbstatic.com/team-logos/{mlbId}.svg
```

- `{mlbId}` - The team's MLB ID
- Returns SVG format for crisp scaling

## Authentication

The MLB Stats API is public and does not require authentication for read operations.

## Rate Limiting

The API does not document explicit rate limits, but the application uses reasonable delays during bulk ingestion to avoid overwhelming the service.

## Data Freshness

- **Teams**: Rarely change, sync once per season
- **Rosters**: Change throughout season, sync periodically
- **Games**: Update frequently during season, sync daily
- **Stats**: Update after each game, sync daily during season

## Additional Resources

- [MLB Stats API Wiki](https://github.com/toddrob99/MLB-StatsAPI/wiki) - Community documentation
- [MLB Data API](https://appac.github.io/mlb-data-api-docs/) - Alternative documentation
