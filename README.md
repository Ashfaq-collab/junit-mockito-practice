# Employee Management REST API

A RESTful Employee Management API built with Java and Spring Boot. This project demonstrates CRUD operations, input validation, exception handling, and automated testing using JUnit 5 and Mockito.

## Technologies Used

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 In-Memory Database
- JUnit 5
- Mockito
- MockMvc
- Maven

## Features

- Create an employee
- Retrieve all employees
- Retrieve an employee by ID
- Update employee details
- Delete an employee
- Validate employee names, emails, and departments
- Handle missing employees with HTTP 404
- Test service logic using JUnit and Mockito
- Test REST endpoints using MockMvc

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/employees` | Create an employee |
| GET | `/api/employees` | Retrieve all employees |
| GET | `/api/employees/{id}` | Retrieve an employee |
| PUT | `/api/employees/{id}` | Update an employee |
| DELETE | `/api/employees/{id}` | Delete an employee |

## Example Request

Create an employee:

`POST /api/employees`

```json
{
  "name": "Rahul",
  "email": "rahul@example.com",
  "department": "IT"
}
```

## Run the Project

1. Clone this repository.
2. Open the project in IntelliJ IDEA or another Java IDE.
3. Make sure the required Java version is installed.
4. Run the application using the IDE or Maven:

```bash
./mvnw spring-boot:run
```

On Windows, use:

```bash
mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080`.

## Run Tests

Run all automated tests with:

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```

The tests cover service-layer business logic and REST Controller behavior.

## Database

This project uses an H2 in-memory database for development and practice. Data is reset when the application stops.

H2 Console: `http://localhost:8080/h2-console`

## Learning Goals

This project was created to practise Java, Spring Boot REST APIs, dependency injection, JPA, exception handling, JUnit 5, Mockito, and automated testing.

## Author

Java Developer | Spring Boot | JUnit | Mockito
