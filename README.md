invex-employee-management-web
Employee management REST API built with Spring Boot 3.3.13 and Java 21. Provides CRUD operations for employee records, backed by MySQL and documented via OpenAPI/Swagger.
---
Table of Contents
Prerequisites
Project Structure
Setup and Installation
Environment Variables
Running the Application
API Documentation
Code Quality
Tech Stack
---
Prerequisites
The following tools must be installed before proceeding:
Java 21 — Download
Maven 3.8+ — Download
Docker — Required to run the MySQL database via Docker Compose. Download
---
Project Structure
This project depends on a shared database library (`invex-test-database-lib`) that contains JPA entities, repositories, and DTOs. That library must be compiled and installed into your local Maven repository before building this project.
```
invex-project/
├── invex-test-database-lib/       # Shared library: entities, repos, DTOs
└── invex-employee-management-web/ # Main application: REST API, services, config
```
---
Setup and Installation
Follow these steps in order.
Step 1 — Install Docker and start it
Ensure Docker Desktop (or Docker Engine) is installed and running before continuing. The database runs inside a container and must be available before the application starts.
Step 2 — Build and install the shared library
The main project depends on `invex-test-database-lib`. Install it to your local Maven repository first:
```bash
cd invex-test-database-lib
mvn clean install
```
Step 3 — Create the `.env` file
Create a file named `.env` at the root of the `invex-employee-management-web` project (same directory as `docker-compose.yml`) with the following content:
> **Note:** This file was provided separately via email. The values below match what is expected by both Docker Compose and the Spring Boot application.
```env
MYSQL_ROOT_PASSWORD=xxx
MYSQL_DATABASE=xxx
MYSQL_USER=xxx
MYSQL_PASSWORD=xxx
MYSQL_PORT=3306
DB_URL=jdbc:mysql://xx:xx/xxx?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
DB_USERNAME=xx
DB_PASSWORD=xx
```
This file must not be committed to version control.
Step 4 — Configure environment variables in your IDE
The Spring Boot application reads the following environment variables at runtime. Add them to your IDE's run configuration (IntelliJ: Run > Edit Configurations > Environment variables):
```
API_KEY=api-key-xxxxx;DB_PASSWORD=xxxx;DB_URL=jdbc:mysql://xx:xx/xxxx?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC;DB_USERNAME=invex_user
```
These variables are required for the application to connect to the database and authenticate API requests. The application will fail to start if they are missing.
Step 5 — Start the database
From the root of the project (where `docker-compose.yml` is located), start MySQL using Docker Compose:
```bash
docker compose --env-file .env up -d
```
This starts a MySQL 8 container with the database, user, and password defined in your `.env` file. No further database configuration is required.
Step 6 — Build the application
```bash
cd invex-employee-management-web
mvn clean install
```
---
Running the Application
Once the database is running and environment variables are configured, start the application from your IDE or via Maven:
```bash
mvn spring-boot:run
```
The application starts on port `8081` by default.
---
API Documentation
Swagger UI is available at:
```
http://localhost:8081/swagger-ui/index.html
```
All endpoints require an API key passed via the `X-API-KEY` header (or as configured by the `ApiKeyAuth` security scheme in Swagger).
Available Endpoints
Method	Endpoint	Description
`GET`	`/api/v1/employees`	Retrieve all employees
`GET`	`/api/v1/employees/{id}`	Retrieve a single employee by ID
`POST`	`/api/v1/employees`	Create one or multiple employees
`PUT`	`/api/v1/employees/{id}`	Update an existing employee
`DELETE`	`/api/v1/employees/{id}`	Delete an employee by ID
`GET`	`/api/v1/employees/search?name=`	Search employees by name
Example Requests
Get all employees
```bash
curl -X GET http://localhost:8081/api/v1/employees \
```
Create employees
```bash
curl -X POST http://localhost:8081/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '[
    {
      "firstName": "Jane",
      "lastName": "Doe",
      "email": "jane.doe@example.com"
    }
  ]'
```
Update an employee
```bash
curl -X PUT http://localhost:8081/api/v1/employees/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com"
  }'
```
Delete an employee
```bash
curl -X DELETE http://localhost:8081/api/v1/employees/{id} \
```
---
Code Quality
This project uses Checkstyle to enforce consistent code formatting and style. The Checkstyle configuration is located at:
```
src/test/resources/config/checkstyle.xml
```
Checkstyle runs automatically during the `validate` phase of the Maven build. The build will fail if any violations are found. To check for violations manually:
```bash
mvn checkstyle:check
```
Standards to follow when contributing:
Google Java Style Guide conventions
Maximum line length and import ordering as defined in `checkstyle.xml`
No unused imports or variables
Test coverage must remain above 80% (enforced via JaCoCo)
---
Tech Stack
Component	Technology
Language	Java 21
Framework	Spring Boot 3.3.13
Persistence	Spring Data JPA, Hibernate, Spring Data Envers
Database	MySQL 8 (Docker)
Validation	Spring Validation (Jakarta)
API Documentation	SpringDoc OpenAPI 3 / Swagger UI
Monitoring	Spring Actuator, Micrometer, Prometheus
Code Quality	Checkstyle, JaCoCo
Utilities	Lombok, Jackson JSR310
---
Developer
Erick Antonio Reyes Montalvo
montalvoerickantonio@gmail.com
