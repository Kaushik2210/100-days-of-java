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
