# Architectural Patterns

## 1. Layered Architecture (Controller → Service → Repository)

Three strict layers with no cross-layer skipping:

- **Controllers** (`rest/`) — HTTP mapping only; no business logic, no DB calls
- **Services** (`service/`) — All business logic; orchestrate repository calls
- **Repositories** (`repository/`) — DB access only

Example chain: `PlaceController:30` → `PlaceManager:9` → `PlaceRepository:11`

## 2. Interface + Implementation (Manager Pattern)

Every service and the JWT service has a paired interface:

```
AuthManager.java          ← interface
AuthManagerImpl.java      ← @Service implementation

PlaceManager.java         ← interface
PlaceManagerImpl.java     ← @Service implementation

UserManager.java          ← interface
UserManagerImpl.java      ← @Service implementation

JwtService.java           ← interface
JwtServiceImpl.java       ← @Component implementation
```

This allows swapping implementations and easier unit testing. Controllers and filters depend only on the interface, never the concrete class.

## 3. Custom Repository Pattern (MongoTemplate Aggregation)

When Spring Data derived queries aren't enough, a custom interface + component extends the main repo:

- `PlaceRepositoryCustom.java` — declares `findPlacesBySearchForm()`
- `PlaceRepositoryCustomImpl.java:18` — `@Component` using `MongoTemplate` to build aggregation pipelines
- `PlaceRepository.java:11` — `extends MongoRepository<Place, String>, PlaceRepositoryCustom`

The impl uses `Aggregation.match()` + `Aggregation.sort()` + `mongoTemplate.aggregate()`. See `PlaceRepositoryCustomImpl.java:50-56`.

## 4. Dependency Injection Style

Two styles coexist — prefer constructor injection for new code:

**Constructor injection (preferred):** `@RequiredArgsConstructor` on the class + `final` fields.
Used in: `AuthManagerImpl`, `UserManagerImpl`, `JwtAuthenticationFilter`, `SecurityConfig`.

**Field injection (legacy):** `@Autowired` on the field.
Used in: `AuthController`, `PlaceController`, `PlaceManagerImpl`.

**Properties injection:** `@Value("${jwt.secret}")` in `JwtServiceImpl.java:20`.

## 5. JWT Stateless Security Flow

Request lifecycle for protected endpoints:

1. `JwtAuthenticationFilter` (`security/JwtAuthenticationFilter.java:25`) extends `OncePerRequestFilter`
2. Extracts `Bearer <token>` from `Authorization` header (line 31-36)
3. Calls `JwtService.extractEmail()` → fetches `User` from `UserRepository`
4. Calls `JwtService.isTokenValid()` → sets `UsernamePasswordAuthenticationToken` in `SecurityContextHolder`
5. JWT errors (`ExpiredJwtException`, `JwtException`) return HTTP 401 directly (lines 59-65)

`SecurityConfig.java:25-28` marks only `/rest/auth/login` and `/rest/auth/register` as `permitAll()`; session policy is `STATELESS`.

Token contains subject (email) plus claims: `id`, `firstName`, `lastName`. Signed HS256, 24h expiry. See `JwtServiceImpl.java:29-38`.

## 6. MongoDB Document Conventions

- `@Document(collection="...")` on entity classes
- `@Id String id` — MongoDB ObjectId stored as String
- `userId` field on `Place` (not a `@DBRef`) — ownership is tracked by storing user ID as a plain string field (`Place.java:22`)
- Lombok `@Data + @Builder + @AllArgsConstructor + @NoArgsConstructor` on all entities and most DTOs
- Sensitive fields annotated `@JsonIgnore` (e.g., `User.password` at `User.java:24`)

## 7. Computed Fields on Save

`PlaceManagerImpl.saveOrUpdatePlace()` (`service/PlaceManagerImpl.java:18-25`):
- Generates ID as first segment of a UUID if `place.getId()` is null
- Computes `placeRating = firstRating.getPlaceRating() + secondRating.getPlaceRating()` before persisting

`Rating.java` also computes its own `placeRating` as an average of its sub-ratings.

## 8. Static File Serving

Profile images are written to `uploads/{userId}_{filename}` on disk by `UserManagerImpl.java:20-37`.
`WebConfig.java:12` maps the URL pattern `/uploads/**` → `file:uploads/` so Spring MVC serves them directly.

## 9. Sorting via Enum + Switch

`PlaceSearchForm` carries a `PlaceSorting` enum value. `PlaceRepositoryCustomImpl.java:27-48` maps each enum constant to a MongoDB `Sort.Direction` and field name via a switch statement before building the aggregation pipeline. Adding a new sort option requires adding to `PlaceSorting.java` and extending the switch.

## 10. Exception Handling

No global `@ControllerAdvice` exists. Exceptions bubble up to Spring's default handler (HTTP 500) unless caught explicitly:

- JWT filter catches `ExpiredJwtException` / `JwtException` → 401 (`JwtAuthenticationFilter.java:59-65`)
- Auth errors throw `RuntimeException` with a message (`AuthManagerImpl.java:25,28,39`)
- Custom repo throws `IllegalArgumentException` for unknown sort values (`PlaceRepositoryCustomImpl.java:47`)
- File I/O exceptions are declared via `throws IOException` and propagate to the controller
