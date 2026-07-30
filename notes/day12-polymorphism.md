# Day 12: Polymorphism (Compile-time & Runtime)

Polymorphism means "many forms" — the same method call can behave differently depending on the object it's actually acting on. Java has two kinds: **compile-time polymorphism** (method overloading, already seen on Day 8) and **runtime polymorphism** (method overriding, seen on Day 11). Today is about understanding runtime polymorphism properly — how it actually works and why it's useful.

## The core idea

When a subclass overrides a method, you can refer to the subclass object using a superclass-typed variable. At runtime, Java figures out which version of the method to call based on the object's *actual* type, not the variable's declared type.

```java
Animal a = new Cat("Whiskers");
a.makeSound(); // calls Cat's makeSound(), not Animal's
```

Even though `a` is declared as `Animal`, the JVM looks at what `a` actually points to (a `Cat`) and calls `Cat`'s overridden method. This is called **dynamic method dispatch**.

## Why this matters

Without polymorphism, working with a group of related objects means writing separate code for every subclass. With it, you can write one method that accepts the superclass type and let each object handle the details itself.

```java
Animal[] animals = { new Cat("Whiskers"), new Dog("Rex") };
for (Animal x : animals) {
    x.makeSound(); // each one prints its own version
}
```

The loop doesn't know or care whether `x` is a `Cat` or `Dog` — it just calls `makeSound()` and trusts each object to do the right thing.

## Compile-time vs runtime, side by side

- **Compile-time (overloading):** the compiler picks which method to call based on the argument types it sees while compiling. Nothing about this depends on what happens when the program runs.
- **Runtime (overriding):** the JVM picks which method body to run based on the actual object type, decided while the program is executing. The compiler only checks that the method exists on the declared type.

Tomorrow's notes will build a small example showing both kinds side by side, then dig into upcasting/downcasting and `instanceof`.
