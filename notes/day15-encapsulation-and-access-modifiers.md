# Day 15: Encapsulation & Access Modifiers

Encapsulation is bundling an object's data with the methods that operate on it, and hiding that data from direct outside access. The object controls how its own state can be read or changed — callers go through methods, never the raw fields.

## Private fields, public accessors

The standard pattern: make fields `private`, then expose controlled access through public getter/setter methods.

```java
class BankAccount {
    private double balance; // outside code cannot touch this directly

    BankAccount(double openingBalance) {
        this.balance = openingBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        balance += amount;
    }
}
```

Without encapsulation, `account.balance = -500;` would be perfectly legal and silently corrupt the object. With `balance` private and `deposit()` as the only way to increase it, that validation is enforced everywhere, permanently, by construction.

## Why it matters

- **Invariants stay true.** The class alone decides what states are valid, so bugs from "someone set this field to garbage" become impossible instead of merely unlikely.
- **Implementation can change freely.** As long as the public methods keep their meaning, the private fields backing them can be renamed, split, or recomputed without breaking any caller.
- **The public surface is small and intentional.** Callers only see what the class chose to expose, not every internal detail.