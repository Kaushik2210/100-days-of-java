# Day 19: Enums

An `enum` is a special type whose entire set of possible values is fixed and known at compile time — a small, closed set of named constants. Under the hood every enum is a full class (implicitly extending `java.lang.Enum`), so it can carry fields, constructors, and methods, but its instances are limited to exactly the constants you declare.

## Declaring an enum

```java
enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
```

Each name (`MONDAY`, `TUESDAY`, ...) is a `public static final` instance of `Day` — there are exactly seven `Day` objects that will ever exist, created once when the class is loaded.

```java
Day today = Day.WEDNESDAY;
```

## values(), ordinal(), and valueOf()

Every enum automatically gets these methods for free:

- **`values()`** — a static method returning an array of every constant, in declaration order. Useful for iterating over all possibilities.
- **`ordinal()`** — the constant's position in the declaration (zero-based). `Day.MONDAY.ordinal()` is `0`.
- **`valueOf(String)`** — a static method that parses a `String` back into the matching constant, throwing `IllegalArgumentException` if there's no match.

```java
for (Day d : Day.values()) {
    System.out.println(d + " -> " + d.ordinal());
}

Day parsed = Day.valueOf("FRIDAY"); // Day.FRIDAY
```

## Enums in switch statements

Enums pair naturally with `switch` — no need to qualify the constant name inside the switch body, since the compiler already knows the type being switched on.

```java
String description;
switch (today) {
    case SATURDAY, SUNDAY -> description = "Weekend";
    default -> description = "Weekday";
}
```

Because the set of enum constants is closed, some tools and compiler warnings can flag a `switch` on an enum that doesn't cover every constant, catching a class of bugs that plain `int` or `String` codes can't.

## Enums with fields, constructors, and methods

Since an enum is a real class, its constants can carry their own data. Declare fields and a constructor exactly like a normal class — each constant then supplies its own constructor arguments, once, at the point it's declared.

```java
enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS(4.869e+24, 6.0518e6),
    EARTH(5.976e+24, 6.37814e6);

    private final double mass;   // kilograms
    private final double radius; // meters

    Planet(double mass, double radius) { // enum constructors are always private, implicitly
        this.mass = mass;
        this.radius = radius;
    }

    double surfaceGravity() {
        final double G = 6.67300E-11;
        return G * mass / (radius * radius);
    }
}
```

```java
System.out.println(Planet.EARTH.surfaceGravity()); // regular method call on a constant
```

The constructor can never be `public` or `protected` — enum constants are only ever constructed once, internally, when the enum class is loaded, so `new Planet(...)` outside the enum is not legal.

## A different method body per constant

Each constant can override a method with its own implementation by supplying a body in `{ }` right after its constructor arguments. This is useful when behavior genuinely differs per constant instead of just data.

```java
enum Operation {
    PLUS {
        @Override
        double apply(double a, double b) { return a + b; }
    },
    MINUS {
        @Override
        double apply(double a, double b) { return a - b; }
    };

    abstract double apply(double a, double b); // each constant must implement this
}
```

```java
System.out.println(Operation.PLUS.apply(2, 3)); // 5.0
```

This avoids a `switch` on the enum scattered throughout the codebase every time behavior needs to branch on which constant it is — each constant already knows what to do.

## EnumSet and EnumMap

`java.util` provides `EnumSet` and `EnumMap`, specialized collections for enum keys that are far more memory- and time-efficient than a general-purpose `HashSet`/`HashMap`, because they're backed by a bitmask over the enum's fixed set of constants rather than a hash table. Prefer them whenever the key type is an enum.
