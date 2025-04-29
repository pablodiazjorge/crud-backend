# Backend CRUD - Book Management API

This is a **Book Management API** built using **Spring Boot** with **PostgreSQL** as the database. It serves as the backend for the [Frontend CRUD Book Management Application](https://github.com/pablodiazjorge/crud-frontend/tree/simple) and provides RESTful endpoints for complete CRUD (Create, Read, Update, Delete) functionality to manage books.

The project follows a standard Spring Boot architecture with controllers, services, repositories, and entities, leveraging **Spring Data JPA** for database operations.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
- [Database Structure](#database-structure)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Technologies Used](#technologies-used)

---

## Overview

This backend API supports the frontend Angular application by providing endpoints to:
- Retrieve a list of books.
- Fetch a specific book by ID.
- Create new books.
- Update existing books.
- Delete books.

It connects to a PostgreSQL database and uses Spring Boot’s robust ecosystem for persistence and RESTful communication.

---

## Features

- **RESTful Endpoints**: CRUD operations exposed via standard HTTP methods (GET, POST, PUT, DELETE).
- **Error Handling**: Returns appropriate HTTP status codes (e.g., 200 OK, 404 Not Found, 400 Bad Request).
- **Database Persistence**: Uses Spring Data JPA with Hibernate for seamless database interaction.
- **CORS Support**: Configured to allow requests from the Angular frontend running on `http://localhost:4200/`.

---

## Project Structure

```
crud-backend [crud]
│
├── .idea                # IntelliJ IDEA configuration (optional)
├── .mvn                 # Maven wrapper files
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.pablodiazjorge.crud
│   │   │       ├── controllers
│   │   │       │   └── BookController.java    # REST controller for book endpoints
│   │   │       ├── entities
│   │   │       │   └── Book.java              # Book entity definition
│   │   │       ├── repositories
│   │   │       │   └── BookRepository.java    # JPA repository interface
│   │   │       └── services
│   │   │           ├── BookService.java       # Service interface
│   │   │           └── BookServiceImpl.java   # Service implementation
│   │   └── resources
│   │       ├── static                         # Static resources (empty in this project)
│   │       ├── templates                      # Templates (empty in this project)
│   │       └── application.properties         # Configuration file
└── test                 # Unit/integration tests (to be implemented)
```

---

## Getting Started

### Prerequisites

Before running the application, ensure you have the following installed:
- **Java Development Kit (JDK)** (v17+)
- **Maven** (for building the project)
- **PostgreSQL** (for the database)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/pablodiazjorge/crud-backend/tree/simple
   ```
2. Navigate to the project directory:
   ```bash
   cd crud-backend
   ```
3. Install dependencies and build the project:
   ```bash
   mvn clean install
   ```

---

## Configuration

### Database Configuration

Open the `application.properties` file in `src/main/resources`.

Important: Configure the database connection details as follows (adjust to your PostgreSQL setup):

```properties
spring.application.name=crud
spring.datasource.url=jdbc:postgresql://localhost:5432/crud
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

- `spring.datasource.url`: URL to your PostgreSQL database.
- `spring.datasource.username` and `spring.datasource.password`: Your PostgreSQL credentials.
- `spring.jpa.hibernate.ddl-auto=update`: Automatically updates the schema based on entities.
- `spring.jpa.show-sql=true`: Logs SQL statements for debugging.

### CORS Configuration

In `BookController.java`, the `@CrossOrigin` annotation is critical for allowing frontend requests:

```java
@CrossOrigin("http://localhost:4200/")
```

This enables the Angular frontend (running on `http://localhost:4200/`) to communicate with the backend. Adjust the URL if your frontend runs on a different host/port.

---

## Database Structure

The database schema is managed by Spring Data JPA based on the `Book` entity:

```java
@Data
@NoArgsConstructor
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotNull
    private Integer pages;

    @NotNull
    private Double price;
}
```

- `id`: Auto-incremented primary key.
- `title`: Book title (required).
- `author`: Book author (required).
- `pages`: Number of pages (required).
- `price`: Book price (required).

Ensure your PostgreSQL database (`crud`) is created and accessible.

---

## Running the Application

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

The API will be available at:

```
http://localhost:8080/book
```

Note: Ensure PostgreSQL is running and the database credentials match your `application.properties`.

---

## API Endpoints

The `BookController` provides the following REST endpoints:

| Method  | Endpoint      | Description          | Request Body | Response       |
|---------|-------------|----------------------|--------------|---------------|
| GET     | `/book`      | Retrieve all books  | None         | List<Book>     |
| GET     | `/book/{id}` | Retrieve book by ID | None         | Book or 404    |
| POST    | `/book`      | Create a new book   | Book JSON    | Book or 400    |
| PUT     | `/book`      | Update a book       | Book JSON    | Book or 400    |
| DELETE  | `/book/{id}` | Delete book by ID   | None         | 200 or 404     |

### Example Book JSON:

```json
{
    "title": "Sample Book",
    "author": "John Doe",
    "pages": 300,
    "price": 29.99
}
```

---

## Technologies Used

### Backend:
- Spring Boot
- Spring Data JPA
- Hibernate

### Database:
- PostgreSQL

### Build Tool:
- Maven

