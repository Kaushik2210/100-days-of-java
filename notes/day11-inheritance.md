# Day 11: Inheritance

Phase 2 starts here. Inheritance lets one class reuse and extend the
fields and methods of another, instead of rewriting them from scratch.

## The `extends` keyword

A class can inherit from another using `extends`:

```java
public class Animal {
    String name;

    public Animal(String name) {
        this.name = name;
    }

    void eat() {
        System.out.println(name + " is eating.");
    }
}

public class Cat extends Animal {
    void meow() {
        System.out.println(name + " says meow!");
    }
}
```

`Cat` is the **subclass** (or child class), `Animal` is the **superclass**
(or parent class). Every `Cat` automatically has the `name` field and
`eat()` method from `Animal`, plus its own `meow()`:

```java
Cat cat = new Cat("Whiskers");
cat.eat();  // inherited from Animal
cat.meow(); // defined in Cat
```

## What gets inherited

A subclass inherits all `public` and `protected` members of its
superclass (and package-private members if it's in the same package).
`private` members are not directly accessible from the subclass — they
still exist as part of the object, but only the superclass's own methods
can touch them directly.

## `super()` — calling the parent constructor

A subclass's constructor doesn't automatically get the superclass's
fields initialized on its own — it must explicitly call the superclass
constructor using `super(...)`, and that call must be the first statement:

```java
public class Cat extends Animal {
    String breed;

    public Cat(String name, String breed) {
        super(name);      // calls Animal(String name)
        this.breed = breed;
    }
}
```

If you don't write a `super(...)` call yourself, Java inserts an implicit
`super();` (calling the superclass's no-argument constructor) as the first
line. This fails to compile if the superclass has no no-argument
constructor available.

Run `src/day11/InheritanceDemo.java` to see an `Animal` superclass and a
`Cat` subclass that extends it, using `super(...)` to initialize the
inherited field.

## Method overriding

A subclass can provide its own implementation of a method it inherited,
replacing the superclass's version for objects of that subclass. This is
called **overriding**:

```java
class Animal {
    void makeSound() {
        System.out.println("Some generic animal sound");
    }
}

class Cat extends Animal {
    @Override
    void makeSound() {
        System.out.println("Meow!");
    }
}
```

Unlike overloading (Day 8), an override must have the **exact same
signature** (name, parameter types, and return type — or a covariant
return type) as the method it's replacing. This is runtime behavior
replacement, not a separate method that happens to share a name.

## `@Override`

The `@Override` annotation is optional but strongly recommended. It tells
the compiler "I intend this to override a superclass method" — if the
signature doesn't actually match anything in the superclass (a typo in
the method name, a wrong parameter type), the compiler raises an error
instead of silently letting you create an unrelated new method.

## `super.method()` — calling the overridden version

Inside an override, you can still call the superclass's original
implementation using `super.methodName()`, useful when you want to extend
behavior rather than fully replace it:

```java
@Override
void makeSound() {
    super.makeSound(); // still prints the generic message first
    System.out.println("...but really, meow!");
}
```

Run `src/day11/InheritanceDemo.java` for `Animal.makeSound()` overridden
in `Cat`, including a call to `super.makeSound()`.

## Single inheritance and the `Object` root

A Java class can `extends` only **one** superclass — unlike some other
languages, there's no multiple inheritance of classes. (Interfaces, coming
on Day 14, are how Java achieves similar flexibility without that
complexity.)

Every class that doesn't explicitly extend anything implicitly extends
`java.lang.Object`. That means every object in Java — even one from a
class with no `extends` clause at all — already inherits methods like
`toString()`, `equals()`, and `hashCode()` from `Object`. We'll cover
those in depth on Day 17; for now, just know that inheritance in Java
always forms a single chain going back to `Object` at the root.

## Preventing further inheritance with `final`

Marking a class `final` stops any other class from extending it:

```java
public final class ImmutablePoint {
    final int x;
    final int y;
    // ...
}
```

You can also mark an individual method `final` inside a non-final class,
which allows the class to be extended but prevents subclasses from
overriding that specific method. This is useful when a method's behavior
must never change no matter what subclass is created.

Run `src/day11/InheritanceDemo.java` for the full `Animal`/`Cat` example
built up over today's notes.
