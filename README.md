# Backend CRUD - Book Management API

This is an advanced **Book Management API** built using **Spring Boot** with **PostgreSQL** as the database. It provides enhanced RESTful endpoints for managing books, including image upload functionality via **Cloudinary** and improved logging features.

This version includes:
- **Cloudinary Integration**: Securely store and manage book cover images.
- **Global Error Management**: Ensure consistent error responses across all endpoints.
- **Comprehensive Unit Tests**: Ensuring robust code coverage.
- **Enhanced Logging**: Configurable logging levels and improved monitoring.
- **Pagination**: Allows for efficient data retrieval and display by breaking large datasets into smaller, manageable chunks, reducing server load and improving user experience.

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
- [Error Management](#error-management)
- [Unit Testing](#unit-testing)
- [Technologies Used](#technologies-used)

---

## Overview

This backend API provides endpoints for managing books, including:
- Retrieving a list of books.
- Fetching a specific book by ID.
- Creating new books with optional images.
- Updating book details and images.
- Deleting books and associated images.

It integrates with **Cloudinary** for image storage and has robust logging capabilities for monitoring application behavior.

---

## Features

- **RESTful Endpoints**: CRUD operations exposed via standard HTTP methods (GET, POST, PUT, DELETE).
- **Cloudinary Image Uploads**: Supports storing and deleting book images.
- **Enhanced Logging**: Configurable logging levels for better debugging and monitoring.
- **Global Exception Handler**: Ensure a descriptive response of the error.
- **Comprehensive Unit Tests**: Includes multiple unit tests covering services and controllers.
- **Database Persistence**: Uses Spring Data JPA with Hibernate for seamless database interaction.
- **CORS Support**: Configured to allow requests from the frontend.

---

## Project Structure

```
crud-basic-backend
│
├── src
│   ├── main/java/com/example/crud
│   │   ├── controllers    # REST controllers
│   │   ├── entities       # JPA entity definitions
│   │   ├── repositories   # JPA repository interfaces
│   │   ├── services       # Business logic implementations
│   │   └── config         # Configuration files
│   ├── resources
│   │   ├── application.properties # Configuration (excluded from version control)
│   │   ├── logback-spring.xml     # Logging configuration
└── test    # Unit tests
```

---

## Getting Started

### Prerequisites

Ensure you have the following installed:
- **Java Development Kit (JDK) v17+**
- **Maven** (for building the project)
- **PostgreSQL** (for database management)
- **Cloudinary Account** (for image hosting)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/pablodiazjorge/crud-basic-backend/tree/advanced
   ```
2. Navigate to the project directory:
   ```bash
   cd crud-basic-backend
   ```
3. Install dependencies and build the project:
   ```bash
   mvn clean install
   ```

---

## Configuration

### Database and Cloudinary Configuration

The `application.properties` file is **excluded from version control** for security reasons. It contains sensitive information such as database credentials and API keys. **Ensure you create this file in `src/main/resources/` before running the application.**

#### Example Configuration (Replace with actual values):
```properties
# Application
spring.application.name=crud

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Cloudinary Configuration
cloudinary.cloud_name=your_cloud_name
cloudinary.api_key=your_api_key
cloudinary.api_secret=your_api_secret

# Logging Configuration
logging.config=classpath:logback-spring.xml
logging.file.path=logs
spring.profiles.active=dev
logging.level.root=INFO

# Limit verbose HTTP client logs (Optional)
logging.level.org.apache.hc.client5.http=INFO
logging.level.org.apache.http=INFO

# Disable Hibernate SQL and Binding Logs (Optional)
logging.level.org.hibernate.SQL=OFF
logging.level.org.hibernate.type.descriptor.sql=OFF

# Disable Hibernate verbose logs (Optional)
logging.level.org.hibernate.tool.hbm2ddl=OFF
logging.level.org.hibernate.hql.internal.ast.AST=OFF
```

---

## Database Structure

The `Book` entity is structured as follows:
```java
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

    private String imageUrl;
}
```
Ensure your PostgreSQL database is set up before running the application.

---

## Running the Application

Start the Spring Boot application:
```bash
mvn spring-boot:run
```

API will be available at:
```
http://localhost:8080/book
```
Ensure PostgreSQL is running and configured correctly.

---

## API Endpoints

| Method | Endpoint     | Description         |
|--------|--------------|---------------------|
| GET    | `/book`      | Retrieve all books  |
| GET    | `/book/{id}` | Retrieve book by ID |
| POST   | `/book`      | Create a new book   |
| PUT    | `/book`      | Update a book       |
| DELETE | `/book/{id}` | Delete book by ID   |

---

## Error Management

This application includes a **global exception handler** to ensure consistent error responses across all endpoints. The global error handler is implemented using `@ControllerAdvice` and `@ExceptionHandler` annotations.

### Example Global Exception Handler:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request parameters");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}
```

### How It Works:
- **404 Not Found**: When an entity (e.g., a book) is not found, it returns a `404 Not Found` response.
- **400 Bad Request**: When validation fails (e.g., missing required fields), it returns a `400 Bad Request` response.
- **500 Internal Server Error**: If an unexpected error occurs, it returns a `500 Internal Server Error` response.

This ensures that all API responses are structured and informative, making debugging and client-side handling easier.

---

## Unit Testing

This project includes a robust suite of **unit tests** to ensure stability and reliability.

### **Test Coverage**

#### `CrudApplicationTests`
- `contextLoads`

#### `ImageServiceImplTest`
- `uploadImage_success`
- `uploadImage_cloudinaryFailure`
- `deleteImage_success`
- `deleteImage_cloudinaryFailure`

#### `CloudinaryServiceImplTest`
- `upload_success`
- `upload_emptyFile_throwsException`
- `upload_nullFile_throwsException`
- `upload_cloudinaryFailure_throwsException`
- `delete_success`
- `delete_cloudinaryFailure_throwsException`
- `delete_nullPublicId_proceeds`
- `delete_emptyPublicId_proceeds`

#### `BookServiceImplTest`
- `saveBook_success_noImage`
- `saveBook_success_withImage`
- `updateBook_success`
- `updateBookImage_success_noPreviousImage`
- `updateBookImage_success_withPreviousImage`
- `getBooks_success`
- `getBookById_success`
- `getBookById_notFound`
- `deleteBook_success_withImage`
- `deleteBook_success_noImage`

#### `BookControllerTest`
- `saveBook_success`
- `updateBookImage_success`
- `updateBookImage_notFound`
- `updateBook_success`
- `updateBook_invalidData`
- `getAllBooks_success`
- `getBookById_success`
- `getBookById_notFound`
- `deleteBook_success`
- `deleteBook_notFound`

To run tests:
```bash
mvn test
```

---

## Technologies Used

### Backend:
- Spring Boot
- Spring Data JPA
- Hibernate
- Cloudinary API

### Database:
- PostgreSQL

### Build Tool:
- Maven
