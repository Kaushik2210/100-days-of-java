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

## The four access modifiers

Java has four levels of visibility, from most to least restrictive:

| Modifier | Same class | Same package | Subclass (different package) | Everywhere |
|---|---|---|---|---|
| `private` | yes | no | no | no |
| *(none — package-private)* | yes | yes | no | no |
| `protected` | yes | yes | yes | no |
| `public` | yes | yes | yes | yes |

- **`private`** — only visible inside the declaring class itself. This is the default choice for fields, per the encapsulation pattern above.
- **package-private (no modifier)** — visible to any class in the same package, but invisible outside it. Useful for helper classes that are implementation details of a package, not part of its public API.
- **`protected`** — like package-private, plus visible to subclasses even if they live in a different package. Used when a superclass wants to expose something to subclasses (for `super.field` access or overriding) without making it public to everyone.
- **`public`** — visible from anywhere. Reserve this for the deliberate API of a class.

```java
package accounts;

public class Account {
    private String pin;          // only Account itself can read/write this
    String branchCode;           // any class in package `accounts` can use this
    protected double balance;    // subclasses (any package) and package `accounts` can use this
    public String accountId;     // anyone can use this
}
```

## Encapsulation enables immutability

A class with no setters and only `final` fields set once in the constructor can't be modified after creation at all — every field is readable but never writable from outside, and not even the class itself can change it later.

```java
final class Point {
    private final double x;
    private final double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double getX() { return x; }
    double getY() { return y; }

    Point translated(double dx, double dy) { // returns a new Point instead of mutating this one
        return new Point(x + dx, y + dy);
    }
}
```

Immutable objects are simpler to reason about — they can be shared freely between threads or callers with no risk of one holder's change surprising another, because there is no way to change them at all after construction.