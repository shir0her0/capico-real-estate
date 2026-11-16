# Capico Real Estate

A small Spring Boot case-study project using Java 25, PostgreSQL, REST APIs, and Maven.

## Services

- `auth-service`: registration, login, logout, user accounts, and ADMIN/USER access.
- `eureka-server`: service registration and discovery.
- `api-gateway`: the entry point that routes requests.
- Later: property, search, requirements/recommendations, feedback, and reporting.

## Run the authentication service

Requirements: JDK 25, Git, and PostgreSQL 18 or compatible. Maven is downloaded by the Maven Wrapper on the first run.

Create a database called `capico_auth` in pgAdmin:

```sql
CREATE DATABASE capico_auth;
```

In PowerShell, set the username and password used by your local PostgreSQL installation:

```powershell
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "your-local-postgres-password"
```

The service has a simple fixed JWT key for this local case-study demo. You can start it without configuring a key. Set `JWT_SECRET` only if you want to override the demo key.

To create the first administrator at startup, set these values. Public registration always creates a `USER`; an existing account is never promoted by the bootstrap:

```powershell
$env:BOOTSTRAP_ADMIN_ENABLED = "true"
$env:BOOTSTRAP_ADMIN_EMAIL = "admin@example.com"
$env:BOOTSTRAP_ADMIN_PASSWORD = "choose-a-password-of-at-least-8-characters"
$env:BOOTSTRAP_ADMIN_FIRST_NAME = "Capico"
$env:BOOTSTRAP_ADMIN_LAST_NAME = "Admin"
$env:BOOTSTRAP_ADMIN_MOBILE_NUMBER = "9999999999"
```

Run from the repository root:

```powershell
cd services\auth-service
..\..\mvnw.cmd spring-boot:run
```

Swagger UI: <http://localhost:8081/swagger-ui.html>

## Try the API

Import [`postman/Capico Auth Service.postman_collection.json`](postman/Capico%20Auth%20Service.postman_collection.json) into Postman and run the collection in order. It creates a fresh test account, logs in, calls `/me` with the returned token, and calls logout.

The endpoints are:

| Method | Path | Access | Purpose |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Create a USER account |
| `POST` | `/api/auth/login` | Public | Check credentials and return a JWT |
| `POST` | `/api/auth/logout` | Authenticated | Tell the client to discard its token |
| `GET` | `/api/auth/me` | Authenticated | Return the current account |

Logout is intentionally simple for this demo: JWTs are stateless, so the client deletes its token. A token already copied elsewhere remains valid until its 30-minute expiry. The service does not keep a token-revocation table.

## Work across laptops

Use Git to move source code between machines. Keep local passwords and database files out of Git. Pull before continuing on the other laptop, and push commits when you want to share your changes.
