# Day 49: Unit Testing with JUnit & Mocking

A unit test verifies one small piece of code (typically one method or class) in isolation, automatically, so a regression shows up immediately instead of being discovered in production. JUnit is the standard framework for writing and running these tests in Java, and is what `mvn test`/`gradle test` (Day 48) actually execute under the hood.

## Writing a test with JUnit 5

A test class is ordinary Java; each test method is annotated `@Test` and uses static assertion methods from `org.junit.jupiter.api.Assertions` to state what must be true.

```java
class Calculator {
    int add(int a, int b) {
        return a + b;
    }

    int divide(int a, int b) {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
        return a / b;
    }
}
```

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @Test
    void addsTwoPositiveNumbers() {
        Calculator calculator = new Calculator();
        assertEquals(5, calculator.add(2, 3)); // fails the test if the actual value differs
    }

    @Test
    void dividingByZeroThrows() {
        Calculator calculator = new Calculator();
        assertThrows(ArithmeticException.class, () -> calculator.divide(10, 0));
    }
}
```

`assertEquals(expected, actual)`, `assertTrue`/`assertFalse`, `assertNull`/`assertNotNull`, and `assertThrows` (which takes a lambda, Day 29) cover the vast majority of everyday assertions. A failing assertion throws internally, which JUnit catches and reports as a failed test — the rest of that one test method stops running, but other test methods are unaffected.

## Lifecycle annotations

`@BeforeEach`/`@AfterEach` run before/after every single test method in a class — ideal for setting up (and tearing down) a fresh instance of the thing under test, so tests never leak state into each other. `@BeforeAll`/`@AfterAll` (on a `static` method) run once for the whole class, for expensive setup shared safely across tests.

```java
class CalculatorTest {
    Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator(); // fresh instance before every test method
    }

    @Test
    void addsTwoPositiveNumbers() {
        assertEquals(5, calculator.add(2, 3));
    }
}
```

## Why mocking: isolating the unit under test

A "unit" test should test one unit — but real code often depends on collaborators that are slow, unreliable, or side-effecting in tests: a database (Day 47), a network call, the system clock. **Mockito** creates a fake stand-in object ("a mock") for such a dependency, so a test can control exactly what it returns and verify exactly how it was used, without ever touching the real thing.

```java
interface PaymentGateway {
    boolean charge(String cardNumber, double amount);
}

class OrderService {
    private final PaymentGateway gateway;

    OrderService(PaymentGateway gateway) {
        this.gateway = gateway; // dependency injected in, not created internally -- this is what makes mocking possible
    }

    boolean placeOrder(String cardNumber, double amount) {
        if (amount <= 0) return false;
        return gateway.charge(cardNumber, amount);
    }
}
```

## Mocking with Mockito: mock, when/thenReturn, verify

```java
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

PaymentGateway mockGateway = mock(PaymentGateway.class); // a fake PaymentGateway, no real network call

when(mockGateway.charge("4111-1111-1111-1111", 50.0)).thenReturn(true); // program its behavior

OrderService service = new OrderService(mockGateway);
boolean result = service.placeOrder("4111-1111-1111-1111", 50.0);

assertTrue(result);
verify(mockGateway).charge("4111-1111-1111-1111", 50.0); // asserts the mock was actually called this way
```

`mock(PaymentGateway.class)` creates an object satisfying the `PaymentGateway` interface where every method does nothing and returns a default value, until `when(...).thenReturn(...)` programs specific behavior for specific arguments. `verify(mock).method(args)` asserts that a particular call actually happened — useful for confirming side-effecting logic ran (e.g. "the order service really did call charge," not just "the return value looked right").

This only works cleanly because `OrderService` receives its `PaymentGateway` through the constructor (**dependency injection**) rather than constructing a real one internally — a design principle that pays off specifically because it makes substituting a mock trivial in tests.
