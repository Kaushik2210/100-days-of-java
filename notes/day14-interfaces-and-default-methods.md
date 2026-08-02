# Day 14: Interfaces & Default Methods

An interface is a pure contract: it says *what* a class can do without saying *how*. Where Day 13's abstract classes could mix shared state and shared behavior with unimplemented methods, an interface (before Java 8) promised nothing but method signatures — every implementing class had to supply its own body for every one of them.

## Declaring an interface

Use the `interface` keyword. Every method declared in it is implicitly `public abstract` — you never write those modifiers yourself.

```java
interface Playable {
    void play(); // implicitly public abstract
}
```

Fields in an interface are implicitly `public static final` — constants, not instance state:

```java
interface SpeedLimits {
    int HIGHWAY_KMH = 120; // really: public static final int HIGHWAY_KMH = 120;
}
```

## Implementing an interface

A class uses `implements` and must provide a body for every abstract method the interface declares, or the class itself must be declared `abstract`.

```java
class AudioTrack implements Playable {
    String title;

    AudioTrack(String title) {
        this.title = title;
    }

    @Override
    public void play() { // must be public -- can't reduce visibility below the interface's
        System.out.println("Playing audio: " + title);
    }
}
```

Note the `public` on `play()` is required even though the interface didn't spell it out: overriding methods can never narrow the access level of the method they override, and interface methods start at `public`.

## A class can implement multiple interfaces

Unlike classes, which can only `extends` one superclass, a class can `implements` as many interfaces as it needs, comma-separated. This is Java's answer to "multiple inheritance of type" without the ambiguity that comes from multiple inheritance of state.

```java
class Car implements Drivable, Honkable {
    // must implement every abstract method from both interfaces
}
```

## Default methods (Java 8+)

Before Java 8, adding a new method to a widely-implemented interface broke every existing implementation, since they'd all fail to compile until updated. The `default` keyword lets an interface method carry a real body, which implementing classes inherit automatically and may optionally override.

```java
interface Drivable {
    void drive();

    default void honk() { // has a body, unlike a normal interface method
        System.out.println("Standard horn: beep!");
    }
}
```

A class implementing `Drivable` only has to supply `drive()` — `honk()` comes for free unless it chooses to override it.

## Static methods in interfaces

Interfaces can also hold `static` methods: utility logic related to the interface but not tied to any specific implementing instance. Called as `InterfaceName.method()`, never through an instance.

```java
interface Honkable {
    default void honk() {
        System.out.println("Loud horn: HOOONK!");
    }

    static void info() {
        System.out.println("Honkable: anything that can announce its presence");
    }
}
```

## The diamond problem and how Java resolves it

If a class implements two interfaces that both supply a default method with the same signature, Java refuses to guess which one you meant — the class **must** override the method itself to resolve the conflict. Inside that override, `InterfaceName.super.method()` lets you call a specific interface's version explicitly.

```java
class Car implements Drivable, Honkable {
    String model;

    Car(String model) {
        this.model = model;
    }

    @Override
    public void drive() {
        System.out.println(model + " is driving");
    }

    @Override
    public void honk() { // required: both Drivable and Honkable define honk()
        Drivable.super.honk();
        Honkable.super.honk();
    }
}
```

Leaving `honk()` unimplemented in `Car` is a compile error — "class Car inherits unrelated defaults for honk()" — precisely because the compiler won't pick a winner on your behalf.

## Interface vs abstract class

- **State**: an abstract class can hold instance fields and a constructor; an interface's fields are always constants (`public static final`), never per-instance state.
- **Inheritance**: a class extends only one class (abstract or not), but can implement any number of interfaces.
- **Intent**: reach for an abstract class when subclasses share meaningful state or a common implementation skeleton (Day 13's `Shape`). Reach for an interface when you're describing a capability that unrelated classes might all have — `Playable`, `Drivable`, `Honkable` — regardless of what they otherwise are.
