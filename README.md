# Capico Real Estate

Capico is a real estate application built as small Spring Boot services. Development uses Java 25, Spring Boot 4.1, PostgreSQL, Maven, and REST.

## Service plan

- `auth-service`: registration, login, logout, users, JWT authentication, and ADMIN/USER access rules.
- `eureka-server`: service registration and discovery.
- `api-gateway`: the single HTTP entry point and request routing.
- Later services: property and images; search; requirements and recommendations; feedback; reporting.

The auth service is being built first. Eureka and the API Gateway are separate infrastructure services; they do not replace authentication.

## Requirements

- JDK 25
- PostgreSQL 18 or compatible
- Git
- Internet access on first build so Maven Wrapper can download Maven and dependencies

Maven is provided by `mvnw.cmd` on Windows and `./mvnw` on macOS/Linux.

## Authentication service

The service runs on port `8081` and expects a PostgreSQL database named `capico_auth`.

Create the database in pgAdmin or `psql`:

```sql
CREATE DATABASE capico_auth;
```

In PowerShell, set your local database password and generate a private, random 256-bit Base64 JWT key before starting the service. Do not commit either value:

```powershell
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "your-local-postgres-password"
$random = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$keyBytes = New-Object byte[] 32
$random.GetBytes($keyBytes)
$env:JWT_SECRET = [Convert]::ToBase64String($keyBytes)
```

Then run:

```powershell
cd services/auth-service
../../mvnw.cmd spring-boot:run
```

On macOS/Linux, use `DB_USERNAME=postgres DB_PASSWORD=... JWT_SECRET=... ../../mvnw spring-boot:run`.

Swagger UI: <http://localhost:8081/swagger-ui.html>

### Auth endpoints

| Method | Path | Access | Purpose |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Create a USER account |
| `POST` | `/api/auth/login` | Public | Verify credentials and return a JWT |
| `POST` | `/api/auth/logout` | Authenticated | Revoke the current JWT |
| `GET` | `/api/auth/me` | Authenticated | Return the current account |

Registration never accepts a role, so a public caller cannot register as an administrator. The initial administrator will be added through a controlled seed/setup step later.

## Working across laptops

Use Git to move source code between machines. Keep passwords, JWT secrets, and database data out of Git. After cloning on Windows, run `git pull` before continuing and `git add`, `git commit`, and `git push` when you want to save your changes to GitHub.
