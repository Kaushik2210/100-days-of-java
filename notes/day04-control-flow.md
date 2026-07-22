# Day 4: Control Flow — if/else & switch

Today's focus is on how Java decides which statements actually run: the
`if`/`else` family first, then `switch`.

## The plain if

```java
int age = 20;
if (age >= 18) {
    System.out.println("adult");
}
```

The condition must be a `boolean` expression — Java will not accept `0`/`1`
as truthy/falsy the way C does. `if (age)` simply does not compile unless
`age` is itself a `boolean`.

## if / else and else-if chains

```java
int score = 72;
if (score >= 90) {
    System.out.println("A");
} else if (score >= 80) {
    System.out.println("B");
} else if (score >= 70) {
    System.out.println("C");
} else {
    System.out.println("F");
}
```

Each `else if` is really just an `else` whose body is another `if`
statement — Java has no dedicated `elif` keyword, it's all nested
`if`/`else` under the hood. Only the first matching branch runs; once one
condition is true, the rest are skipped.

## Nested if and the dangling-else trap

```java
if (a > 0)
    if (b > 0)
        System.out.println("both positive");
    else
        System.out.println("a positive, b not");
```

An `else` always binds to the *nearest* unmatched `if`, not necessarily the
one that looks right by indentation. The example above prints "a positive,
b not" only when `a > 0` and `b <= 0` — indentation is just a suggestion to
the human reader, the compiler doesn't care about it. Always use braces
`{ }` on multi-statement or nested blocks to avoid this trap.

## Truthiness is strict

```java
Boolean flag = null;
if (flag) { ... } // NullPointerException — unboxing null throws
```

If a condition is a boxed `Boolean` that happens to be `null`, Java
auto-unboxes it to evaluate the `if`, and unboxing `null` throws an NPE at
runtime. This is a common bug when a condition comes from a method that can
return `null` instead of `true`/`false`.

## The classic switch statement

```java
int day = 3;
switch (day) {
    case 1:
        System.out.println("Monday");
        break;
    case 2:
        System.out.println("Tuesday");
        break;
    case 3:
        System.out.println("Wednesday");
        break;
    default:
        System.out.println("Unknown");
}
```

`switch` compares a value against a list of `case` labels. `case` values
must be compile-time constants (literals, `final` constants, or enum
constants) — you cannot `case` on an arbitrary variable or a range. Without
a `break`, execution **falls through** into the next case's body, which is
a classic source of bugs when someone forgets it:

```java
switch (day) {
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
        System.out.println("weekday"); // grouped labels, intentional fall-through
        break;
    case 6:
    case 7:
        System.out.println("weekend");
        break;
}
```

Grouping labels with no body between them (like `case 1:` through
`case 5:` above) is the one fall-through pattern that's usually intentional
and readable; falling through *with* a body and no `break` is almost always
a mistake.

## Switch expressions (Java 14+) and arrow syntax

Modern Java lets `switch` be an *expression* that produces a value, using
`->` instead of `:` so there's no fall-through and no `break` needed:

```java
int day = 3;
String name = switch (day) {
    case 1, 2, 3, 4, 5 -> "weekday";
    case 6, 7 -> "weekend";
    default -> "invalid";
};
```

Each arrow branch is its own mini scope — no fall-through between them at
all, even without `break`. When a branch needs more than one statement, use
a block and `yield` to produce the value:

```java
String description = switch (day) {
    case 1, 2, 3, 4, 5 -> {
        String label = "weekday";
        yield label + " (day " + day + ")";
    }
    default -> "weekend";
};
```

The compiler also checks **exhaustiveness** for switch expressions: if
`day` were an `enum` and a case were missing (with no `default`), it won't
compile. That safety net doesn't exist for the classic statement form.

## Key points

- `if` conditions must be `boolean` — no implicit numeric-to-boolean
  conversion.
- `else if` chains are sugar for nested `if`/`else`; only one branch fires.
- Braces resolve the dangling-else ambiguity and make refactors safer.
- Unboxing a `null` `Boolean` in a condition throws `NullPointerException`.
- Classic `switch` falls through without `break`; arrow-form switch
  expressions never fall through and can `yield` a value.
- `case` labels must be compile-time constants; switch expressions over
  enums are checked for exhaustiveness by the compiler.

## Common pitfalls

- Omitting braces on a nested `if` and later adding a second statement to
  the "else" branch by mistake — it silently attaches to the inner `if`.
- Assuming `else if` short-circuits like a `switch` — it does, but each
  condition is still evaluated top to bottom until one is true, so put
  cheaper/likelier checks first for readability (not performance — the JIT
  handles that).
- Forgetting `break` in a classic `switch` and silently falling into the
  next case's code.
- Mixing classic-colon and arrow-form syntax in the same `switch` block —
  Java doesn't allow it; pick one style per switch.

## Try it yourself

Run `src/day04/ControlFlowDemo.java` to see the else-if grading chain, the
dangling-else example, a classic fall-through `switch`, and an arrow-form
switch expression with `yield`.
