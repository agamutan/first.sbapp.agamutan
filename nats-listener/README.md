# NATS Listener Application

A minimal Spring Boot application that subscribes to NATS messages published by the main application.

## Purpose

This application listens to events published to NATS whenever a modifying API operation (POST/PUT/DELETE) is called on the main application. It logs all received messages with full payload and metadata.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Running NATS server (see docker-compose setup below)

## Configuration

The application is configured via `src/main/resources/application.properties`:

```properties
nats.server-url=nats://localhost:4222
nats.subject=app.events
server.port=8081
```

- `nats.server-url`: URL of the NATS server to connect to
- `nats.subject`: Subject/topic to subscribe to for messages
- `server.port`: Port for the Spring Boot application (8081 to avoid conflict with main app)

## Running with Docker Compose

### 1. Start NATS server

From the repository root directory:

```bash
docker-compose up -d nats
```

This will start the NATS server on port 4222 (client port) and 8222 (monitoring port).

### 2. Start the nats-listener application

From the `nats-listener` directory:

```bash
cd nats-listener
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/nats-listener-0.0.1-SNAPSHOT.jar
```

### 3. Start the main application

From the repository root:

```bash
mvn spring-boot:run
```

## Testing

Once both applications and NATS are running, you can test the integration:

1. Make a POST request to the main application:
   ```bash
   curl -X POST http://localhost:8080/api/tasks \
     -H "Content-Type: application/json" \
     -d '{"title":"Test Task","description":"Test Description","status":"PENDING"}'
   ```

2. Check the nats-listener console output. You should see log entries like:
   ```
   INFO  c.a.n.service.NatsSubscriber - Received message on subject 'app.events': {"timestamp":"2024-01-01T12:00:00Z","subject":"app.events","httpMethod":"POST","path":"/api/tasks","principal":"anonymous","body":"{...}","status":"success"}
   ```

## Message Format

Messages published to NATS have the following JSON structure:

```json
{
  "timestamp": "2024-01-01T12:00:00.000Z",
  "subject": "app.events",
  "httpMethod": "POST",
  "path": "/api/tasks",
  "principal": "anonymous",
  "body": "{...request body...}",
  "status": "success"
}
```

## Stopping the Applications

To stop the NATS server:

```bash
docker-compose down
```

To stop the listener application, press `Ctrl+C` in the terminal where it's running.
