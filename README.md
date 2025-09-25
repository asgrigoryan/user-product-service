# User Order Service

## Overview
User Order Service is a reactive Spring Boot application that provides APIs for managing users, their orders, and associated products. It demonstrates best practices in reactive programming, parallel data fetching, error handling, and comprehensive logging using Spring WebFlux and Project Reactor.

## Features
- Fetch user details by ID
- Retrieve orders for a user by phone number
- For each order, fetch products in parallel (non-blocking)
- Aggregate and select the product with the highest score
- Robust error handling: if a product fetch fails, it is handled gracefully
- Comprehensive logging and MDC context propagation
- Extensible client architecture for third-party service integration

## Technologies
- Java 17+
- Spring Boot 3+
- Spring WebFlux (Project Reactor)
- Lombok
- SLF4J Logging
- Gradle
- Docker support

## Getting Started

### Prerequisites
- Java 17 or higher
- Gradle
- Docker (optional, for containerization)

### Build & Run
1. **Clone the repository:**
   ```bash
   git clone https://github.com/asgrigoryan/user-product-service.git
   cd user-order-service
   ```
2. **Build the project:**
   ```bash
   ./gradlew build
   ```
3. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```
4. **Run tests:**
   ```bash
   ./gradlew test
   ```

### Docker
To build and run the service in Docker:
```bash
docker build -t user-order-service .
docker run -p 8080:8080 user-order-service
```

## Logging
The service uses SLF4J for logging. Comprehensive logs are available for all major operations, including order and product fetching and error handling.

## Error Handling
- All product fetch operations are performed in parallel.
- If a product API call fails for a given order, it is handled gracefully and does not affect other operations.
