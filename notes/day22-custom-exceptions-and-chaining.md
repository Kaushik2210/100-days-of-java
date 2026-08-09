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
