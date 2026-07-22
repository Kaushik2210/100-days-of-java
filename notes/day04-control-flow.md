# Day 4: Control Flow — if/else & switch

Today's focus is on how Java decides which statements actually run: the
`if`/`else` family first, then `switch` in a later commit.

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

## Key points

- `if` conditions must be `boolean` — no implicit numeric-to-boolean
  conversion.
- `else if` chains are sugar for nested `if`/`else`; only one branch fires.
- Braces resolve the dangling-else ambiguity and make refactors safer.
- Unboxing a `null` `Boolean` in a condition throws `NullPointerException`.

## Common pitfalls

- Omitting braces on a nested `if` and later adding a second statement to
  the "else" branch by mistake — it silently attaches to the inner `if`.
- Assuming `else if` short-circuits like a `switch` — it does, but each
  condition is still evaluated top to bottom until one is true, so put
  cheaper/likelier checks first for readability (not performance — the JIT
  handles that).

## Try it yourself

Run `src/day04/ControlFlowDemo.java` to see the else-if grading chain and
the dangling-else example side by side. A `switch` section will be added to
both files in a follow-up commit.
