# Thoughts App

A Quarkus application that lets users submit, moderate, and display "thoughts" (quotes and ideas). It provides a REST API for creating and listing thoughts, server-side rendered HTML pages via Qute templates, and event-driven integration with the [thoughts-evaluator](../thoughts-evaluator) microservice through Kafka.

## How It Works

1. A user submits a thought via `POST /api/thoughts`.
2. The thought is persisted to PostgreSQL with a `PENDING` status and published to the `thoughts-created` Kafka topic.
3. The [thoughts-evaluator](../thoughts-evaluator) microservice consumes the thought, evaluates it using an IBM Granite LLM, and publishes the verdict to the `thoughts-evaluated` Kafka topic.
4. The `EvaluationSink` in this app consumes the verdict and updates the thought's status to `ENABLED` (approved) or `DISABLED` (rejected).
5. Only `ENABLED` thoughts are shown in the UI.

## REST API

| Method | Path | Produces | Description |
|--------|------|----------|-------------|
| POST | `/api/thoughts` | `application/json` | Create a new thought |
| GET | `/api/thoughts` | `application/json` | List all thoughts (any status) |

## UI Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/ui/random` | Display a random approved thought as an HTML card |
| GET | `/ui/all` | Display all approved thoughts as an HTML page |

## Key Classes

- **`Thought`** — JPA entity (Panache active record) with fields: `content`, `author`, `authorBio`, `displayStatus`.
- **`DisplayStatus`** — Enum representing the moderation lifecycle: `PENDING` → `ENABLED` or `DISABLED`.
- **`ThoughtResource`** — REST API for creating and listing thoughts. Publishes new thoughts to Kafka.
- **`UIResource`** — Serves HTML pages rendered with Qute checked templates. Only displays approved thoughts.
- **`EvaluationSink`** — Kafka consumer that receives moderation results and updates thought status in the database.
- **`ThoughtEvaluation`** — Record representing a moderation verdict (thought ID + approved/rejected).

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

Quarkus Dev Services will automatically start a PostgreSQL database and a Kafka broker — no manual setup needed.

> **_NOTE:_** Quarkus ships with a Dev UI, available in dev mode at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it's not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/ibm-seates-event-0.0.1-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- Qute ([guide](https://quarkus.io/guides/qute)): Offer templating support for web, email, etc in a build time, type-safe way
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC
- SmallRye Reactive Messaging - Kafka ([guide](https://quarkus.io/guides/kafka-getting-started)): Connect to Kafka with Reactive Messaging
