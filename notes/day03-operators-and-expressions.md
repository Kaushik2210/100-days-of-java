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

## Operator precedence

Java evaluates `*`, `/`, `%` before `+`, `-`; comparisons before `&&`/`||`;
and assignment last. When in doubt, use parentheses — they cost nothing and
remove ambiguity for the next reader.

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
- Prefer explicit parentheses over relying on precedence rules from memory.

## Common pitfalls

- Assuming `/` always gives a fractional answer — it doesn't for two `int`s.
- Using `x++` and `++x` interchangeably without checking which value is
  actually needed.
- Chaining `==` for `String` comparison (covered on Day 2's wrapper-class
  section) — the same reference-vs-value trap applies to operator misuse
  more broadly.

## Try it yourself

Run [`OperatorsDemo.java`](../src/day03/OperatorsDemo.java) and predict each
line of output before running it — especially the division, modulo, and
increment sections.
