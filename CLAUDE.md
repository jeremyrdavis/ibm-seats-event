# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a demo repository for an IBM event showcasing Quarkus. It contains a "Thoughts App" that demonstrates the developer inner loop, data persistence, messaging, AI integration, and Kubernetes deployment. The demo script is in `script.md`.

Currently only the first app (`ibm-seats-event/`) exists — a Quarkus REST API with server-side HTML templating. The demo plan calls for a second consumer app with Kafka messaging and LangChain4j AI integration (not yet created).

## Build & Dev Commands

All commands run from the `ibm-seats-event/` directory:

```bash
./mvnw quarkus:dev          # Dev mode with live reload (Dev Services auto-starts PostgreSQL)
./mvnw package              # Build the application
./mvnw test                 # Run unit tests
./mvnw verify               # Run unit + integration tests
./mvnw package -Dnative     # Build native executable (requires GraalVM)
```

Dev UI is available at http://localhost:8080/q/dev/ when running in dev mode.

## Architecture

### ibm-seats-event

Single-module Quarkus 3.34.5 app (Java 25) under package `com.redhat.demos.thoughts`.

- **`Thought.java`** — JPA entity extending `PanacheEntity` (active record pattern). Fields: `content`, `author`, `authorBio`.
- **`ThoughtResource.java`** — JAX-RS resource at `/api/thoughts` with three endpoints:
  - `POST /api/thoughts` — create and persist a thought (JSON)
  - `GET /api/thoughts` — list all thoughts (JSON)
  - `GET /api/thoughts/random` — render a random thought as HTML via Qute `CheckedTemplate`
- **Qute template** at `src/main/resources/templates/ThoughtResource/randomThought.html` — Qute convention: template path must match `{ResourceClass}/{methodName}.html`.
- **`import.sql`** — seeds the database on startup (Hibernate `drop-and-create` strategy).

### Key Dependencies

Hibernate ORM with Panache, Qute templating, Quarkus REST + Jackson, PostgreSQL via JDBC. Test dependencies (quarkus-junit, rest-assured) are present but no tests exist yet.

### Database

PostgreSQL managed by Quarkus Dev Services (no manual DB setup needed in dev mode). Schema is dropped and recreated on each start (`drop-and-create`). SQL logging is enabled at DEBUG level.
