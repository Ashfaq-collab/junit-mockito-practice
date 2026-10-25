# JUnit and Mockito Notes for Java/Spring Boot Freshers

These notes summarize what we have learned so far, with simple
explanations and practical examples.

## 1. What is software testing?

Software testing checks whether code behaves as expected.

For example, if `add(2, 3)` should return `5`, a test calls the method
and checks that the result is `5`.

Automated tests are useful because they can be run repeatedly whenever
code changes.

## 2. What is JUnit?

**JUnit** is a Java testing framework. It runs tests and provides
assertions to check results.

We are using **JUnit 5 (Jupiter)**.

-   **JUnit:** Runs tests and checks expected outcomes.
-   **Mockito:** Creates mock objects for dependencies, such as
    repositories, so we can test a class without needing the real
    dependency.

## 3. Unit tests

A **unit test** checks a small piece of code, usually a method or class,
in isolation.

Example production class:

``` java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```

Example test:

``` java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {

    @Test
    void shouldAddTwoNumbers() {
        Calculator calculator = new Calculator();

        int result = calculator.add(2, 3);

        assertEquals(5, result);
    }
}
```

The test calls the real `Calculator` method and checks its result.

## 4. Arrange -- Act -- Assert (AAA)

A simple structure for most tests:

1.  **Arrange:** Prepare the object and test data.
2.  **Act:** Call the method being tested.
3.  **Assert:** Check the result.

``` java
@Test
void shouldAddTwoNumbers() {
    // Arrange
    Calculator calculator = new Calculator();

    // Act
    int result = calculator.add(2, 3);

    // Assert
    assertEquals(5, result);
}
```

## 5. Important JUnit annotations

### `@Test`

Marks a method as a test method.

``` java
@Test
void shouldReturnExpectedResult() {
    assertEquals(4, 2 + 2);
}
```

### `@BeforeEach`

Runs before **each** test method. Use it to prepare objects or test
data.

``` java
private Calculator calculator;

@BeforeEach
void setUp() {
    calculator = new Calculator();
}
```

If three test methods run, `@BeforeEach` runs three times.

### `@AfterEach`

Runs after each test method. Use it for cleanup when needed. Not every
simple unit test needs it.

### `@BeforeAll` and `@AfterAll`

-   `@BeforeAll` runs once before the test methods in a class.
-   `@AfterAll` runs once after the test methods in a class.

With JUnit 5's default test-instance lifecycle, these methods are
normally `static`.

## 6. Common JUnit assertions

Import them with:

``` java
import static org.junit.jupiter.api.Assertions.*;
```

  ---------------------------------------------------------------------------------------------
  Assertion                                What it checks          Example
  ---------------------------------------- ----------------------- ----------------------------
  `assertEquals(expected, actual)`         Values are equal        `assertEquals(5, result);`

  `assertTrue(condition)`                  Condition is true       `assertTrue(age >= 18);`

  `assertFalse(condition)`                 Condition is false      `assertFalse(age < 18);`

  `assertNull(value)`                      Value is `null`         `assertNull(email);`

  `assertNotNull(value)`                   Value is not `null`     `assertNotNull(user);`

  `assertThrows(Type.class, executable)`   Code throws the         See example below
                                           expected exception type 
  ---------------------------------------------------------------------------------------------

Example for checking an exception:

``` java
IllegalArgumentException exception = assertThrows(
    IllegalArgumentException.class,
    () -> service.calculateAge(-1)
);

assertEquals("Age cannot be negative", exception.getMessage());
```

`assertThrows()` returns the exception that was thrown, so its message
can also be checked.

For floating-point values, use a tolerance when appropriate:

``` java
assertEquals(0.3, result, 0.001);
```

The third argument is the allowed difference (delta).

## 7. Testing multiple scenarios

Don't test only the happy path. Think about:

-   **Happy path:** Typical valid input.
-   **Boundary case:** Input at a condition's edge, such as exactly age
    `18`.
-   **Invalid input:** Input that should be rejected, such as a negative
    amount.
-   **Missing data:** For example, a user ID that isn't found.

Example service:

``` java
public boolean isEligible(int age) {
    return age >= 18;
}
```

Useful cases:

-   `isEligible(20)` should be `true`.
-   `isEligible(16)` should be `false`.
-   `isEligible(18)` should be `true` (the boundary).

## 8. Parameterized tests

Parameterized tests run the same test logic with several sets of inputs.

They reduce repetitive test methods.

### `@ParameterizedTest` with `@ValueSource`

Use `@ValueSource` when each invocation needs one supplied value.

``` java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ParameterizedTest
@ValueSource(ints = {1, 5, 10, 100})
void shouldIdentifyPositiveNumbers(int number) {
    assertTrue(number > 0);
}
```

JUnit runs this test once for each supplied value.

### `@ParameterizedTest` with `@CsvSource`

Use `@CsvSource` when each test invocation needs multiple related
values.

``` java
@ParameterizedTest
@CsvSource({
    "2, 3, 5",
    "10, 20, 30",
    "-5, 3, -2",
    "0, 0, 0"
})
void shouldAddNumbers(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}
```

Each row provides `a`, `b`, and the expected sum.

## 9. Set up JUnit in a Spring Boot Maven project

Create the project at [Spring Initializr](https://start.spring.io/) or
use an existing Spring Boot project.

For a Maven project, the following dependency is usually enough for
JUnit, Mockito, and Spring testing utilities:

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Place files in the standard folders:

``` text
src/
  main/java/.../CalculatorService.java
  test/java/.../CalculatorServiceTest.java
```

Run tests from the project root:

``` bash
mvn test
```

On Windows, if the Maven wrapper is present:

``` powershell
.\mvnw.cmd test
```

You can also run a test class using the green run icon in IntelliJ IDEA.
If test-run icons are missing, check that Maven has been reloaded, the
test dependency exists, and `src/test/java` is marked as a test source
root.

## 10. What is Mockito?

A service often depends on another class. For example:

``` text
UserService -> UserRepository -> Database
```

When unit-testing `UserService`, we usually want to test its logic
without connecting to a real database.

**Mockito creates a mock object** that stands in for a dependency. We
can tell it what to return and check whether methods were called.

## 11. Example: User, repository, and service

A simple `User` class:

``` java
public class User {
    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
```

A repository placeholder for this learning example:

``` java
import java.util.Optional;

public class UserRepository {
    public Optional<User> findById(int id) {
        // In a real application, this would fetch data.
        return Optional.empty();
    }

    public User save(User user) {
        // Placeholder for this example.
        return user;
    }
}
```

A service that depends on the repository:

``` java
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUserName(int id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return user.get().getName().toUpperCase();
        }

        return "User not found";
    }

    public User createUser(String name) {
        User user = new User(0, name);
        return userRepository.save(user);
    }
}
```

These repository methods are simplified placeholders, not a real Spring
Data JPA implementation.

## 12. Important Mockito annotations

### `@Mock`

Creates a mock dependency.

``` java
@Mock
private UserRepository userRepository;
```

### `@InjectMocks`

Creates the class under test and injects available mocks into it.

``` java
@InjectMocks
private UserService userService;
```

### Enable Mockito's JUnit extension

Use this on the test class:

``` java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    // tests
}
```

Relevant imports:

``` java
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
```

## 13. `when(...).thenReturn(...)` --- define a mock's response

Example:

``` java
when(userRepository.findById(1))
    .thenReturn(Optional.of(new User(1, "Rahul")));
```

This tells Mockito: when the mock's `findById(1)` method is called,
return an `Optional` containing the specified user.

For a missing user:

``` java
when(userRepository.findById(99))
    .thenReturn(Optional.empty());
```

This avoids using a real database in the unit test.

## 14. A complete Mockito test

``` java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserName() {
        User user = new User(1, "Rahul");

        when(userRepository.findById(1))
            .thenReturn(Optional.of(user));

        String result = userService.getUserName(1);

        assertEquals("RAHUL", result);
        verify(userRepository).findById(1);
    }

    @Test
    void shouldReturnUserNotFound() {
        when(userRepository.findById(99))
            .thenReturn(Optional.empty());

        String result = userService.getUserName(99);

        assertEquals("User not found", result);
    }
}
```

Remember: the expected result depends on the current implementation of
`getUserName()`. If you later change the method to throw an exception
when a user is missing, update the test to match that new behavior.

## 15. `verify()` --- check whether a mock method was called

Example:

``` java
verify(userRepository).findById(1);
```

By default, this verifies that `findById(1)` was called once on the
mock.

Use it when the interaction itself matters, not just the returned value.

Difference:

-   `assertEquals()` checks a result.
-   `verify()` checks a call to a mock.

## 16. Testing a save operation

A simple service method:

``` java
public User createUser(String name) {
    User user = new User(0, name);
    return userRepository.save(user);
}
```

Test it using Mockito:

``` java
import static org.mockito.ArgumentMatchers.any;

@Test
void shouldSaveNewUser() {
    User savedUser = new User(0, "Rahul");

    when(userRepository.save(any(User.class)))
        .thenReturn(savedUser);

    User result = userService.createUser("Rahul");

    assertEquals("Rahul", result.getName());
    verify(userRepository).save(any(User.class));
}
```

`any(User.class)` tells Mockito to match any `User` object. This is
useful because `createUser()` creates a new instance, and two separate
objects are not necessarily equal.

In a real Spring Data JPA project, `save()` is provided by the
repository implementation.

## 17. Testing exceptions with JUnit and Mockito

Create a custom exception:

``` java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
```

Update the service so a missing user causes an exception:

``` java
public String getUserName(int id) {
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent()) {
        return user.get().getName().toUpperCase();
    }

    throw new UserNotFoundException(
        "User not found with id: " + id
    );
}
```

Test the exception:

``` java
@Test
void shouldThrowExceptionWhenUserDoesNotExist() {
    when(userRepository.findById(99))
        .thenReturn(Optional.empty());

    UserNotFoundException exception = assertThrows(
        UserNotFoundException.class,
        () -> userService.getUserName(99)
    );

    assertEquals(
        "User not found with id: 99",
        exception.getMessage()
    );

    verify(userRepository).findById(99);
}
```

This test checks three things:

1.  The mock repository reports that the user is missing.
2.  The service throws `UserNotFoundException` with the expected
    message.
3.  The service calls the repository with ID `99`.

Do not expect `"User not found"` as a return value in this version of
the service; it throws an exception instead.

## 18. Common beginner mistakes

-   Forgetting `@Test` on a test method.
-   Forgetting the `spring-boot-starter-test` dependency.
-   Putting test classes in `src/main/java` instead of `src/test/java`.
-   Forgetting `@ExtendWith(MockitoExtension.class)` when using Mockito
    annotations in a plain JUnit 5 test.
-   Stubbing the wrong method arguments.
-   Expecting a return value when the service actually throws an
    exception.
-   Forgetting to update tests after intentionally changing business
    logic.
-   Mocking the class under test instead of mocking its dependencies.

## 19. Quick interview revision

**What is JUnit?**\
A Java framework used to write and run tests.

**What is Mockito?**\
A framework used to create mock objects and control or verify dependency
interactions.

**What is a unit test?**\
A test of a small piece of code in isolation.

**What does `@BeforeEach` do?**\
Runs before every test method.

**What does `assertThrows()` do?**\
Checks that code throws the expected exception type.

**What does `@Mock` do?**\
Creates a mock object.

**What does `@InjectMocks` do?**\
Creates the class under test and injects available mocks into it.

**What does `when().thenReturn()` do?**\
Defines what a mock returns for a particular invocation.

**What does `verify()` do?**\
Checks whether a method was called on a mock.

**Why mock a repository when testing a service?**\
To test service logic without depending on a real database.

## 20. What to practise next

1.  Test a calculator service with JUnit.
2.  Test a service that uses a mocked repository.
3.  Test both existing-user and missing-user scenarios.
4.  Test a save operation.
5.  Test an exception with `assertThrows()`.
6.  Run the test suite using `mvn test`.

**Fresher goal:** Be able to write and explain a basic JUnit test and a
Mockito service test confidently. Advanced testing features can wait
until these basics feel comfortable.
