# Data Manager

Spring Boot backend for healthcare data management: patients, doctors, appointments, medical records, prescriptions, billing, documents, notifications, audit logs, and dashboards.

## Tech stack

- Java 17, Spring Boot 3.5
- PostgreSQL (production) / H2 (dev profile)
- Flyway migrations, JWT auth, Spring Security
- Docker Compose (Postgres, MailHog, app)
- OpenAPI / Swagger UI

## Project layout

```
Data_manager/
├── management/          # Spring Boot application
├── api-req/             # HTTP client examples (IDE REST Client)
├── docker-compose.yml
└── .env.example         # Copy to .env for Docker secrets
```

## Quick start (local dev)

**Requirements:** JDK 17+, Maven 3.9+

```bash
cd management
mvn spring-boot:run
```

- API: http://localhost:4000
- Swagger UI: http://localhost:4000/swagger-ui.html
- Health: http://localhost:4000/actuator/health

Default profile is `dev` (in-memory H2). Bootstrap admin (change in production):

| Field    | Default        |
|----------|----------------|
| Username | `admin`        |
| Password | `Admin@12345`  |

## Quick start (Docker)

```bash
# From repo root
cp .env.example .env
# Edit .env — set JWT_SECRET and passwords

cd management
mvn -DskipTests package

cd ..
docker compose up --build
```

- App: http://localhost:4000
- MailHog UI: http://localhost:8025

## Configuration

| Variable | Description |
|----------|-------------|
| `JWT_SECRET` | Signing key for access/refresh tokens |
| `SPRING_DATASOURCE_*` | Database URL, user, password (prod) |
| `BOOTSTRAP_ADMIN_*` | First-run admin user (disable after setup) |
| `DOCUMENT_STORAGE_DIR` | Uploaded document path |

See `management/src/main/resources/application*.properties` for full options.

## Tests

```bash
cd management
mvn test
```

## API examples

Use the `.http` files under `api-req/` with VS Code REST Client or IntelliJ HTTP Client.

## Push to GitHub

1. Create a new empty repository on GitHub (no README/license if you already have this repo).
2. From this folder:

```bash
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
git branch -M main
git push -u origin main
```

Replace `YOUR_USERNAME` and `YOUR_REPO` with your GitHub details.

## License

Add a license file if you plan to open-source this project.
