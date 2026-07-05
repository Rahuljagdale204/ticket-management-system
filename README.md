# Ticket Management System

A RESTful Ticket Management API built with Java and Spring Boot, featuring CRUD operations, filtering by status and priority, validation, exception handling, unit testing, and AI-powered ticket enhancements such as priority recommendation, ticket summarization, suggested resolutions, and auto-generated tags.

---

## Features

### Mandatory Requirements

* ✅ Create, Read, Update, and Delete (CRUD) APIs for tickets
* ✅ Filter tickets by status and priority
* ✅ Request validation
* ✅ Global exception handling
* ✅ Unit tests using JUnit and Mockito
* ✅ In-memory database (H2)

### Bonus AI Features

The application integrates AI-powered capabilities using the Google Gemini API:

* 🤖 Ticket Summarization
* 🤖 Priority Recommendation
* 🤖 Suggested Resolution
* 🤖 Auto-generated Tags

---

## Technology Stack

* Java
* Spring Boot
* Spring Data JPA
* H2 Database
* Maven
* JUnit 5
* Mockito
* Lombok
* Google Gemini API

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   ├── config
│   │   ├── dto
│   │   ├── entity
│   │   ├── repository
│   │   ├── service
│   │   ├── util
│   │   └── web
│   └── resources
│       └── application.yaml
└── test
    └── java
        └── service
```

---

## Ticket Entity

The application manages tickets with the following attributes:

| Field        | Type    |
| ------------ | ------- |
| id           | Long    |
| title        | String  |
| description  | String  |
| category     | String  |
| priority     | Enum    |
| status       | Enum    |
| creationDate | Instant |
| deleted      | Boolean |

---

## CRUD API Endpoints

### Create Ticket

```http
POST /rest/v1/api/tickets
```

---

### Get Ticket By ID

```http
GET /rest/v1/api/tickets/{ticketId}
```

---

### Update Ticket Information

```http
PATCH /rest/v1/api/tickets/{ticketId}/info
```

---

### Update Ticket Status

```http
PATCH /rest/v1/api/tickets/{ticketId}/status
```

---

### Get All Tickets (Pagination Supported)

```http
GET /rest/v1/api/tickets/all?page=0&size=10
```

---

### Search Ticket By ID And Status

```http
GET /rest/v1/api/tickets/{ticketId}/search?status=NEW
```

---

### Delete Ticket

```http
DELETE /rest/v1/api/tickets/{ticketId}
```

---

## AI Endpoints

### Get AI Insight for Ticket

```http
GET /rest/v1/api/tickets/{{ticketId}}/ai-insight
```

---

### Apply AI Suggestions to Ticket

```http
PATCH /rest/v1/api/tickets/{{ticketId}}/ai-apply
```

---

## Validation

The application validates incoming requests using Jakarta Bean Validation.

Examples:

* Ticket title cannot be empty.
* Ticket description cannot be empty.
* Invalid status values are rejected.
* Invalid priority values are rejected.

---

## Error Handling

A global exception handler is implemented using `@ControllerAdvice`.

Handled exceptions include:

* ResourceNotFoundException
* InvalidInputException
* Validation exceptions
* Unexpected server exceptions

Example response:

```json
{
  "timestamp": "2026-07-05T10:00:00",
  "message": "Ticket not found with id : 100",
  "status": 404
}
```

---

## Unit Testing

Unit tests are implemented using:

* JUnit 5
* Mockito
* Mock Repository Layer

The tests cover:

* Positive scenarios
* Negative scenarios
* Exception handling
* CRUD operations
* AI services

No real database operations are performed during testing.

---

## Prerequisites

Before running the application, ensure the following are installed:

* Java 17 or above
* Maven 3.8+
* Internet connection (required for Gemini API features)

---

## Configuration

The AI features use the Google Gemini API.

Add your Gemini API key in:

```yaml
src/main/resources/application.yaml
```

Example:

```yaml
gemini:
  api:
    key: YOUR_GEMINI_API_KEY
```

> Note: The API key is intentionally excluded from the repository for security reasons.

---

## Build Instructions

Clone the repository:

```bash
git clone https://github.com/Rahuljagdale204/ticket-management-system.git
cd ticket-management-system
```

Build the application:

```bash
mvn clean install
```

---

## Run the Application

### Option 1: Run using Maven

```bash
mvn spring-boot:run
```

### Option 2: Run the generated JAR

After building:

```bash
cd .\target\
java -jar .\backend-0.0.1-SNAPSHOT.jar
```

---

## H2 Database Console

H2 Console:

```text
http://localhost:8080/h2-console
```

Example configuration:

```text
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password:
```

## API Reference

---

### Create Ticket
- **Method:** `POST`
- **URL:** `{{baseUrl}}/rest/v1/api/tickets`
- **Description:** Creates a new ticket. Returns 201 Created with a success message. The `status`, `category`, and `priority` fields use enum values.
- **Enums:**
  - `status`: `NEW`, `IN_PROGRESS`, `RESOLVED`
  - `category`: `NETWORK`, `HARDWARE`, `SOFTWARE`, `OTHER`
  - `priority`: `LOW`, `MEDIUM`, `HIGH`
- **Headers:** `Content-Type: application/json`
- **Request Body:**
```json
{
  "title": "User - not able to update Order Data!",
  "description": "Production service failed with NPE check for User Credentials",
  "creationDate": "2024-01-15 10:30:00",
  "status": "NEW"
}
```
- **Response:** 201 Created with a success message.

---

### Get Ticket by ID
- **Method:** `GET`
- **URL:** `{{baseUrl}}/rest/v1/api/tickets/{id}`
- **Description:** Retrieves a single ticket by its ID. Returns the full Ticket object.
- **Path Parameters:**
  - `id` — The ID of the ticket.
- **Response:** Full Ticket object.

---

### Get All Tickets (Paginated)
- **Method:** `GET`
- **URL:** `{{baseUrl}}/rest/v1/api/tickets/all`
- **Description:** Retrieves a paginated list of all tickets. Supports Spring Pageable query params.
- **Query Parameters:**

 Parameter | Type    | Description                                      | Example           |
-----------|---------|--------------------------------------------------|-------------------|
 `page`    | integer | Page number (0-indexed). Default: `0`            | `0`               |
 `size`    | integer | Number of items per page. Default: `10`          | `10`              |
 `sort`    | string  | Sort field and direction. Default: `id,asc`      | `creationDate,desc` |

- **Response:** Paginated list of Ticket objects.

---

### Search Ticket by ID and Status
- **Method:** `GET`
- **URL:** `{{baseUrl}}/rest/v1/api/tickets/{id}/search`
- **Description:** Searches for a ticket by its ID and status. The `status` query param is required.
- **Path Parameters:**
  - `id` — The ID of the ticket.
- **Query Parameters:**

 Parameter | Type   | Description                                                        | Example       |
-----------|--------|--------------------------------------------------------------------|---------------|
 `status`  | string | Ticket status to filter by. Allowed values: `NEW`, `IN_PROGRESS`, `RESOLVED` | `NEW` |

- **Response:** Matching Ticket object.

---

### Update Ticket Info
- **Method:** `PATCH`
- **URL:** `{{baseUrl}}/rest/v1/api/tickets/{ticketId}/info`
- **Description:** Updates the info (title, description, creationDate, category, priority) of an existing ticket by its ID.
- **Path Parameters:**
  - `ticketId` — The ID of the ticket.
- **Headers:** `Content-Type: application/json`
- **Enums:**
  - `category`: `NETWORK`, `HARDWARE`, `SOFTWARE`, `OTHER`
  - `priority`: `LOW`, `MEDIUM`, `HIGH`
- **Request Body:**
```json
{
  "title": "Updated Ticket Title",
  "description": "Updated description of the issue",
  "creationDate": "2024-01-15 10:30:00",
  "category": "HARDWARE",
  "priority": "HIGH"
}
```
- **Response:** Updated Ticket object.

---

### Update Ticket Status
- **Method:** `PATCH`
- **URL:** `{{baseUrl}}/rest/v1/api/tickets/{id}/status`
- **Description:** Updates only the status of an existing ticket by its ID.
- **Path Parameters:**
  - `id` — The ID of the ticket.
- **Headers:** `Content-Type: application/json`
- **Enums:**
  - `status`: `NEW`, `IN_PROGRESS`, `RESOLVED`
- **Request Body:**
```json
{
  "status": "IN_PROGRESS"
}
```
- **Response:** Updated Ticket object.

---

### Delete Ticket
- **Method:** `DELETE`
- **URL:** `{{baseUrl}}/rest/v1/api/tickets/{id}`
- **Description:** Deletes (removes) a ticket by its ID.
- **Path Parameters:**
  - `id` — The ID of the ticket.
- **Response:** 204 No Content on success.

---

### Get AI Insight for Ticket
- **Method:** `GET`
- **URL:** `{{baseUrl}}/rest/v1/api/tickets/{ticketId}/ai-insight`
- **Description:** Retrieves AI-generated insights for a specific ticket by its ID. Returns a `TicketAiInsight` object with AI analysis of the ticket.
- **Path Parameters:**
  - `ticketId` — The ID of the ticket.
- **Response:** `TicketAiInsight` object.

---

### Apply AI Suggestions to Ticket
- **Method:** `PATCH`
- **URL:** `{{baseUrl}}/rest/v1/api/tickets/{ticketId}/ai-apply`
- **Description:** Optional convenience endpoint — applies AI-suggested priority/category to a ticket if not already set.
- **Path Parameters:**
  - `ticketId` — The ID of the ticket.
- **Response:** Updated `Ticket` object.

---

## Future Enhancements
* User Support for Ticket along with AduitLog & comments
* Authentication and Authorization
* Swagger/OpenAPI Documentation
* Docker Support
* Database Persistence using MySQL/PostgreSQL
* AI Model Fine-Tuning
* Ticket Assignment Workflow

---

## Author

**Rahul Jagdale**

---

## License

This project was developed as part of a technical assessment and is intended for educational and evaluation purposes.

---