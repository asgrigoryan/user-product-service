# User Order Service

## Overview
User Order Service is a reactive Spring Boot application that provides APIs for managing users, their orders, and associated products. It demonstrates best practices in reactive programming, error handling, logging, and service composition using WebFlux and Project Reactor.

## Features
- Fetch user details by ID
- Retrieve orders for a user by phone number
- Fetch products for each order in parallel, with robust error handling
- Aggregate and select the product with the highest score
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
   git clone https://github.com/<your-org>/user-order-service.git
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

## API Endpoints
- `GET /userOrderService/orders/user/{userId}`: Get the product with the highest score for a user
- `GET /userOrderService/orders/phone/{phoneNumber}`: Get orders by phone number

## Error Handling
- All external service calls are wrapped with robust error handling. If a product service call fails, an empty list is returned for that code, and the process continues for other codes.
- If no products are found, a 404 or appropriate error is returned.

## Logging
- All major operations are logged, including request start, per-code fetch, errors, and completion.
- MDC context propagation is supported for distributed tracing.

## Extending the Service
- Add new clients in the `client/` package for additional third-party integrations.
- Add new endpoints in the `controller/` package as needed.

## Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes
4. Push to your fork and open a pull request

## License
MIT

---

## GitHub Repository Description

> Reactive Spring Boot service for managing users, orders, and products. Features parallel product fetching, robust error handling, and comprehensive logging. Built with WebFlux, Project Reactor, and Docker support.

