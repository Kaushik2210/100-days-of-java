# Day 22: Custom Exceptions & Exception Chaining

The built-in exceptions (`IllegalArgumentException`, `IOException`, etc.) describe generic problems. When your own code has a failure mode with meaning specific to your domain — "insufficient funds," "seat already booked" — a custom exception class communicates that far more clearly than reusing a generic one.

## Defining a custom exception

Extend `Exception` for a checked exception (callers must handle or declare it), or `RuntimeException` for an unchecked one (callers aren't forced to). Give it a constructor that passes a message (and optionally a cause) up to the parent via `super(...)`.

```java
class InsufficientFundsException extends Exception { // checked: caller must handle it
    InsufficientFundsException(String message) {
        super(message);
    }
}
```

```java
class BankAccount {
    private double balance;

    BankAccount(double balance) {
        this.balance = balance;
    }

    void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException(
                "Cannot withdraw " + amount + "; balance is only " + balance);
        }
        balance -= amount;
    }
}
```

```java
BankAccount account = new BankAccount(100.0);
try {
    account.withdraw(500.0);
} catch (InsufficientFundsException e) {
    System.out.println("Withdrawal failed: " + e.getMessage());
}
```

Because `withdraw` declares `throws InsufficientFundsException`, every caller is forced by the compiler to either catch it or declare it themselves — the checked-exception contract from Day 21 flows through custom exceptions exactly like built-in ones.

## Exception chaining

Sometimes a low-level failure (a database error, a parsing error) needs to be reported as a higher-level, more meaningful exception to the caller — without losing the original cause. `Throwable` has a constructor that accepts a **cause**, and `getCause()` retrieves it later. This is exception chaining: wrap the original exception instead of discarding it.

```java
class ReportGenerationException extends RuntimeException {
    ReportGenerationException(String message, Throwable cause) {
        super(message, cause); // preserves the original exception as the cause
    }
}

class ReportService {
    void generate() {
        try {
            parseData(); // throws a low-level NumberFormatException
        } catch (NumberFormatException e) {
            throw new ReportGenerationException("Failed to generate report", e); // wrap, don't discard
        }
    }

    private void parseData() {
        Integer.parseInt("not-a-number"); // throws NumberFormatException
    }
}
```

```java
try {
    new ReportService().generate();
} catch (ReportGenerationException e) {
    System.out.println(e.getMessage());
    System.out.println("Caused by: " + e.getCause()); // the original NumberFormatException
}
```

Without chaining — catching the low-level exception and throwing a brand-new one with no cause — the stack trace loses the real origin of the failure, making the bug much harder to track down in production. Always pass the original exception as the `cause` when you wrap it.

## Best practices

- Extend `Exception` for conditions callers should be forced to handle (checked); extend `RuntimeException` for programmer errors or conditions that usually can't be meaningfully recovered from at the call site (unchecked).
- Give custom exceptions a clear, specific name (`InsufficientFundsException`, not `MyException`).
- Always chain the original cause when wrapping one exception in another — never swallow it silently.
- Don't create a custom exception type just to rename a built-in one with no new information; only do it when it adds real domain meaning.
