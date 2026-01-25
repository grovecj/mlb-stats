# MLB Statistics Application

A full-stack web application for consuming, storing, and displaying MLB statistics. The application pulls data from the official MLB Stats API and presents it through a modern React interface.

## Architecture

### Overview

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │     │                 │
│  React Frontend │────▶│  Spring Boot    │────▶│  PostgreSQL     │
│  (TypeScript)   │     │  REST API       │     │  Database       │
│                 │     │                 │     │                 │
└─────────────────┘     └────────┬────────┘     └─────────────────┘
                                 │
                                 ▼
                        ┌─────────────────┐
                        │                 │
                        │  MLB Stats API  │
                        │  (External)     │
                        │                 │
                        └─────────────────┘
```

### Tech Stack

| Layer      | Technology                          |
|------------|-------------------------------------|
| Frontend   | React 18, TypeScript, Vite          |
| Backend    | Java 21, Spring Boot 3.x            |
| Database   | PostgreSQL 16                       |
| Migrations | Flyway                              |
| Build      | Maven (backend), npm (frontend)     |

### Project Structure

```
mlb-stats/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/mlbstats/
│       ├── MlbStatsApplication.java
│       ├── common/                    # Shared utilities, exceptions, config
│       │   ├── config/
│       │   ├── exception/
│       │   └── util/
│       ├── domain/                    # Core domain models (JPA entities)
│       │   ├── team/
│       │   ├── player/
│       │   ├── game/
│       │   └── stats/
│       ├── ingestion/                 # Data ingestion module
│       │   ├── client/                # MLB API client
│       │   ├── mapper/                # API response -> entity mappers
│       │   ├── scheduler/             # Scheduled sync jobs
│       │   └── service/               # Ingestion orchestration
│       └── api/                       # REST API module
│           ├── controller/
│           ├── dto/
│           └── service/
├── frontend/
│   ├── package.json
│   └── src/
│       ├── components/
│       │   ├── common/                # Header, Navigation, DataTable
│       │   ├── team/                  # TeamCard, TeamRoster, TeamStats
│       │   ├── player/                # PlayerCard, PlayerStats
│       │   └── game/                  # GameCard, BoxScore
│       ├── pages/                     # Route components
│       ├── services/                  # API client
│       └── types/                     # TypeScript interfaces
└── docker-compose.yml
```

### Modular Monolith Design

The backend follows a modular monolith pattern where each module (`ingestion`, `api`) is self-contained:

- Own service layer with clear interfaces
- No direct cross-module repository access
- Communication via domain services

**Future microservice extraction:** Each module can become a separate Spring Boot application by extracting to its own Maven project and adding message queues for inter-service communication.

### Database Schema

The application uses the following core entities:

- **teams** - MLB team information
- **players** - Player biographical data
- **team_rosters** - Season-aware roster relationships
- **games** - Game schedule and results
- **player_batting_stats** - Season batting statistics
- **player_pitching_stats** - Season pitching statistics
- **player_game_batting** - Game-level batting stats
- **player_game_pitching** - Game-level pitching stats

## Prerequisites

- Java 21+
- Node.js 18+
- Docker and Docker Compose
- Maven 3.9+ (or use included wrapper)

## Running the Application

### 1. Start PostgreSQL

```bash
cd mlb-stats
docker-compose up -d
```

This starts a PostgreSQL 16 container on port 5432.

### 2. Start the Backend

```bash
cd backend

# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or with installed Maven
mvn spring-boot:run
```

The backend starts on http://localhost:8080. Flyway automatically runs database migrations on startup.

### 3. Start the Frontend

```bash
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

The frontend starts on http://localhost:5173 with hot reload enabled.

### 4. Load Initial Data

Trigger a full data sync from the MLB API:

```bash
curl -X POST http://localhost:8080/api/ingestion/full-sync
```

Or sync individual data types:

```bash
# Sync teams only
curl -X POST http://localhost:8080/api/ingestion/teams

# Sync rosters for current season
curl -X POST http://localhost:8080/api/ingestion/rosters

# Sync games for current season
curl -X POST http://localhost:8080/api/ingestion/games

# Sync player statistics
curl -X POST http://localhost:8080/api/ingestion/stats
```

## Accessing the Application

| Service          | URL                                    |
|------------------|----------------------------------------|
| Frontend         | http://localhost:5173                  |
| Backend API      | http://localhost:8080/api              |
| Swagger UI       | http://localhost:8080/swagger-ui.html  |
| OpenAPI Spec     | http://localhost:8080/api-docs         |

## API Endpoints

### Teams
- `GET /api/teams` - List all teams
- `GET /api/teams/{id}` - Team details
- `GET /api/teams/{id}/roster?season=` - Team roster
- `GET /api/teams/{id}/games?season=` - Team games
- `GET /api/teams/{id}/stats?season=` - Team batting stats

### Players
- `GET /api/players?search=&page=&size=` - Search players
- `GET /api/players/{id}` - Player details
- `GET /api/players/{id}/batting-stats?season=` - Batting stats
- `GET /api/players/{id}/pitching-stats?season=` - Pitching stats
- `GET /api/players/leaders/home-runs?season=&limit=` - HR leaders
- `GET /api/players/leaders/batting-average?season=&limit=` - AVG leaders
- `GET /api/players/leaders/wins?season=&limit=` - Wins leaders
- `GET /api/players/leaders/strikeouts?season=&limit=` - SO leaders

### Games
- `GET /api/games?date=&season=&teamId=` - List games
- `GET /api/games/{id}` - Game details
- `GET /api/games/today` - Today's games

### Ingestion
- `POST /api/ingestion/full-sync?season=` - Full data sync
- `POST /api/ingestion/teams` - Sync teams
- `POST /api/ingestion/rosters?season=` - Sync rosters
- `POST /api/ingestion/games?season=` - Sync games
- `POST /api/ingestion/stats?season=` - Sync statistics

## Configuration

### Backend (application.yml)

Key configuration options:

```yaml
# Database connection
spring.datasource.url: jdbc:postgresql://localhost:5432/mlbstats
spring.datasource.username: mlbstats
spring.datasource.password: mlbstats

# MLB API settings
mlb.api.base-url: https://statsapi.mlb.com/api/v1
mlb.api.timeout: 30000

# Enable scheduled ingestion (disabled by default)
mlb.ingestion.enabled: false
```

### Scheduled Jobs

When `mlb.ingestion.enabled=true`, the following jobs run automatically:

| Schedule        | Job                    | Description                    |
|-----------------|------------------------|--------------------------------|
| Daily 6 AM      | Stats sync             | Full player statistics refresh |
| Hourly          | Games sync             | Update yesterday/today's games |
| Weekly (Sunday) | Roster sync            | Full roster refresh            |

## Development

### Backend

```bash
cd backend

# Run tests
./mvnw test

# Build JAR
./mvnw package

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend

```bash
cd frontend

# Development server
npm run dev

# Type checking
npm run lint

# Production build
npm run build

# Preview production build
npm run preview
```

## Troubleshooting

### Database connection refused
Ensure PostgreSQL container is running:
```bash
docker-compose ps
docker-compose logs postgres
```

### No data showing in frontend
1. Check backend is running: http://localhost:8080/api/teams
2. Trigger data sync: `curl -X POST http://localhost:8080/api/ingestion/full-sync`
3. Check backend logs for errors

### CORS errors
The backend is configured to allow requests from http://localhost:5173. If using a different port, update `WebConfig.java`.

## License

MIT
