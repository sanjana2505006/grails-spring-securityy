# grails-spring-security

Sample app for **Securing Grails Applications with Spring Security Core** (Apache Grails `8.0.0-SNAPSHOT`, JDK 21).

The guide walks through securing a small REST API with Spring Security Core: User and Role setup, HTTP Basic authentication, `@Secured` on controller actions, and Spock unit and integration tests against a real PostgreSQL database (Testcontainers). Spring Security evaluates access before your controller runs, so you keep thin REST controllers and JSON views — mostly declaring who can call which endpoint, and returning 401, 403, or 422 where appropriate.

## Layout

| Directory | What it is |
|-----------|------------|
| `initial/` | Vanilla Grails 8 REST API starter (`postgres`, `testcontainers`, `spock`, JSON views). A simple `Resource` REST API with no security plugin yet. Start the guide here. |
| `complete/` | The finished sample — `grails-spring-security` plugin, `User`/`Role` domains, HTTP Basic auth, `@Secured` controller rules, JSON views, and Spock unit + integration tests against real PostgreSQL (Testcontainers). |

## Running

```bash
git clone -b grails8 https://github.com/grails-guides/grails-spring-security.git
cd grails-spring-security/complete
./gradlew test integrationTest
```

To follow the guide step by step, start from `initial/`:

```bash
cd grails-spring-security/initial
./gradlew test
```

Run the finished app:

```bash
cd grails-spring-security/complete
./gradlew bootRun
```

### Example secured endpoints (complete/)

Public read access (no credentials):

```bash
curl -s http://localhost:8080/api/resources
curl -s http://localhost:8080/api/resources/1
```

Create a resource (requires authentication — any logged-in user):

```bash
curl -s -u user:password -H 'Content-Type: application/json' \
  -d '{"name":"Team Notes","description":"Shared notes"}' \
  http://localhost:8080/api/resources
```

Update or delete (requires `ROLE_ADMIN`):

```bash
curl -s -u admin:password -X PUT -H 'Content-Type: application/json' \
  -d '{"name":"Admin Console","description":"Updated by admin"}' \
  http://localhost:8080/api/resources/2

curl -s -u admin:password -X DELETE http://localhost:8080/api/resources/2
```

Anonymous create attempts return **401**; authenticated users without `ROLE_ADMIN` receive **403** on update/delete.

Development users (seeded in `BootStrap` for the `development` environment only):

| Username | Password | Roles |
|----------|----------|-------|
| `user` | `password` | `ROLE_USER` |
| `admin` | `password` | `ROLE_ADMIN`, `ROLE_USER` |

## Requirements

- **JDK 21** (Temurin recommended; the Gradle build enforces Java 21+)
- **Docker** running locally — required for `integrationTest` (Testcontainers PostgreSQL). Unit tests (`./gradlew test`) do not need Docker.
- **PostgreSQL** on `localhost:5432` — required for `./gradlew bootRun` (default database `devDb` in `application.yml`)

If Gradle reports *"Run this build using a Java 21 or newer JVM"*, your shell or IDE is still on an older JDK:

```bash
sdk install java 21.0.6-tem
sdk default java 21.0.6-tem
java -version   # should show 21.x
```

If `integrationTest` fails with a Testcontainers / `docker.sock` error, start Docker Desktop (or your local Docker daemon) and retry.

## Security model (complete/)

```groovy
@Secured(['permitAll'])
def index() { ... }          // public

@Secured(['isAuthenticated()'])
def save() { ... }           // any authenticated user

@Secured(['ROLE_ADMIN'])
def update() { ... }         // admin only
```

Controllers stay thin; Spring Security enforces access before actions run. JSON views under `grails-app/views/` shape REST responses.

## Guide prose

Published narrative lives on [grails.apache.org/guides](https://grails.apache.org/guides/) in [apache/grails-static-website](https://github.com/apache/grails-static-website) under `guides/grails-spring-security/v8/`.

## CI

GitHub Actions (`.github/workflows/grails8.yml`) runs `./gradlew test` for `initial` and `complete`, and `./gradlew integrationTest` for `complete`, on pushes and PRs to the `grails8` branch.
