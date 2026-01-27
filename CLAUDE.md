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

## Testing

### Backend Integration Tests

Run all tests:
```bash
cd backend
./mvnw test
```

Run a single test class:
```bash
./mvnw test -Dtest=TeamIngestionServiceTest
```

#### Test Structure

- `src/test/java/com/mlbstats/BaseIntegrationTest.java` - Base class with MockMvc setup, entity factories, fixture loading
- `src/test/resources/application-test.yml` - H2 in-memory database config
- `src/test/resources/fixtures/mlb-api/` - JSON fixtures for mock API responses

#### Adding New Ingestion Service Tests

```java
class MyIngestionServiceTest extends BaseIntegrationTest {
    @MockitoBean
    private MlbApiClient mlbApiClient;

    @Autowired
    private MyIngestionService myIngestionService;

    @Test
    void syncData_shouldCreateEntities() {
        // Load fixture
        MyResponse response = loadFixture("mlb-api/my-response.json", MyResponse.class);
        when(mlbApiClient.getMyData()).thenReturn(response);

        // Call service
        int count = myIngestionService.syncData();

        // Verify
        assertThat(count).isEqualTo(5);
        assertThat(myRepository.findAll()).hasSize(5);
    }
}
```

#### Adding New Controller Tests

```java
class MyControllerTest extends BaseIntegrationTest {
    @Test
    @WithMockUser(roles = "USER")
    void getEndpoint_shouldReturnData() throws Exception {
        createTestTeam(147, "New York Yankees", "NYY");

        mockMvc.perform(get("/api/my-endpoint"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("New York Yankees"));
    }
}
```

### Frontend Tests

Run all tests:
```bash
cd frontend
npm test -- --run
```

Run with coverage:
```bash
npm test -- --run --coverage
```

#### Test Structure

- `src/test/setup.ts` - MSW server setup, global test configuration
- `src/test/mocks/handlers.ts` - Mock API endpoint handlers
- `src/test/mocks/data/` - Mock data matching TypeScript types
- `src/test/utils/render.tsx` - Custom render with providers

#### Adding New Page Tests

```typescript
import { describe, it, expect } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import { render } from '../../test/utils/render'
import MyPage from '../MyPage'

describe('MyPage', () => {
  it('displays data after loading', async () => {
    render(<MyPage />)

    await waitFor(() => {
      expect(screen.queryByText('Loading...')).not.toBeInTheDocument()
    })

    expect(screen.getByText('Expected Content')).toBeInTheDocument()
  })
})
```

#### Adding New Mock Handlers

In `src/test/mocks/handlers.ts`:

```typescript
http.get('/api/my-endpoint', () => {
  return HttpResponse.json(mockData)
})
```

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

## New Relic Application Monitoring

The backend integrates with New Relic for application monitoring and metrics via Spring Boot Actuator and Micrometer.

### Metrics Exported

- HTTP request metrics (latency, status codes, throughput)
- JVM metrics (heap, GC, threads)
- Database connection pool metrics
- Custom application metrics

### Configuration

Enable New Relic monitoring via environment variables:

| Variable | Description |
|----------|-------------|
| `NEW_RELIC_ENABLED` | Set to `true` to enable metrics export |
| `NEW_RELIC_API_KEY` | New Relic Ingest License key |
| `NEW_RELIC_ACCOUNT_ID` | New Relic Account ID |
| `ENVIRONMENT` | Environment tag (e.g., `production`, `staging`) |

### Terraform Setup

```hcl
new_relic_enabled    = true
new_relic_api_key    = "your-license-key"
new_relic_account_id = "your-account-id"
```

### Getting New Relic Credentials

1. Sign up at https://newrelic.com (free tier available)
2. Get License Key: User menu > API Keys > Create key (Ingest - License)
3. Get Account ID: User menu > Administration > Access management

### Actuator Endpoints

Available at `/actuator/*` (requires authentication):
- `/actuator/health` - Application health status
- `/actuator/info` - Application info
- `/actuator/metrics` - Available metrics list
- `/actuator/metrics/{name}` - Specific metric details
