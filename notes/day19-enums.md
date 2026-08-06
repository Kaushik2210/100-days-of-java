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
