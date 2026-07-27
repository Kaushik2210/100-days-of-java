# Day 9: Intro to OOP — Classes & Objects

Up to now everything has lived inside `main`. Today starts object-oriented
programming: modeling real things as classes and objects.

## Classes as blueprints

A **class** is a blueprint that describes what a kind of thing looks like
and what it can do. It doesn't itself hold any data — it's a template.

```java
public class Dog {
    String name;
    int age;

    void bark() {
        System.out.println(name + " says woof!");
    }
}
```

`Dog` describes that every dog has a `name` and an `age`, and can `bark()`.
No actual dog exists yet — this is just the shape.

## Objects as instances

An **object** is a concrete instance created from a class, using `new`:

```java
Dog myDog = new Dog();
myDog.name = "Rex";
myDog.age = 3;
myDog.bark(); // Rex says woof!
```

`new Dog()` allocates a real `Dog` in memory and gives you a reference to
it (`myDog`). Each object gets its own copy of the fields defined by the
class — `myDog.name` is independent of any other `Dog` object's `name`.

## Fields and instance methods

- **Fields** (also called instance variables) are the data each object
  carries — `name` and `age` above.
- **Instance methods** are behavior that operates on that data —
  `bark()` reads `this` object's `name` without needing it passed in as a
  parameter.

Unlike the `static` methods from Day 8, instance methods belong to a
*specific object*, not to the class as a whole. You can't call `bark()`
without first having a `Dog` object to call it on.

## The dot operator

`.` is used both to access a field (`myDog.name`) and to call a method on
an object (`myDog.bark()`). In both cases you're saying "look at this
specific object, then act on it."

Run `src/day09/ClassesDemo.java` to see a `Dog` class defined and a couple
of `Dog` objects created and used.

## Encapsulation: hiding fields behind methods

Letting outside code reach in and set `myDog.age = -5;` directly is
dangerous — nothing stops invalid data from being assigned. **Encapsulation**
means making fields `private` so they can only be touched through methods
you control, which can validate input before accepting it.

```java
public class Account {
    private double balance;

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
}
```

- `private` fields are only visible inside the class itself — no other
  class can read or write `balance` directly.
- A **getter** (`getBalance`) exposes a read-only view of the field.
- A **setter**, or in this case a purpose-built method like `deposit`,
  controls exactly how the field can change, and can reject bad input
  (here, a non-positive deposit is silently ignored).

This is the core OOP idea of keeping an object's internal state consistent
by only allowing changes through a controlled interface, instead of
letting any code touch the raw data.

Run `src/day09/ClassesDemo.java` for an `Account` class that keeps
`balance` private and only exposes it through `getBalance()` and
`deposit()`.

## Object references and shared state

A variable holding an object doesn't hold the object itself — it holds a
**reference** (an address) pointing to it, same as we saw with arrays on
Day 6. Assigning one object variable to another copies the reference, not
the object:

```java
Account a = new Account();
a.deposit(100.0);

Account b = a; // b points to the SAME Account as a, not a copy
b.deposit(50.0);

System.out.println(a.getBalance()); // 150.0 — a sees b's deposit too
```

Because `a` and `b` refer to the exact same object in memory, a change made
through `b` is visible through `a`. This is different from the two
independent `Dog` objects earlier (`myDog` and `anotherDog`), where each
`new Dog()` call created a *separate* object with its own memory — no
reference was ever shared between them.

To get a truly independent copy, you have to explicitly create a new
object and copy the field values over yourself; simple assignment never
does that for you.

Run `src/day09/ClassesDemo.java` for a demonstration of two variables
referring to the same `Account` object, and how a deposit through one is
visible through the other.
