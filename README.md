# Book CRUD App with JWT Authentication and Rate Limiting

## Overview

This project is a Spring Boot application that provides a REST API for managing books. It features:

- **CRUD Operations:** Create, read, update, and delete book entries (stored in-memory for demonstration).
- **JWT Authentication:** Secure endpoints using JSON Web Tokens.
- **Rate Limiting:** Control the number of requests per minute per client using Bucket4j.
- **Containerization:** Docker support for easy container builds.
- **Deployment Guidance:** Steps to deploy on AWS ECS Fargate.

## Endpoints and Examples

### 1. Authentication Endpoints

#### POST `/auth/login`
Authenticates a user and returns a JWT token.

- **Request Headers:**
    - `Content-Type: application/json`
- **Request Body Example:**
  ```json
  {
    "username": "john",
    "password": "password123"
  }
  ```
- **Response Example (Success - HTTP 200):**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwiaWF0IjoxNjg1NTYzMzQ2LCJleHAiOjE2ODU1NjY5NDZ9.abc123XYZ"
  }
  ```
- **Response Example (Failure - HTTP 401):**
  ```
  Invalid credentials
  ```

### 2. Book Endpoints (JWT Protected & Rate Limited)

**Note:** All `/books` endpoints require the `Authorization` header with a valid JWT token:
```
Authorization: Bearer <JWT_TOKEN>
```

#### GET `/books`
Retrieves a list of all books.

- **Example Request:**
  ```bash
  curl -X GET -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/books
  ```
- **Example Response:**
  ```json
  [
    {
      "id": 1,
      "title": "Book Title",
      "author": "Author Name",
      "content": "Content of the book..."
    },
    {
      "id": 2,
      "title": "Another Book",
      "author": "Another Author",
      "content": "Some more content..."
    }
  ]
  ```

#### POST `/books`
Creates a new book.

- **Request Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <JWT_TOKEN>`
- **Request Body Example:**
  ```json
  {
    "title": "New Book Title",
    "author": "New Author",
    "content": "This is the content of the new book."
  }
  ```
- **Example Request using curl:**
  ```bash
  curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{"title":"New Book Title","author":"New Author","content":"This is the content of the new book."}' \
  http://localhost:8080/books
  ```
- **Example Response (HTTP 201):**
  ```json
  {
    "id": 3,
    "title": "New Book Title",
    "author": "New Author",
    "content": "This is the content of the new book."
  }
  ```

#### GET `/books/{id}`
Retrieves a specific book by its ID.

- **Example Request:**
  ```bash
  curl -X GET -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/books/3
  ```
- **Example Response (HTTP 200):**
  ```json
  {
    "id": 3,
    "title": "New Book Title",
    "author": "New Author",
    "content": "This is the content of the new book."
  }
  ```
- **Example Response (If Book Not Found - HTTP 404):**
  ```
  Book not found
  ```

#### PUT `/books/{id}`
Updates an existing book.

- **Request Headers:**
    - `Content-Type: application/json`
    - `Authorization: Bearer <JWT_TOKEN>`
- **Request Body Example:**
  ```json
  {
    "title": "Updated Book Title",
    "author": "Updated Author",
    "content": "Updated content of the book."
  }
  ```
- **Example Request using curl:**
  ```bash
  curl -X PUT -H "Content-Type: application/json" -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{"title":"Updated Book Title","author":"Updated Author","content":"Updated content of the book."}' \
  http://localhost:8080/books/3
  ```
- **Example Response (HTTP 200):**
  ```json
  {
    "id": 3,
    "title": "Updated Book Title",
    "author": "Updated Author",
    "content": "Updated content of the book."
  }
  ```

#### DELETE `/books/{id}`
Deletes a book by its ID.

- **Example Request:**
  ```bash
  curl -X DELETE -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/books/3
  ```
- **Example Response (HTTP 200):**
  ```
  Book deleted
  ```
- **Example Response (If Book Not Found - HTTP 404):**
  ```
  Book not found
  ```

### Rate Limiting Behavior

- The application is configured to allow up to 5 requests per 30 seconds per client (determined by IP or authenticated username).
- If the limit is exceeded, the response will be:
    - **HTTP Status:** 429 Too Many Requests
    - **Response Body:**
      ```
      Rate limit exceeded. Try again in X seconds.
      ```

## Getting Started

### Prerequisites

- **Java:** JDK 17 or later.
- **Maven:** For building the project.
- **Docker:** Optional, for containerization.
- **IDE:** Any Java IDE such as IntelliJ IDEA or Eclipse.

### Build and Run Locally

1. **Clone the Repository**
   ```bash
   git clone https://github.com/<your-username>/book-crud-app.git
   cd book-crud-app
   ```

2. **Build the Application**
   ```bash
   mvn clean package
   ```

3. **Run the Application**
   ```bash
   java -jar target/book-crud-app-0.0.1-SNAPSHOT.jar
   ```

### Testing with Postman

1. **Authentication Request:**
    - Set method to **POST**.
    - URL: `http://localhost:8080/auth/login`
    - Headers:
        - `Content-Type: application/json`
    - Body (raw, JSON):
      ```json
      {
        "username": "john",
        "password": "password123"
      }
      ```
    - Click **Send** to obtain a JWT token.

2. **Accessing Protected Endpoints:**
    - For example, to GET all books:
        - Set method to **GET**.
        - URL: `http://localhost:8080/books`
        - Add a header:
            - `Authorization: Bearer <JWT_TOKEN>`
        - Click **Send** to retrieve the list of books.

## Containerization

### Build the Docker Image

1. **Create a Dockerfile** (if not already created):

   ```dockerfile
   FROM openjdk:17-slim
   WORKDIR /app
   COPY target/book-crud-app-0.0.1-SNAPSHOT.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. **Build the Image:**
   ```bash
   docker build -t book-crud-app .
   ```

3. **Run the Container:**
   ```bash
   docker run -p 8080:8080 book-crud-app
   ```

## Deployment to AWS ECS Fargate

- **Push the Docker Image to ECR:** Follow AWS documentation to push your Docker image to the Elastic Container Registry.
- **Create an ECS Cluster:** Create a Fargate cluster.
- **Deploy Your Service:** Create a task definition and deploy your service on ECS Fargate.
- **Configure Networking:** Ensure your service has a public IP or is behind an ALB with proper security group rules.

## Future Improvements

- **Persistent Storage:** Migrate from in-memory storage to a database (e.g., MySQL, PostgreSQL).
- **Secrets Management:** Retrieve secret keys (like the JWT secret) from AWS KMS or AWS Secrets Manager instead of hardcoding.
- **User Management:** Replace hardcoded credentials with a user management system backed by a database.
- **Enhanced Logging & Monitoring:** Integrate with centralized logging (ELK stack) and monitoring solutions (Prometheus, Grafana).
- **API Documentation:** Integrate Swagger/OpenAPI for interactive API documentation.
- **Advanced Rate Limiting:** Use a distributed store like Redis to share rate limiting counters across multiple instances.

## License

This project is for demonstration purposes. Further enhancements are recommended before using it in a production environment.
