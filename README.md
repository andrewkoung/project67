# Wise Quotes API

A lightweight Java (JDK 24) API that serves wise quotes, designed for DevOps learning and production-like simulations.

Features:
- REST API: list, get, random, create, delete quotes
- Health and readiness endpoints for orchestration
- Prometheus metrics endpoint
- Environment-driven configuration
- Structured logging
- Dockerized build and run

## Quick start

Run locally (no Docker):
1) Install Java 24 and Maven 3.9+
2) Build the jar:
   - mvn package
3) Start the API:
   - java -jar target/wise-quotes-api.jar
4) Verify it’s running:
   - curl http://localhost:8080/health
   - curl http://localhost:8080/api/v1/quotes/random

Run with Docker:
1) Build the image:
   - docker build -t wise-quotes-api .
2) Start a container:
   - docker run --rm -p 8080:8080 wise-quotes-api
3) Verify:
   - curl http://localhost:8080/health
   - curl http://localhost:8080/api/v1/quotes/random

Change the port (example: 9090):
- APP_PORT=9090 java -jar target/wise-quotes-api.jar
- Or with Docker: docker run --rm -e APP_PORT=9090 -p 9090:9090 wise-quotes-api

Create a quote (example):
- curl -X POST -H "Content-Type: application/json" -d '{"text":"Stay hungry, stay foolish","author":"Steve Jobs"}' http://localhost:8080/api/v1/quotes

Stop the app:
- Press Ctrl+C in the terminal where it’s running (or stop the Docker container).

## Build and Run

Prerequisites:
- Java 24 (or use Docker)
- Maven 3.9+

Build:
- mvn package

Run locally:
- java -jar target/wise-quotes-api.jar
- Or with custom port: APP_PORT=9090 java -jar target/wise-quotes-api.jar

Docker:
- docker build -t wise-quotes-api .
- docker run --rm -p 8080:8080 wise-quotes-api

## Environment Variables

- APP_PORT (default: 8080)
- APP_CONTEXT_PATH (default: /)
- ENABLE_PROMETHEUS (default: true)
- STARTUP_READY_DELAY_MS (default: 0) - simulates startup delay for readiness
- QUOTES_SEED_FILE (optional) - path to a JSON file with an array of { "text": "...", "author": "..." }

## Endpoints

Assuming APP_CONTEXT_PATH=/:

- GET /api/v1/quotes?page=0&size=20 - list quotes (paginated)
- GET /api/v1/quotes/random - random quote
- GET /api/v1/quotes/{id} - get quote by id
- POST /api/v1/quotes - create quote
  - Body: { "text": "string", "author": "string" }
- DELETE /api/v1/quotes/{id} - delete quote

Operational:
- GET /health - liveness
- GET /ready - readiness (200 when ready, 503 until STARTUP_READY_DELAY_MS passes)
- GET /metrics - Prometheus scrape (text/plain)

## Example

Create:
- curl -X POST -H "Content-Type: application/json" -d '{"text":"Stay hungry, stay foolish","author":"Steve Jobs"}' http://localhost:8080/api/v1/quotes

Random:
- curl http://localhost:8080/api/v1/quotes/random

## Testing

- mvn test

## Notes

- Logs are printed to stdout in a concise format.
- You can mount a seed file into the container and set QUOTES_SEED_FILE to pre-load quotes.
