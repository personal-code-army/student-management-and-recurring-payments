# Student Management & Recurring Payments

Web system for student management and recurring payments, built for a real client (a martial arts gym). It covers authentication, administrative control, plans, subscriptions and automatic billing.

[![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/personal-code-army/student-management-and-recurring-payments?utm_source=oss&utm_medium=github&utm_campaign=personal-code-army%2Fstudent-management-and-recurring-payments&labelColor=171717&color=FF570A&label=CodeRabbit+Reviews)](https://coderabbit.ai)

## Features

- **Authentication** — JWT (Spring Security OAuth2 Resource Server), with admin and user roles
- **Students** — create, update, search and remove students; CPF stored encrypted
- **Plans & subscriptions** — configurable plans and student subscriptions
- **Payments** — payment records plus a scheduled job that flags overdue payments
- **Companies** — users are linked to a company (gym)

## Tech stack

| Layer | Technologies |
|---|---|
| Back-end | Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA, Flyway, Lombok |
| Front-end | Next.js 16, React 19, TypeScript, Tailwind CSS 4, shadcn/ui (Radix), Axios |
| Database | PostgreSQL 16 (Docker Compose, with pgAdmin) |
| Quality | JUnit + Spring Security Test, Vitest, CodeRabbit reviews, GitHub Actions |

## Architecture

The back-end follows **hexagonal architecture (ports & adapters)**:

```
backend/src/main/java/br/com/geloteam/studentmanagement
├── domain/          # entities + ports (in = use cases, out = repositories)
├── application/     # use case implementations
├── infrastructure/  # adapters: web (controllers), persistence (JPA), config, scheduling
└── shared/
```

The domain doesn't depend on Spring or JPA; persistence is behind `*RepositoryPort` interfaces, implemented by `*RepositoryAdapter` classes.

## Running locally

Prerequisites: **Java 21**, **Node.js 20+**, **Docker**.

### 1. Database

```bash
cd backend
# create a .env with DB_NAME, DB_USER, DB_PASSWORD, PGADMIN_EMAIL, PGADMIN_PASSWORD
docker compose up -d   # Postgres on localhost:5433, pgAdmin on localhost:5050
```

### 2. Back-end

Set the environment variables used by `application.properties`:

| Variable | Description |
|---|---|
| `DB_URL` | e.g. `jdbc:postgresql://localhost:5433/<db_name>` |
| `DB_USER` / `DB_PASSWORD` | database credentials |
| `API_SECURITY_TOKEN_SECRET` | JWT signing secret |
| `API_CPF_ENCRYPTION_KEY` | key used to encrypt CPFs |
| `CORS_ALLOWED_ORIGINS` | defaults to `http://localhost:3000` |

```bash
cd backend
./mvnw spring-boot:run   # API on http://localhost:8080 (Flyway runs the migrations)
```

A Postman collection is available at [`backend/postman_collection.json`](backend/postman_collection.json).

### 3. Front-end

```bash
cd frontend
npm install
npm run dev   # http://localhost:3000
```

### Tests

```bash
cd backend && ./mvnw test
cd frontend && npm test
```

## Contributing

Branches follow the task ID (`GT-<n>-<type>/<description>`), and every change goes through a pull request with automated review.

## License

See [LICENSE](LICENSE).
