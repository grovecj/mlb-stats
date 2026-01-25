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
| Auth       | Google OAuth 2.0                    |
| Migrations | Flyway                              |
| Build      | Maven (backend), npm (frontend)     |

## Prerequisites

- Java 21+
- Node.js 18+
- Docker and Docker Compose
- Maven 3.9+ (or use included wrapper)
- Google Cloud Console account (for OAuth)

## Local Development Setup

### 1. Set up Google OAuth Credentials

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project (or select existing)
3. Navigate to **APIs & Services > Credentials**
4. Click **Create Credentials > OAuth 2.0 Client ID**
5. Select **Web application**
6. Add authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
7. Save your Client ID and Client Secret

### 2. Configure Environment Variables

Create a `.env` file in the project root (for local dev):

```bash
export GOOGLE_CLIENT_ID=your-client-id-here
export GOOGLE_CLIENT_SECRET=your-client-secret-here
```

Then source it: `source .env`

### 3. Start PostgreSQL

```bash
docker-compose up -d
```

### 4. Start the Backend

```bash
cd backend
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

The `dev` profile enables:
- Debug logging
- Swagger UI at http://localhost:8080/swagger-ui.html
- Disabled rate limiting

### 5. Start the Frontend (Development)

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 - you'll be redirected to Google login.

### 6. Load Initial Data

After logging in, trigger a data sync (requires `INGESTION_API_KEY` in production, but not in dev mode):

```bash
curl -X POST http://localhost:8080/api/ingestion/full-sync
```

## Production Deployment (Digital Ocean App Platform)

### 1. Prerequisites

- GitHub repository with this code
- Digital Ocean account
- Google OAuth credentials configured for your production domain

### 2. Deploy via App Platform

1. Go to [Digital Ocean App Platform](https://cloud.digitalocean.com/apps)
2. Click **Create App**
3. Connect your GitHub repository
4. DO will detect the `.do/app.yaml` spec file
5. Configure the following **environment variables** (marked as secrets):

| Variable | Description |
|----------|-------------|
| `GOOGLE_CLIENT_ID` | OAuth client ID |
| `GOOGLE_CLIENT_SECRET` | OAuth client secret |
| `INGESTION_API_KEY` | Secret key to protect ingestion endpoints |

6. Update Google OAuth redirect URI to: `https://your-app.ondigitalocean.app/login/oauth2/code/google`
7. Deploy

### 3. Initial Data Load

After deployment, trigger data sync with your API key:

```bash
curl -X POST https://your-app.ondigitalocean.app/api/ingestion/full-sync \
  -H "X-API-Key: your-ingestion-api-key"
```

### Estimated Costs

| Component | Size | Monthly Cost |
|-----------|------|--------------|
| App Service | basic-xxs (512MB) | ~$5 |
| Managed PostgreSQL | db-s-1vcpu-1gb | ~$15 |
| **Total** | | **~$20/month** |

## Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DATABASE_URL` | Yes | localhost | JDBC PostgreSQL URL |
| `DATABASE_USERNAME` | Yes | mlbstats | Database username |
| `DATABASE_PASSWORD` | Yes | mlbstats | Database password |
| `GOOGLE_CLIENT_ID` | Yes | - | Google OAuth client ID |
| `GOOGLE_CLIENT_SECRET` | Yes | - | Google OAuth client secret |
| `INGESTION_API_KEY` | No | - | API key for ingestion endpoints |
| `INGESTION_ENABLED` | No | true | Enable/disable ingestion endpoints |
| `RATE_LIMIT_ENABLED` | No | true | Enable rate limiting |
| `RATE_LIMIT_RPM` | No | 60 | Requests per minute per IP |
| `SWAGGER_ENABLED` | No | false | Enable Swagger UI |
| `LOG_LEVEL` | No | INFO | Logging level for com.mlbstats |
| `PORT` | No | 8080 | Server port |

## API Endpoints

### Public (after authentication)

#### Teams
- `GET /api/teams` - List all teams
- `GET /api/teams/{id}` - Team details
- `GET /api/teams/{id}/roster?season=` - Team roster
- `GET /api/teams/{id}/games?season=` - Team games

#### Players
- `GET /api/players?search=&page=&size=` - Search players
- `GET /api/players/{id}` - Player details
- `GET /api/players/{id}/batting-stats?season=` - Batting stats
- `GET /api/players/{id}/pitching-stats?season=` - Pitching stats
- `GET /api/players/leaders/home-runs?season=&limit=` - HR leaders

#### Games
- `GET /api/games?date=&season=&teamId=` - List games
- `GET /api/games/{id}` - Game details
- `GET /api/games/today` - Today's games

### Admin (requires X-API-Key header in production)

- `POST /api/ingestion/full-sync?season=` - Full data sync
- `POST /api/ingestion/teams` - Sync teams
- `POST /api/ingestion/rosters?season=` - Sync rosters
- `POST /api/ingestion/games?season=` - Sync games
- `POST /api/ingestion/stats?season=` - Sync statistics

## Security Features

- **Google OAuth 2.0** - All users must authenticate via Google
- **CSRF Protection** - Cookie-based CSRF tokens
- **Rate Limiting** - 60 requests/minute per IP (configurable)
- **API Key Protection** - Ingestion endpoints require API key in production
- **Non-root Container** - Docker runs as unprivileged user

## Development

### Backend

```bash
cd backend

# Run tests
./mvnw test

# Build JAR
./mvnw package

# Run with dev profile
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
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
```

### Docker Build

```bash
# Build the combined image
docker build -t mlb-stats .

# Run locally
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/mlbstats \
  -e DATABASE_USERNAME=mlbstats \
  -e DATABASE_PASSWORD=mlbstats \
  -e GOOGLE_CLIENT_ID=your-id \
  -e GOOGLE_CLIENT_SECRET=your-secret \
  mlb-stats
```

## License

MIT
