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

## How overload resolution actually works

Overload resolution is based entirely on the **declared/static type** of the arguments and the reference itself — never on the runtime type. This trips people up when overloading is mixed with inheritance:

```java
void greet(Animal a) { System.out.println("Hello, animal"); }
void greet(Cat c)    { System.out.println("Hello, cat"); }

Animal a = new Cat("Whiskers");
greet(a); // prints "Hello, animal" -- picked at compile time using a's DECLARED type
```

Even though `a` holds a `Cat` at runtime, `greet(a)` resolves to `greet(Animal)` because that's what the compiler sees when it type-checks the call. This is the key difference from overriding: overload resolution happens once, at compile time, based on static types; override dispatch happens every time the method runs, based on the real object.

## Upcasting and downcasting

**Upcasting** is assigning a subclass object to a superclass-typed variable. It's always safe and happens implicitly:

```java
Animal a = new Cat("Whiskers"); // upcast, no cast operator needed
```

Once upcast, you can only call methods that exist on the declared type (`Animal`) — even though the object is really a `Cat`, `a.pounce()` won't compile unless `pounce()` is declared on `Animal` too.

**Downcasting** goes the other direction — treating a superclass reference as its more specific subclass — and needs an explicit cast because it can fail at runtime:

```java
Animal a = new Cat("Whiskers");
Cat c = (Cat) a;       // downcast, works because a really is a Cat
c.pounce();             // now Cat-only methods are available

Animal other = new Dog("Rex");
Cat wrong = (Cat) other; // compiles, but throws ClassCastException at runtime
```

## Checking before you downcast: `instanceof`

Because a bad downcast throws `ClassCastException`, it's good practice to check the object's real type first with `instanceof`:

```java
if (a instanceof Cat) {
    Cat c = (Cat) a;
    c.pounce();
}
```

Tomorrow's code example will put overloading, overriding, upcasting, and a safe `instanceof`-guarded downcast into one runnable program.
