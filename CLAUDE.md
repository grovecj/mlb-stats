# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

### Backend (Java/Spring Boot)
```bash
cd backend
./mvnw spring-boot:run                           # Run with default profile
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run  # Run with dev profile (enables Swagger, debug logging)
./mvnw test                                      # Run all tests
./mvnw test -Dtest=PlayerApiServiceTest          # Run single test class
./mvnw test -Dtest=PlayerApiServiceTest#testMethod  # Run single test method
./mvnw package                                   # Build JAR
```

### Frontend (React/TypeScript)
```bash
cd frontend
npm install        # Install dependencies
npm run dev        # Start dev server (http://localhost:5173)
npm run build      # Production build
npm run lint       # ESLint check
npm run test       # Run vitest tests
```

### Database
```bash
docker-compose up -d   # Start PostgreSQL
```

### Infrastructure
```bash
cd terraform
terraform plan     # Preview changes
terraform apply    # Deploy to Digital Ocean
```

## Architecture Overview

### Backend Structure (`backend/src/main/java/com/mlbstats/`)

```
api/
├── controller/    # REST endpoints (TeamController, PlayerController, GameController, etc.)
├── dto/           # Response objects
└── service/       # API business logic, DTO transformations

domain/
├── team/          # Team entity + repository
├── player/        # Player, TeamRoster entities
├── game/          # Game entity
├── stats/         # PlayerBattingStats, PlayerPitchingStats, game-level stats
└── user/          # AppUser, Role enum (USER < ADMIN < OWNER)

ingestion/
├── client/        # MlbApiClient - calls MLB Stats API
│   └── dto/       # API response mappings
├── mapper/        # Transform API responses → domain entities
├── scheduler/     # Cron jobs for automated sync
└── service/       # TeamIngestionService, GameIngestionService, etc.

common/
├── config/        # SecurityConfig, RestClientConfig, AuthProperties
├── security/      # CustomOAuth2UserService, AppUserPrincipal
├── exception/     # GlobalExceptionHandler, custom exceptions
└── util/          # DateUtils
```

### Data Flow

1. **Ingestion**: MLB Stats API → MlbApiClient → IngestionService → Mapper → Repository → PostgreSQL
2. **API Requests**: Controller → ApiService → Repository → DTO transformation → JSON response

For details on the MLB Stats API endpoints and image CDN URLs, see [docs/MLB_STATS_API.md](docs/MLB_STATS_API.md).

### Authentication & Authorization

- OAuth2 login (Google) via Spring Security
- `CustomOAuth2UserService` creates/updates `AppUser` on login
- Role hierarchy: USER (read-only) → ADMIN (can trigger sync) → OWNER (user management)
- First user matching `OWNER_EMAIL` env var gets OWNER role
- Use `@PreAuthorize("hasRole('ADMIN')")` on controllers/methods

### Frontend Structure (`frontend/src/`)

- `pages/` - Route components (HomePage, TeamsPage, AdminPage, etc.)
- `components/` - Reusable UI components
- `contexts/AuthContext.tsx` - Auth state, `useAuth()` hook with `isAdmin`, `isOwner`, `hasRole()`
- `services/api.ts` - All backend API calls (includes CSRF token handling)

## Frontend Theme Support

Use CSS variables for dark mode compatibility. Defined in `frontend/src/index.css`:

| Variable | Usage |
|----------|-------|
| `--primary-color` | Brand color, links, headings |
| `--secondary-color` | Accents, errors |
| `--background-color` | Page background |
| `--card-background` | Cards, panels, modals |
| `--text-color` | Primary text |
| `--text-light` | Secondary/muted text |
| `--border-color` | Borders, dividers |
| `--success-color` | Success states |
| `--warning-color` | Warning states |

```css
/* Always use variables instead of hardcoded colors */
.component {
  background: var(--card-background);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}
```

## CSRF Token Handling

POST/PUT/DELETE requests must include the CSRF token. Use helpers in `api.ts`:
- `postJson(url)` - Automatically includes CSRF token from cookie
- For custom requests: read `XSRF-TOKEN` cookie, send as `X-XSRF-TOKEN` header

## Google Analytics 4 (GA4) Instrumentation

The app includes GA4 for tracking user behavior. Page views are tracked automatically via `useAnalytics` hook in `App.tsx`.

### Tracking Custom Events

For significant user interactions, use the `event` function from `frontend/src/utils/analytics.ts`:

```typescript
import { event } from '../utils/analytics';

// Track button clicks, form submissions, etc.
event('button_click', { button_name: 'sync_teams' });
event('form_submit', { form_name: 'search', search_term: query });
event('filter_change', { filter_type: 'season', value: '2024' });
```

### When to Add Events

Add GA4 events for:
- Button clicks that trigger important actions (sync, export, etc.)
- Form submissions (search, filters)
- Navigation to external links
- Error states users encounter
- Feature usage (toggle dark mode, expand/collapse sections)

### Event Naming Conventions

- Use snake_case for event names and parameter keys
- Keep event names descriptive but concise
- Common parameters: `button_name`, `form_name`, `page_section`, `item_id`, `item_type`

### Configuration

- Measurement ID is set via `VITE_GA_MEASUREMENT_ID` env var
- Local development: set in `frontend/.env`
- Production: configured via Terraform (`ga_measurement_id` variable)
