# SSCC Validator

A application for validating and saving SSCC (Serial Shipping Container Code) numbers. The system provides a standalone validation service accessible via a REST API, and a interface that consumes the service.

## Tech Stack

- **Java 25**
- **Spring Boot 4.1**
- **springdoc-openapi 3.0.0** (Swagger UI)
- **Docker & Docker Compose**
- **Nginx** (frontend static hosting + reverse proxy)
- **HTML / CSS / JavaScript** (frontend)
- **Maven**

## Architecture

```
┌─────────────────────┐        HTTP         ┌──────────────────────┐
│      Frontend       │ ──── /api/v1/ ────▶ │       Backend        │
│   (nginx + HTML)    │                     │   (Spring Boot)      │
│    port 3000        │                     │    port 8080         │
└─────────────────────┘                     └──────────────────────┘
```

- **Backend** — stateless Spring Boot service that is agnostic to the FE solution.
- **Frontend** — HTML/CSS/JS files served by nginx, which also reverse-proxies API calls to the backend (prevent CORS issues).

## SSCC Validation Rules

An SSCC is an 18-digit numeric code defined by the GS1 standard. The validation service checks:

1. **Not empty** — input from user must be present (not blank)
2. **Sanitization** — removes dashes, spaces, and parentheses if present. Also removes leading `00` if found. These are used to indicate that the following 18 digits comprise an SSCC
3. **Length** — must be exactly 18 digits after sanitization
4. **Numeric values only** — must contain only digits
5. **Check digit** — the 18th digit must match the GS1 mod-10 check digit calculated from the first 17 digits
6. **GS1 (company) prefix** _(optional validation)_ — if a company GS1 prefix is provided, it must match the prefix embedded in the SSCC (starting at position 2)

## Project Structure

```
sscc-validator/
├── docker-compose.yml
├── README.md
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw
│   └── src/main/java/com/example/backend/
│       ├── BackendApplication.java
│       ├── common/
│       │   ├── CustomException.java                # Custom exception
│       │   ├── ErrorResponseDto.java
│       │   └── GlobalExceptionHandler.java
│       └── sscc/
│           ├── SsccController.java                 # Endpoints for SSCC validation
│           ├── dto/
│           ├── enums/
│           ├── service/
│           │   ├── SsccService.java                # SSCC validation endpoints logic
│           │   ├── SsccStorageService.java         # In-memory SSCC list (storage)
│           │   └── SsccValidationService.java      # Neutral validation logic
│           └── util/
├── backend/src/test/java/com/example/backend/
│   └── sscc/
│       ├── SsccControllerTest.java                 # Controller tests
│       └── service/
│           ├── SsccServiceTest.java                # Service tests
│           ├── SsccValidationServiceTest.java      # Validation logic unit tests
│           └── SsccStorageServiceTest.java         # Storage unit tests
└── frontend/
    ├── Dockerfile
    ├── nginx.conf
    └── src/
        ├── index.html
        ├── styles.css
        └── app.js
```

## API Endpoints

Check Swagger documentation on `http://localhost:8080/swagger-ui.html`

### Error Codes

| Code                  | Status | Description                                      |
| --------------------- | ------ | ------------------------------------------------ |
| `EMPTY_INPUT`         | 422    | SSCC was null or blank                           |
| `INVALID_LENGTH`      | 422    | Not exactly 18 digits after sanitization         |
| `NON_NUMERIC`         | 422    | Contains non-digit characters                    |
| `INVALID_CHECK_DIGIT` | 422    | GS1 mod-10 check digit does not match            |
| `PREFIX_MISMATCH`     | 422    | GS1 company prefix does not match provided value |
| `DUPLICATE`           | 409    | SSCC already exists in the saved list           |

## Getting Started

### Prerequisites

- Docker and Docker Compose

### Running with Docker

Start the entire application (backend + frontend):

```bash
docker compose up --build
```

Stop and remove containers:

```bash
docker compose down
```

This starts:

- `backend` — the Spring Boot API on **http://localhost:8080**
- `frontend` — the web UI on **http://localhost:3000**

This does not start:

- `tests` — separate container for running tests - tests do not run on application startup. See the [Testing](#testing) section for instructions.

### Accessing the Application

| Resource   | URL                                           |
| ---------- | --------------------------------------------- |
| Web UI     | `http://localhost:3000`                       |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI    | `http://localhost:8080/v3/api-docs`           |

## Testing

Run all tests:

```bash
docker compose run --rm tests
```

## Key Design Decisions

- **Validation service** — `SsccValidationService` has no dependencies. It can be extracted into a shared library for reuse in future projects.
- **Services throw errors, GlobalExceptionHandler translates those** — exceptions are propagated - the `GlobalExceptionHandler` maps them to appropriate HTTP status code and message.
- **In-memory storage** — The list clears when the application restarts and values are not saved.
- **No CORS needed** — nginx reverse-proxies all API calls, so the browser only communicates with a single origin. No CORS headers needed on the backend.
- **Separate applications** — backend and frontend are fully independent, communicating via HTTP. This reinforces the microservice architecture.
- **Input sanitization** — the validator strips dashes, spaces, parentheses, and the `00` GS1 identifier before validation, accepting common barcode scanner output formats.

## Future Work

- **Persistence** — replace in-memory storage with a database to persist the values.
- **Tests** — Improve and expande tests to include new scenarios.
- **Rate limiting** — add request throttling to prevent abuse of the validation endpoint.
