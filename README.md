# App Store Service

This is a Spring Boot service that simulates an App Store backend. It supports CRUD operations for apps, full-text search via Elasticsearch, and persistence via JPA Cockroach db.
It also publishes events when new apps are created.

---

## Features

- `GET /apps` — List all apps
- `GET /apps/{id}` — Fetch app by ID
- `GET /apps/search?keyword=...` — Full-text search by keyword
- `POST /apps` — Create or update an app (upserts based on app name)
- Search backed by **Elasticsearch**
- Persistence backed by **JPA**
- Publishes `AppCreatedEvent` on successful save

---

## Technologies

- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- Elasticsearch Java Client
- Cockroach db 
- JUnit 5 + Mockito for testing

---

## Getting Started

### Clone the Repo


## Start dependent services

This project relies on two external services:

- **CockroachDB** (PostgreSQL-compatible)
- **Elasticsearch** (for full-text search)

| Service       | Port(s)    | Notes                                                      |
|---------------|------------|------------------------------------------------------------|
| CockroachDB   | 26257      | SQL port (JDBC connections)                                |
| CR DB Admin   | 8081       | Admin UI ([http://localhost:8081](http://localhost:8081))  |
| Elasticsearch | 9200/9300  | REST and transport ports                                   |



Start both services locally:

```bash
docker-compose up
```


## Run Spring boot app

```bash
./mvnw spring-boot:run
```






