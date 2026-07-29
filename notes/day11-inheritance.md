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
