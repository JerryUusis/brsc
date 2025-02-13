# Bug Report Survey Creator (BRSC) API

**Bug Report Survey Creator (BRSC) API** is a backend service built with **Spring Boot and Kotlin**. It provides CRUD
operations to manage surveys related to bug reporting, allowing seamless integration with the BRSC frontend application.

## Usage

### Prerequisites

Before you start, make sure you have the following installed:

- Java 17+ 
- Kotlin 1.9+ 
- Gradle 8+ 
- Docker (Optional, but recommended for running PostgreSQL)

### Installation

#### 1. Clone the repository

```bash
git clone https://github.com/JerryUusis/brsc
```

#### 2. Setup the database

Instructions to be implemented...

#### 3. Run the application

Run the following commands from the root folder of the project

For development run with Gradle:

```bash
./gradlew bootRun
```

For deployment

1. Build the project

```bash
./gradlew build
```

2. Run the JAR file

```bash
java -jar build/libs/brsc-api-0.0.1-SNAPSHOT.jar
```

If everything works, the API will be available
at: [http://localhost:8080/api/surveys](http://localhost:8080/api/surveys)

#### 4. Test the endpoints

This section provides examples for testing the BRSC API endpoints using cURL or other API testing tools like Postman.

**GET**

- Endpoint: `GET /api/surveys`

Returns all task objects stored in the database in as array of objects in JSON

- Response example (200 OK):

```json
[
  {
    "id": 1,
    "issueNumber": 124,
    "issueLink": "www.example1.com",
    "taskTitle": "Check header styles for typos",
    "instructions": [
      "Navigate to url",
      "Check header spacing and typo"
    ]
  },
  {
    "id": 2,
    "issueNumber": 125,
    "issueLink": "www.example2.com",
    "taskTitle": "Check login button logic",
    "instructions": [
      "Go to login page",
      "Try clicking the button"
    ]
  }
]
```

- Endpoint: `GET /api/surveys/{id}`

Returns a single task object

- Request example: `GET /api/surveys/1`:
- Response example (200 OK):

```json
{
  "id": 1,
  "issueNumber": 124,
  "issueLink": "www.example1.com",
  "taskTitle": "Check header styles for typos",
  "instructions": [
    "Navigate to url",
    "Check header spacing and typo"
  ]
}
```

**POST**

- Endpoint: `POST /api/surveys`
  Creates a new task object in the database. The request body must include survey details (except id, which is
  auto-generated).
- Request example:

```bash
curl -X POST "http://localhost:8080/api/surveys" \
     -H "Content-Type: application/json" \
     -d '{
  "issueNumber": 124,
  "issueLink": "www.example1.com",
  "taskTitle": "Check header styles for typos",
  "instructions": [
    "Navigate to url",
    "Check header spacing and typo"
  ]
}'
```

- Response example (201 Created)
    - Status: 201 Created
    - Headers
        - Location: http://localhost:8080/api/surveys/1

The newly created task object is stored in the database, and the response includes a Location header pointing to its
URL.

## Database architecture

### `surveys`

| Column       | Type         | Constraints                |
|-------------|-------------|----------------------------|
| id          | BIGSERIAL    | PRIMARY KEY               |
| issue_number | INT         | NOT NULL                   |
| issue_link  | VARCHAR(255) | NOT NULL                   |
| task_number | INT         | NOT NULL                   |
| task_title  | VARCHAR(255) | NOT NULL                   |

### `instructions`

| Column           | Type         | Constraints                                             |
|------------------|-------------|---------------------------------------------------------|
| id              | BIGSERIAL    | PRIMARY KEY                                            |
| instruction_text | TEXT         | NOT NULL                                              |
| survey_id       | BIGINT       | FOREIGN KEY -> surveys(id), NOT NULL, ON DELETE CASCADE |

### `users`

| Column        | Type         | Constraints                                        |
|--------------|-------------|----------------------------------------------------|
| id           | BIGSERIAL    | PRIMARY KEY                                       |
| username     | VARCHAR(255) | NOT NULL, UNIQUE                                  |
| password_hash | TEXT        | NOT NULL                                          |
| email        | VARCHAR(255) | NOT NULL, UNIQUE                                 |
| created_at   | TIMESTAMP    | NOT NULL, DEFAULT CURRENT_TIMESTAMP              |

### `roles`

| Column | Type         | Constraints         |
|--------|-------------|---------------------|
| id     | BIGSERIAL   | PRIMARY KEY         |
| name   | VARCHAR(255) | NOT NULL, UNIQUE   |

### `user_roles`

| Column   | Type   | Constraints                                      |
|----------|--------|--------------------------------------------------|
| user_id  | BIGINT | FOREIGN KEY -> users(id), NOT NULL, ON DELETE CASCADE |
| role_id  | BIGINT | FOREIGN KEY -> roles(id), NOT NULL, ON DELETE CASCADE |
| PRIMARY KEY | (user_id, role_id) | |

### Default Role Insertions

```sql
INSERT INTO roles(name) VALUES ('USER') ON CONFLICT DO NOTHING;
INSERT INTO roles(name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;

## Technologies used

- Spring Boot (Kotlin)
- PostgreSQL
- RESTful API with JSON responses
- Spring Data JPA
- Testcontainers
- Docker image

## To be implemented...

- CI/CD to automate builds and testing
- Automated deployment to 
- Deploy to a cloud provider
- Secure the API with authentication (Spring Security, JWT)
