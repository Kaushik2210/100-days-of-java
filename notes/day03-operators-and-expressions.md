# Day 3: Operators & Expressions

Yesterday was about what values look like. Today is about combining them — the
operators that turn variables into expressions, and the rules Java uses to
evaluate those expressions.

## Arithmetic operators

`+  -  *  /  %`

Two gotchas that trip people up constantly:

- **Integer division truncates.** `7 / 2` is `3`, not `3.5`. If either operand
  is a `double`, the result is a `double`: `7.0 / 2` is `3.5`.
- **`%` is remainder, not "mod" in the mathematical sense.** For negative
  operands, the sign of the result follows the *dividend* (the left side):
  `-7 % 2` is `-1`, not `1`.

```java
System.out.println(7 / 2);     // 3
System.out.println(7.0 / 2);   // 3.5
System.out.println(-7 % 2);    // -1
System.out.println(7 % -2);    // 1
```

## Compound assignment operators

`+=  -=  *=  /=  %=`

These aren't just shorthand — they perform an implicit narrowing cast. This
means `x += y` is not exactly the same as `x = x + y` when types differ.

```java
byte b = 10;
b += 5;        // compiles: implicit cast back to byte
// b = b + 5;  // would NOT compile: b + 5 is promoted to int
```

`b + 5` promotes to `int`, and assigning an `int` to a `byte` needs an
explicit cast — but `b += 5` inserts that cast for you silently. This is a
common source of confusion when refactoring `+=` into its longhand form.

## Increment and decrement

`++x` (pre-increment) increments first, then yields the new value.
`x++` (post-increment) yields the current value, then increments.

```java
int x = 5;
int a = ++x;   // x becomes 6, a is 6
int y = 5;
int c = y++;   // c is 5, y becomes 6
```

Mixing these in the same expression (`x = x++ + ++x`) is legal but
unreadable — avoid it in real code even though exam questions love it.

## Relational and logical operators

`==  !=  <  >  <=  >=` produce a `boolean`.

`&&` and `||` are **short-circuiting**: the right-hand side is only evaluated
if needed. This matters when the right-hand side has a side effect or could
throw.

```java
String s = null;
if (s != null && s.length() > 0) {   // safe: s.length() never runs when s is null
    System.out.println("non-empty");
}
```

`&` and `|` also work on booleans but always evaluate both sides — useful
only when you specifically want both sides to run (e.g. both call methods
with needed side effects).

## The ternary operator

`condition ? valueIfTrue : valueIfFalse` — a compact if/else that produces a
value.

```java
int a = 7, b = 3;
int max = (a > b) ? a : b;
```

Nesting ternaries is possible but hurts readability fast; one level is
usually the practical limit.

## Bitwise operators

`&  |  ^  ~` operate on the individual bits of integer types.

- `&` (AND) — bit is 1 only if both operand bits are 1.
- `|` (OR) — bit is 1 if either operand bit is 1.
- `^` (XOR) — bit is 1 if exactly one operand bit is 1.
- `~` (NOT) — flips every bit, including the sign bit.

```java
int a = 6;   // 0110
int b = 3;   // 0011
System.out.println(a & b);   // 0010 -> 2
System.out.println(a | b);   // 0111 -> 7
System.out.println(a ^ b);   // 0101 -> 5
System.out.println(~a);      // -7 (bitwise NOT flips the sign bit too)
```

`~a` is `-7`, not `-6` or `9` — because Java integers are stored in two's
complement, flipping every bit of `6` gives `-(6 + 1)`. This trips people up
the first time they see it: `~x` is always `-x - 1`.

A common real use of `&` is checking a single flag bit without an if-chain:
`if ((flags & MASK) != 0) { ... }`.

## Shift operators

`<<  >>  >>>` shift the bits of an integer left or right.

- `<<` (left shift) — shifts bits left, filling with zeros. Equivalent to
  multiplying by `2^n` (for values that don't overflow).
- `>>` (signed right shift) — shifts bits right, filling with the sign bit
  (so negative numbers stay negative). Equivalent to dividing by `2^n`,
  rounding toward negative infinity.
- `>>>` (unsigned right shift) — shifts bits right, always filling with
  zeros, regardless of sign. Only meaningfully different from `>>` on
  negative numbers.

```java
System.out.println(4 << 2);     // 16  (4 * 2^2)
System.out.println(-8 >> 1);    // -4  (sign-preserving)
System.out.println(-8 >>> 1);   // a large positive number (sign bit is zero-filled)
```

`>>>` has no meaning for `long` vs zero-vs-negative confusion once you
remember: it never cares about the sign, `>>` always does.

## The instanceof operator

`instanceof` checks whether an object is an instance of a given type at
runtime, producing a `boolean`. It's the safe way to check a type before
casting a reference.

```java
Object obj = "hello";
if (obj instanceof String) {
    System.out.println("obj is a String");
}
```

Since Java 16, pattern matching for `instanceof` lets you check and cast in
one step, binding the result to a new variable only inside the branch where
the check succeeded:

```java
Object obj = "hello";
if (obj instanceof String str) {
    System.out.println("length is " + str.length());   // str is already a String here
}
```

This replaces the older two-step idiom of checking with `instanceof` and
then manually casting:

```java
if (obj instanceof String) {
    String str = (String) obj;   // the old way — an extra, redundant cast
    System.out.println("length is " + str.length());
}
```

`instanceof` against `null` always evaluates to `false` — it never throws,
which makes it a safe guard before a cast that otherwise risks a
`ClassCastException` (see Day 2's exception-handling section).

## Operator precedence

Java evaluates `*`, `/`, `%` before `+`, `-`; shift operators before
relational operators (including `instanceof`); comparisons before `&`, `^`,
`|`; and those before `&&`/`||`; assignment is last. When in doubt, use
parentheses — they cost nothing and remove ambiguity for the next reader.

```java
int result = 2 + 3 * 4;       // 14, not 20
int clearer = 2 + (3 * 4);    // same value, clearer intent
```

## Key points

- Integer division truncates toward zero; use a `double` operand to get a
  fractional result.
- `%` follows the sign of the dividend, not mathematical modulo.
- Compound assignment operators (`+=` etc.) insert an implicit narrowing
  cast — they are not pure shorthand for the longhand expression.
- `&&`/`||` short-circuit; `&`/`|` on booleans do not.
- `~x` equals `-x - 1` because of two's complement — it is not just "flip
  the digits" in the everyday sense.
- `>>` preserves sign, `>>>` always zero-fills regardless of sign.
- `instanceof` never throws, even against `null` — it's the safe guard
  before a cast.
- Pattern matching for `instanceof` (Java 16+) combines the check and cast
  into one expression.
- Prefer explicit parentheses over relying on precedence rules from memory.

## Common pitfalls

- Assuming `/` always gives a fractional answer — it doesn't for two `int`s.
- Using `x++` and `++x` interchangeably without checking which value is
  actually needed.
- Chaining `==` for `String` comparison (covered on Day 2's wrapper-class
  section) — the same reference-vs-value trap applies to operator misuse
  more broadly.
- Reaching for `>>` when `>>>` is what's actually needed (e.g. hashing code
  that must not care about sign).
- Casting a reference without an `instanceof` guard first, risking a
  `ClassCastException` at runtime.

## Try it yourself

Run [`OperatorsDemo.java`](../src/day03/OperatorsDemo.java) and predict each
line of output before running it — especially the division, modulo,
increment, bitwise/shift, and instanceof sections.
