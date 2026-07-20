# Day 3 — Operators & Expressions

## What's an expression?
An expression is anything that evaluates to a value: `2 + 3`, `age >= 18`, `isActive && hasPermission`. Operators are the symbols that combine values into expressions.

## Arithmetic operators
```java
int a = 17, b = 5;
System.out.println(a + b); // 22
System.out.println(a - b); // 12
System.out.println(a * b); // 85
System.out.println(a / b); // 3  — integer division truncates
System.out.println(a % b); // 2  — remainder ("modulo")
```
Integer division between two `int` values always produces an `int` — the fractional part is discarded, not rounded. To get a decimal result, at least one operand must be a floating-point type: `17.0 / 5` gives `3.4`.

## Relational and logical operators
Relational operators (`==`, `!=`, `<`, `>`, `<=`, `>=`) compare two values and produce a `boolean`. Logical operators combine booleans:

```java
boolean isAdult = age >= 18;
boolean canVote = isAdult && isCitizen;   // AND — both must be true
boolean needsHelp = isLost || isInjured;  // OR — at least one true
boolean isMinor = !isAdult;               // NOT — flips the value
```

`&&` and `||` **short-circuit**: in `a && b`, if `a` is `false`, Java never evaluates `b` at all. This matters when the right-hand side has a side effect or could throw:
```java
if (index < array.length && array[index] != null) { ... }
```
If `index < array.length` is `false`, Java skips the array access entirely, avoiding an `ArrayIndexOutOfBoundsException`. Swapping the order would break this safety.

## Assignment and compound assignment
```java
int score = 10;
score += 5;  // same as score = score + 5;  -> 15
score -= 3;  // 12
score *= 2;  // 24
score /= 4;  // 6
```

## Increment/decrement and operator precedence
```java
int x = 5;
int y = x++; // y = 5 (post-increment: use old value, then increment)
int z = ++x; // z = 7 (pre-increment: increment first, then use it)
```
Java follows standard mathematical precedence (`*` and `/` before `+` and `-`), and parentheses always win:
```java
int result = 2 + 3 * 4;   // 14, not 20
int forced = (2 + 3) * 4; // 20
```

## Key points
- Integer division truncates toward zero; it never rounds.
- `&&`/`||` short-circuit — the right operand may never run.
- `==` compares primitive values directly, but for reference types (like `String`) it compares references, not content — that's a trap for Day 7.
- When precedence is unclear or the expression is complex, add parentheses for readability even if they're not strictly required.

## Common pitfalls
- Expecting `5 / 2` to give `2.5` — it gives `2`, because both operands are `int`.
- Using `=` (assignment) when `==` (comparison) was intended inside an `if` condition — this is a classic bug, though Java's type system blocks it for non-boolean expressions.
- Relying on evaluation order in `a || b` when `b` has a side effect you actually need to always run.

## Try it yourself
See `src/day03/OperatorsDemo.java`.
