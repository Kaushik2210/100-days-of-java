# Day 21: Exception Handling — try/catch/finally

An exception is an object representing something that went wrong while a program was running. Instead of a method returning an error code that callers might forget to check, Java lets a method *throw* an exception, which immediately unwinds the call stack until some caller *catches* it — or the program terminates if nobody does.

## The exception hierarchy: checked vs unchecked

Every exception is a subclass of `Throwable`, which splits into two main branches:

- **`Error`** — serious problems the JVM itself runs into (`OutOfMemoryError`, `StackOverflowError`). Not meant to be caught or recovered from by application code.
- **`Exception`** — problems application code is expected to handle. This splits further:
  - **Checked exceptions** — subclasses of `Exception` that are *not* `RuntimeException` (e.g. `IOException`). The compiler forces you to either catch them or declare them with `throws` — you can't silently ignore them.
  - **Unchecked exceptions** — subclasses of `RuntimeException` (e.g. `NullPointerException`, `ArithmeticException`, `ArrayIndexOutOfBoundsException`). The compiler doesn't force handling; these usually signal programmer bugs rather than recoverable external conditions.

## try/catch

Wrap risky code in a `try` block; if it throws, control jumps to the matching `catch` block instead of crashing the program.

```java
try {
    int result = 10 / 0; // throws ArithmeticException
    System.out.println(result); // never reached
} catch (ArithmeticException e) {
    System.out.println("Can't divide by zero: " + e.getMessage());
}
```

If the exception type thrown doesn't match any `catch`, it keeps propagating up the call stack looking for a handler that does match — and if none exists anywhere, the program terminates with a stack trace.

## Multiple catch blocks and multi-catch

A `try` can have several `catch` blocks, checked top to bottom — the first one whose type matches (or is a supertype of) the thrown exception runs. More specific exception types must come before more general ones, or the compiler rejects the code as unreachable.

```java
try {
    int[] numbers = { 1, 2, 3 };
    System.out.println(numbers[5]); // throws ArrayIndexOutOfBoundsException
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("Bad index: " + e.getMessage());
} catch (Exception e) { // broader catch-all, must come after the specific one
    System.out.println("Something else went wrong: " + e.getMessage());
}
```

When two unrelated exception types should be handled identically, combine them in one `catch` with `|` instead of duplicating the block:

```java
catch (ArithmeticException | ArrayIndexOutOfBoundsException e) {
    System.out.println("Numeric or index problem: " + e.getMessage());
}
```

## finally

A `finally` block runs after the `try` (and any matching `catch`) completes, **whether or not an exception was thrown**, and even if the `try` or `catch` returns early. It's the right place for cleanup that must always happen — closing a file, releasing a lock, logging that an operation finished.

```java
try {
    System.out.println("Opening resource");
    throw new RuntimeException("boom");
} catch (RuntimeException e) {
    System.out.println("Caught: " + e.getMessage());
} finally {
    System.out.println("Closing resource"); // always runs
}
```

## try-with-resources

For any resource implementing `AutoCloseable` (streams, readers, database connections), `try (Resource r = ...)` automatically calls `r.close()` at the end of the block — even on an exception — without needing an explicit `finally`. This is the preferred pattern for resource cleanup in modern Java; Day 34 (File I/O) covers it in more depth.
