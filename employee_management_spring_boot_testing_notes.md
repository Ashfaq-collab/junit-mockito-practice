# Employee Management REST API — Learning Notes

## 1. Project Overview

We built a small Employee Management REST API using Java and Spring Boot. It supports CRUD operations and demonstrates service-layer unit testing with JUnit 5 and Mockito, plus controller testing with MockMvc.

### Technologies
- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 in-memory database
- Jakarta Bean Validation
- JUnit 5
- Mockito
- MockMvc
- Maven

## 2. Application Architecture

```text
Client (Postman / React)
        |
        v
Controller  -- receives HTTP requests and returns HTTP responses
        |
        v
Service     -- contains business logic
        |
        v
Repository  -- performs database operations through Spring Data JPA
        |
        v
H2 Database
```

- **Controller:** Handles HTTP requests and responses.
- **Service:** Holds application/business logic.
- **Repository:** Provides data-access methods.
- **Entity:** Represents data stored in the database.
- **Exception handler:** Converts selected application exceptions into suitable HTTP responses.

Keeping responsibilities separate makes code easier to maintain and test.

## 3. Employee Entity

`Employee.java` represents an employee. Typical fields are `id`, `name`, `email`, and `department`.

| Annotation | Meaning |
|---|---|
| `@Entity` | Marks the class as a JPA entity. |
| `@Id` | Marks the primary key. |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Lets the database generate the ID. |

A no-argument constructor is required by JPA. Getters and setters allow fields to be read and updated.

### Validation annotations

```java
@NotBlank(message = "Name is required")
private String name;

@NotBlank(message = "Email is required")
@Email(message = "Email must be valid")
private String email;

@NotBlank(message = "Department is required")
private String department;
```

- `@NotBlank` rejects `null`, empty, and whitespace-only strings.
- `@Email` checks whether a value has an email-like format.
- Validation constraints run on incoming request bodies when the controller parameter is annotated with `@Valid`.
- The project needs the `spring-boot-starter-validation` dependency.

## 4. Repository

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
```

Spring Data JPA provides an implementation for this interface.

| Method | Purpose |
|---|---|
| `save(employee)` | Saves a new or existing entity. |
| `findAll()` | Returns all employees. |
| `findById(id)` | Returns an `Optional<Employee>`. |
| `delete(employee)` | Deletes the supplied entity. |
| `deleteById(id)` | Deletes by primary key. |
| `existsById(id)` | Checks whether an ID exists. |

The type parameters `Employee, Long` mean the repository manages `Employee` entities whose IDs are `Long`.

## 5. Service Layer

`EmployeeService` contains the CRUD operations and calls `EmployeeRepository`.

The service uses constructor injection:

```java
private final EmployeeRepository employeeRepository;

public EmployeeService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
}
```

Constructor injection makes dependencies explicit and helps unit tests supply a mock repository.

Typical service methods:
- `createEmployee(employee)` → calls `save`.
- `getAllEmployees()` → calls `findAll`.
- `getEmployeeById(id)` → calls `findById`.
- `updateEmployee(id, updatedEmployee)` → finds the existing employee, updates its fields, and saves it.
- `deleteEmployee(id)` → finds the employee, then deletes it.

### `Optional` and missing employees

```java
return employeeRepository.findById(id)
    .orElseThrow(() ->
        new EmployeeNotFoundException(
            "Employee not found with id: " + id
        )
    );
```

If the employee exists, it is returned. If not, the service throws a custom exception.

## 6. REST Controller

| HTTP method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/employees` | Create an employee |
| `GET` | `/api/employees` | Retrieve all employees |
| `GET` | `/api/employees/{id}` | Retrieve one employee |
| `PUT` | `/api/employees/{id}` | Update an employee |
| `DELETE` | `/api/employees/{id}` | Delete an employee |

Important annotations:

| Annotation | Meaning |
|---|---|
| `@RestController` | Marks the class as a REST controller. |
| `@RequestMapping("/api/employees")` | Defines the base path. |
| `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` | Map HTTP methods to Java methods. |
| `@RequestBody` | Deserializes request JSON into a Java object. |
| `@PathVariable` | Reads a value from the URL. |
| `@ResponseStatus(HttpStatus.CREATED)` | Returns HTTP 201 for successful creation. |
| `@ResponseStatus(HttpStatus.NO_CONTENT)` | Returns HTTP 204 for successful deletion. |
| `@Valid` | Triggers validation of the request object. |

Example create request:

```http
POST /api/employees
Content-Type: application/json
```

```json
{
  "name": "Rahul",
  "email": "rahul@example.com",
  "department": "IT"
}
```

A successful create returns `201 Created`.

## 7. Exception Handling

We created `EmployeeNotFoundException`, which extends `RuntimeException`, and a `GlobalExceptionHandler` annotated with `@RestControllerAdvice`.

```java
@ExceptionHandler(EmployeeNotFoundException.class)
public ResponseEntity<String> handleEmployeeNotFound(
        EmployeeNotFoundException exception) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(exception.getMessage());
}
```

This maps a missing employee to HTTP `404 Not Found`.

Useful HTTP status codes:
- `200 OK` — request succeeded.
- `201 Created` — resource created.
- `204 No Content` — request succeeded with no response body.
- `400 Bad Request` — invalid request, such as failed validation.
- `404 Not Found` — requested resource does not exist.
- `500 Internal Server Error` — unexpected server-side error.

## 8. H2 Database Configuration

Example `application.properties`:

```properties
spring.application.name=employee-management
spring.datasource.url=jdbc:h2:mem:employeedb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

- `jdbc:h2:mem:employeedb` creates an in-memory database.
- `ddl-auto=update` lets Hibernate update the schema during development.
- `show-sql=true` prints SQL statements.
- The H2 console can be opened at `http://localhost:8080/h2-console` while the application is running. Use JDBC URL `jdbc:h2:mem:employeedb` and username `sa`.
- In-memory data is lost when the application stops. This setup is suitable for practice, not production.

## 9. JUnit 5 and Mockito

| Concept | Meaning |
|---|---|
| JUnit `@Test` | Marks a test method. |
| `assertEquals(expected, actual)` | Checks that expected and actual values are equal. |
| `assertThrows(...)` | Checks that an operation throws the expected exception. |
| Mockito `@Mock` | Creates a mock dependency. |
| Mockito `@InjectMocks` | Creates the class under test and injects mocks into it. |
| `@ExtendWith(MockitoExtension.class)` | Enables Mockito's JUnit 5 integration. |
| `when(...).thenReturn(...)` | Configures a mock's response. |
| `verify(...)` | Checks that a mock method was called. |
| `never()` | Checks that a mock method was not called. |
| `any(Employee.class)` | Matches an `Employee` argument. |
| `thenAnswer(...)` | Calculates a mock response dynamically from the invocation. |

### Arrange–Act–Assert (AAA)

1. **Arrange:** Prepare test data and configure mock behavior.
2. **Act:** Call the method being tested.
3. **Assert:** Check the result and relevant interactions.

### Example service unit test

```java
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getEmployeeById_shouldReturnEmployee_whenEmployeeExists() {
        Employee employee = new Employee(
            1L, "Rahul", "rahul@example.com", "IT"
        );

        when(employeeRepository.findById(1L))
            .thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Rahul", result.getName());
        verify(employeeRepository).findById(1L);
    }
}
```

The service is real, but the repository is a mock. This test does not need a database.

### Service tests covered

1. Finding an employee by ID.
2. Creating an employee.
3. Listing employees.
4. Updating an employee.
5. Deleting an employee.
6. Throwing the expected custom exception when an employee is missing.

## 10. Controller Tests with MockMvc

Service unit tests call Java methods directly. Controller tests exercise the MVC request/response pipeline.

- `@WebMvcTest(EmployeeController.class)` loads the MVC slice.
- `MockMvc` simulates HTTP requests without starting a real web server.
- `@MockitoBean` supplies a mock service in supported Spring versions; older projects may use `@MockBean`.
- `jsonPath("$.name").value("Rahul")` checks a field in the JSON response.
- `status().isOk()` checks HTTP 200.
- `status().isCreated()` checks HTTP 201.
- `status().isBadRequest()` checks HTTP 400.

Example:

```java
mockMvc.perform(get("/api/employees/1"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.name").value("Rahul"));
```

Controller tests covered:
1. GET an employee by ID.
2. POST a valid employee.
3. POST invalid data and check for HTTP 400.
4. Verify that the service is not called when validation rejects a request.

Use imports that match your Spring Boot version. Spring Boot 3 and Spring Boot 4 have differences in some testing package names.

## 11. Running the Project and Tests

Windows:

```bat
mvnw.cmd spring-boot:run
mvnw.cmd test
```

macOS/Linux:

```bash
./mvnw spring-boot:run
./mvnw test
```

A successful Maven test run ends with `BUILD SUCCESS`.

## 12. Common Testing Mistakes

- Forgetting `@ExtendWith(MockitoExtension.class)` in a Mockito-only JUnit 5 test.
- Forgetting to stub a mock when the test expects a specific result.
- Using `@Mock` where the class under test needs a real instance.
- Confusing `verify()` (checks an interaction) with `assertEquals()` (checks a result).
- Expecting a service unit test to verify HTTP status codes; HTTP behavior belongs in controller tests.
- Forgetting `@Valid` on controller request-body parameters.
- Forgetting the validation starter dependency.
- Expecting H2 in-memory data to remain after restarting the application.
- Leaving imports that do not match the project's Spring Boot version.
- Forgetting to rerun all tests after changing exception classes or service behavior.

## 13. Interview Revision Questions

1. What is the difference between unit testing and integration testing?
2. Why do we mock the repository when unit-testing the service?
3. What is the difference between `@Mock` and `@InjectMocks`?
4. What do `when(...).thenReturn(...)` and `verify(...)` do?
5. What is the Arrange–Act–Assert pattern?
6. What does `Optional` represent, and why is `orElseThrow()` useful?
7. What is the difference between HTTP 400 and HTTP 404?
8. What is the purpose of `@RestControllerAdvice`?
9. What does `@Valid` do?
10. What is the difference between testing a service and testing a controller with MockMvc?
11. Why is an H2 in-memory database useful in a practice project?
12. Why should tests be run after code changes?

## 14. Suggested Next Steps

To improve the project further:
- Add consistent JSON error responses for validation failures.
- Add tests for all controller endpoints, including update and delete.
- Add tests for validation of invalid email and blank department.
- Consider integration tests using `@SpringBootTest`.
- Add pagination and sorting.
- Add a persistent database such as PostgreSQL when ready.
- Review the GitHub README and keep the repository up to date.

---

**Main takeaway:** JUnit verifies outcomes, Mockito isolates dependencies and verifies interactions, and MockMvc tests the HTTP layer. Together, these tools help you test a Spring Boot application at different layers.
