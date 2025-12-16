# Task List REST API using Spring Boot (Kotlin)

A Spring Boot application with task management features and NATS-based event messaging.

## Features

- Retrieve tasks (GET)
- Create tasks with name, description and status (POST)
- Update tasks (PUT)
- Delete tasks (DELETE)
- NATS messaging for all modifying operations (POST/PUT/DELETE)

## Prerequisites

- Java 17
- Maven 3.6+
- Docker and Docker Compose (for NATS and MySQL)

## Getting Started

### 1. Start Required Services with Docker Compose

Start MySQL and NATS servers:

```bash
docker-compose up -d
```

This will start:
- **MySQL** on port 3306
- **NATS** on port 4222 (client) and 8222 (monitoring)

### 2. Run the Main Application

```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

### 3. Run the NATS Listener (Optional)

To see NATS messages in real-time, start the listener application:

```bash
cd nats-listener
mvn spring-boot:run
```

The listener will log all events published when POST/PUT/DELETE operations are performed on the main application.

See [nats-listener/README.md](nats-listener/README.md) for more details.

## NATS Integration

The application publishes events to NATS whenever a modifying API operation is called:
- **POST** requests (create operations)
- **PUT** requests (update operations)
- **DELETE** requests (delete operations)

### Configuration

NATS configuration is in `src/main/resources/application.properties`:

```properties
nats.server-url=nats://localhost:4222
nats.subject=app.events
```

### Event Message Format

```json
{
  "timestamp": "2024-01-01T12:00:00.000Z",
  "subject": "app.events",
  "httpMethod": "POST",
  "path": "/api/tasks",
  "principal": "username",
  "body": "{...request body...}",
  "status": "success"
}
```

## Testing NATS Integration

1. Start all services (docker-compose, main app, nats-listener)
2. Make a POST request:
   ```bash
   curl -X POST http://localhost:8080/api/tasks \
     -H "Content-Type: application/json" \
     -d '{"title":"Test Task","description":"Test","status":"PENDING"}'
   ```
3. Check the nats-listener console for the published event

## API Documentation

When the application is running, view the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

## Stopping Services

```bash
docker-compose down
```
