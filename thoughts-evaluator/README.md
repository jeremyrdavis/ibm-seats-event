# thoughts-evaluator

A Quarkus microservice that provides AI-based content moderation for the [Thoughts App](../ibm-seats-event). It consumes thoughts from a Kafka topic, evaluates them using an IBM Granite LLM served by Ollama, and publishes the moderation verdict back to Kafka.

## How It Works

1. Consumes `Thought` messages from the `thoughts-created` Kafka topic.
2. Sends the thought's content to an IBM Granite model via Ollama for evaluation.
3. The AI labels each thought as `APPROVED` or `REJECTED` based on content appropriateness.
4. Publishes a `ThoughtEvaluation` (thought ID + verdict) to the `thoughts-evaluated` Kafka topic.
5. The [ibm-seats-event](../ibm-seats-event) app consumes the verdict and updates the thought's display status.

## Key Classes

- **`ThoughtEvaluator`** — A Quarkus LangChain4j AI service interface (`@RegisterAiService`). Defines a system prompt that instructs the Granite model to act as a content moderator, responding with exactly `APPROVED` or `REJECTED`.
- **`MyMessagingApplication`** — Kafka consumer/producer. Receives thoughts, calls the AI evaluator, and emits the verdict. Uses `@ActivateRequestContext` because AI services are request-scoped by default and Kafka handlers run outside a request context.
- **`Thought`** — Record representing an incoming thought (content, author, authorBio).
- **`ThoughtEvaluation`** — Record representing the moderation result (thought ID + approved boolean).

## Prerequisites: IBM Granite model via Ollama

This application uses an IBM Granite LLM served locally by [Ollama](https://ollama.com/) to evaluate thought content.

1. Install Ollama: https://ollama.com/download
2. Pull the Granite model:

```shell script
ollama pull granite3.2:8b
```

3. Verify the model is available:

```shell script
ollama list
```

Ollama runs on `http://localhost:11434` by default. If your Ollama instance is running elsewhere, update `quarkus.langchain4j.ollama.base-url` in `application.properties`.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

Quarkus Dev Services will automatically start a Kafka broker — no manual setup needed.

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

You can then execute your native executable with: `./target/thoughts-evaluator-0.0.1-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- SmallRye Reactive Messaging - Kafka ([guide](https://quarkus.io/guides/kafka-getting-started)): Connect to Kafka with Reactive Messaging
- Quarkus LangChain4j - Ollama ([guide](https://docs.quarkiverse.io/quarkus-langchain4j/dev/ollama.html)): Integrate with Ollama-served LLMs
