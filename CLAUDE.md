# places-backend

REST API backend for a places discovery and management app — users authenticate via JWT, then create/manage/search saved places with ratings, photos, and opening hours.

## Tech Stack

- **Java 17** · **Spring Boot 3.2.3**
- **MongoDB Atlas** (database: `gastrorate`, collections: `users`, `places`)
- **Spring Security** + **JJWT 0.11.5** (stateless JWT auth)
- **Spring Data MongoDB** + **MongoTemplate** (custom aggregation queries)
- **Lombok** (boilerplate reduction)
- **Maven** (build tool — use `./mvnw` wrapper)

## Key Directories

| Path | Purpose |
|------|---------|
| `src/main/java/places/rest/` | Controllers (`AuthController`, `PlaceController`, `UserController`) |
| `src/main/java/places/service/` | Business logic (interface + impl pairs: `*Manager` / `*ManagerImpl`) |
| `src/main/java/places/repository/` | Spring Data repos; `qdls/` holds custom MongoTemplate queries |
| `src/main/java/places/model/` | MongoDB documents (`User`, `Place`) and DTOs/value objects |
| `src/main/java/places/security/` | JWT filter, JWT service, `SecurityConfig` |
| `src/main/java/places/config/` | Web config (static file serving for `uploads/`) |
| `src/main/java/places/constants/` | `Constants.java` — base path `/rest` |
| `src/main/java/places/utils/` | `UtilsHelper` — string, date, math, serialization utilities |
| `uploads/` | Profile images stored on disk; served at `/uploads/**` |

## Build & Run

```bash
./mvnw spring-boot:run          # Start on :8080
./mvnw test                     # Run tests
./mvnw clean package            # Build JAR
./mvnw clean install            # Full build + tests
```

## API Base Path

All endpoints are prefixed with `/rest` (see `constants/Constants.java:4`).
Public routes: `POST /rest/auth/login`, `POST /rest/auth/register` — everything else requires `Authorization: Bearer <token>`.

## Configuration Files

- `src/main/resources/application.yaml` — MongoDB URI
- `src/main/resources/application.properties` — JWT secret (`jwt.secret`)

## Related Projects

| Project | Path |
|---------|------|
| Frontend (Flutter + async_redux) | `/Users/marinoroz/Documents/OrozDigital/places-frontend/` |

When a task involves both backend and frontend changes (e.g., new API endpoint + consuming it in Flutter), read and edit files in both projects freely.

## Additional Documentation

- `.claude/docs/architectural_patterns.md` — Layer structure, interface/impl convention, security flow, custom repository pattern, DI style, model conventions

## Memory System

Project memory lives in `~/.claude/projects/-Users-marinoroz-Documents-OrozDigital-places-backend/memory/`.

**`MEMORY.md`** is always loaded — it's a short index of pointers to individual memory files.

Rules for saving memory:
- **Feedback** (`feedback_*.md`) — whenever something breaks in a non-obvious way or a workaround is needed. These are the most valuable — they prevent repeating mistakes.
- **Project** (`project_*.md`) — in-progress features, deploy state, business reasons behind architectural decisions. Update when state changes.
- **Do NOT save**: code patterns, file paths, architecture (derivable from codebase or `architectural_patterns.md`), git history (belongs in commits), in-progress task state (belongs in TodoWrite).
