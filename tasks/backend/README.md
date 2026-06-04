# Task Management Application

A robust RESTful API for managing tasks, built with Spring Boot. This application supports multi-database configurations (H2 for development, MongoDB for production), role-based access control, and comprehensive API documentation.

## Features

- **CRUD Operations**: Create, read, update, and delete tasks.
- **Security**: Role-Based Access Control (RBAC) with Spring Security.
- **Dynamic Database Profiles**:
  - `dev` profile uses an in-memory **H2 Database**.
  - `prod` profile connects to **MongoDB**.
- **Data Transfer Objects (DTOs)**: MapStruct and Lombok used for seamless entity-to-DTO conversions.
- **Global Error Handling**: Standardized problem details for exceptions.
- **API Documentation**: Interactive Swagger UI via OpenAPI 3.

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.x**
  - Spring Web MVC
  - Spring Data JPA
  - Spring Data MongoDB
  - Spring Security
- **MapStruct** & **Lombok**
- **H2 Database** (Development)
- **MongoDB** (Production)
- **JaCoCo** (Test Coverage)
- **Spotless** (Code Formatting)

---

## Getting Started

### Prerequisites

- JDK 17
- Maven 3.8+
- Docker (optional, for running MongoDB locally)

### Running Locally (Development Profile)

By default, the application runs using the `dev` profile with the H2 in-memory database.

```bash
# Clone the repository
git clone https://github.com/yourusername/task-management.git
cd task-management/tasks

# Run the application
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The H2 console is accessible at: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:taskdb`
- **User**: `sa`
- **Password**: *(leave blank)*

### Running with MongoDB (Production Profile)

To run the application with the `prod` profile, you need a MongoDB instance.

1. **Start MongoDB via Docker**:
   ```bash
   docker-compose up -d
   # OR
   docker run -d -p 27017:27017 --name mongodb mongo:7
   ```

2. **Run the Application**:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

---

## API Documentation

Once the application is running, the interactive Swagger UI and OpenAPI specifications are available at:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI v3 Docs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Testing & Reports

This project uses JUnit 5 and Mockito for unit testing. JaCoCo is integrated to generate test coverage reports.

```bash
# Run tests and generate coverage report
mvn clean test jacoco:report
```

The HTML coverage report can be found at:
`target/site/jacoco/index.html`

---

## Project Structure & Git Strategy

We follow the standard Git Flow branching strategy:
- `main`: Contains production-ready code.
- `develop`: The main integration branch for active development.
- `feature/*`: Branches for developing new features.

```
src/
├── main/
│   ├── java/com/management/tasks/
│   │   ├── config/        # Security and App Configurations
│   │   ├── controllers/   # REST API Endpoints
│   │   ├── dto/           # Request and Response Records
│   │   ├── entity/        # JPA Entities and MongoDB Documents
│   │   ├── exceptions/    # Global Exception Handling
│   │   ├── mapper/        # MapStruct Mappers
│   │   ├── repository/    # Spring Data Repositories
│   │   ├── security/      # OAuth2 and User Details Services
│   │   └── services/      # Business Logic Layer
│   └── resources/
│       ├── application.yaml
│       ├── application-dev.yaml
│       └── application-prod.yaml
└── test/                  # Unit and Integration Tests
```

---

## Postman Collection

A `postman_collection.json` file is included in the `docs/` directory. Import it into Postman and use the `{{base_url}}` variable set to `http://localhost:8080` to interact with the API.
