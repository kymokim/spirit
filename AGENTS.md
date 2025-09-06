# Repository Guidelines

This guide helps contributors work consistently across the Spring Boot/Gradle codebase. Keep PRs focused, tested, and aligned with the structure below.

## Project Structure & Module Organization
- Source: `src/main/java/com/kymokim/spirit/...` organized by feature (e.g., `auth`, `store`, `review`, `notification`, `common`).
- Resources: `src/main/resources/application.properties`, `application-test.yml`, static assets in `src/main/resources/static/`.
- Tests: `src/test/java/com/kymokim/spirit/...` mirroring the main package structure.
- Build output and generated sources live under `build/` (QueryDSL). `./gradlew clean` removes generated code.

## Build, Test, and Development Commands
- `./gradlew build`: Compile, run tests, and package the application.
- `./gradlew test`: Run unit and slice tests (JUnit 5).
- `./gradlew bootRun`: Start the app locally on port 8080.
- Docker: `docker build -t spirit .` then `docker run -p 8080:8080 spirit`.
- Profiles: set `SPRING_PROFILES_ACTIVE=test` to use test config when running.

## Coding Style & Naming Conventions
- Java 21; 4‑space indentation; organize imports; no wildcard imports.
- Packages: lowercase; Classes: PascalCase; methods/fields: camelCase; constants: UPPER_SNAKE_CASE.
- Prefer constructor injection (Lombok `@RequiredArgsConstructor`), avoid field injection.
- Keep controllers thin; put business logic in `service` and persistence in `repository`.
- Do not use var for local variables; always declare explicit types.
- Do not use abbreviated or cryptic variable names; use clear, descriptive, domain-specific names.

## Testing Guidelines
- Frameworks: Spring Boot Test (JUnit 5), Mockito; H2 is used for tests.
- Test files end with `*Tests.java`; method names like `shouldDoX_whenY`.
- Run: `./gradlew test` (filter with `-Dtest=ClassName` when needed).
- Favor slice tests (`@DataJpaTest`, `@WebMvcTest`) over full `@SpringBootTest` unless necessary.

## Commit & Pull Request Guidelines
- Commit style follows Conventional Commits: `Feat(scope): summary`, `Fix`, `Refactor`, etc. Example: `Feat(store): add popular store query API`.
- PRs: include summary, linked issues, test evidence, and any config/DB notes. Add example requests/responses for new endpoints.
- Keep PRs small and reviewable; include or update tests with behavior changes.

## Security & Configuration Tips
- Do not commit secrets. Configure AWS, Firebase, and DB via environment variables/Spring config.
- Avoid logging PII or tokens; validate all inputs; prefer safe defaults.

## Language & Response
- Always answer in korean, politely.
