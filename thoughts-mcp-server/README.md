# Thoughts MCP Server

An MCP (Model Context Protocol) server built with Quarkus that gives AI assistants read-only access to the [thoughts-app](../thoughts-app) PostgreSQL database. It uses the `quarkus-mcp-server-stdio` extension and communicates over standard input/output.

## MCP Tools

The server exposes four read-only tools. All return plain-text formatted output.

### `listThoughts`

Retrieve all thoughts from the database. Takes no parameters.

**Example response:**

```
Found 2 thoughts:

--- Thought #1 ---
Content: Find what you love and let it kill you.
Author: Charles Bukowski
Author Bio: American poet and novelist
Status: ENABLED
```

### `searchThoughtsByContent`

Search thoughts by keyword in their content. The search is case-insensitive and matches partial strings.

| Parameter | Type | Description |
|-----------|------|-------------|
| `keyword` | String | The keyword or phrase to search for in thought content |

**Example:** Searching for `"love"` returns all thoughts whose content contains "love" (case-insensitive).

### `getThoughtsByAuthor`

Get all thoughts by a specific author. The search is case-insensitive and matches partial names.

| Parameter | Type | Description |
|-----------|------|-------------|
| `author` | String | The name of the author to filter by |

**Example:** Searching for `"bukowski"` returns all thoughts by Charles Bukowski.

### `getThoughtById`

Get a specific thought by its database ID.

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | Long | The numeric ID of the thought to retrieve |

## Prerequisites

- Java 25
- The [thoughts-app](../thoughts-app) running in dev mode (provides the PostgreSQL database)

## Quick Start

1. Start the thoughts-app in dev mode (from the `thoughts-app/` directory):

    ```shell
    ./mvnw quarkus:dev
    ```

    This starts PostgreSQL on port **5433** via Quarkus Dev Services and seeds it with sample data.

2. Build the MCP server (from the `thoughts-mcp-server/` directory):

    ```shell
    ./mvnw package -DskipTests
    ```

3. The server is now ready to be launched by an MCP client.

## Claude Code Configuration

Add a `.mcp.json` file to your project root:

```json
{
  "mcpServers": {
    "thoughts": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/thoughts-mcp-server/target/quarkus-app/quarkus-run.jar"
      ],
      "env": {
        "QUARKUS_DATASOURCE_JDBC_URL": "jdbc:postgresql://localhost:5433/quarkus",
        "QUARKUS_DATASOURCE_USERNAME": "quarkus",
        "QUARKUS_DATASOURCE_PASSWORD": "quarkus"
      }
    }
  }
}
```

Once configured, the tools are available in Claude Code as `mcp__thoughts__listThoughts`, `mcp__thoughts__searchThoughtsByContent`, etc.

## Claude Desktop Configuration

Add the following to `~/Library/Application Support/Claude/claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "thoughts": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/thoughts-mcp-server/target/quarkus-app/quarkus-run.jar"
      ],
      "env": {
        "QUARKUS_DATASOURCE_JDBC_URL": "jdbc:postgresql://localhost:5433/quarkus",
        "QUARKUS_DATASOURCE_USERNAME": "quarkus",
        "QUARKUS_DATASOURCE_PASSWORD": "quarkus"
      }
    }
  }
}
```

Restart Claude Desktop after editing the config file.

## Connecting to a Different Database

The default configuration connects to the thoughts-app Dev Services PostgreSQL at `localhost:5433`. To point at a different instance, change the environment variables in the Claude Desktop config:

```json
"env": {
  "QUARKUS_DATASOURCE_JDBC_URL": "jdbc:postgresql://your-host:5432/your-db",
  "QUARKUS_DATASOURCE_USERNAME": "your-user",
  "QUARKUS_DATASOURCE_PASSWORD": "your-pass"
}
```

## Testing with MCP Inspector

You can test the server interactively without Claude Desktop:

```shell
npx @modelcontextprotocol/inspector java -jar target/quarkus-app/quarkus-run.jar
```

## Building a Native Executable

```shell
./mvnw package -Dnative
```

Or without a local GraalVM installation:

```shell
./mvnw package -Dnative -Dquarkus.native.container-build=true
```
