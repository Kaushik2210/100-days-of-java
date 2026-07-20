# Day 2 — Variables, Data Types & Type Casting

## Declaring variables
A variable in Java is a named, typed storage location. Unlike some scripting languages, Java is statically typed: once you declare a variable's type, it can never hold a value of a different type.

```java
int age = 25;
double price = 19.99;
char grade = 'A';
boolean isActive = true;
```

## The eight primitive types
Java has exactly eight primitive types, split by what they represent:

| Type | Size | Example | Notes |
|------|------|---------|-------|
| `byte` | 8-bit | `byte b = 100;` | Range -128 to 127 |
| `short` | 16-bit | `short s = 30000;` | Rarely used directly |
| `int` | 32-bit | `int n = 42;` | Default choice for whole numbers |
| `long` | 64-bit | `long big = 10000000000L;` | Note the `L` suffix |
| `float` | 32-bit | `float f = 3.14f;` | Note the `f` suffix |
| `double` | 64-bit | `double d = 3.14159;` | Default choice for decimals |
| `char` | 16-bit | `char c = 'X';` | Holds a single UTF-16 code unit |
| `boolean` | 1-bit (JVM-dependent) | `boolean flag = false;` | Only `true`/`false` |

Everything else in Java — `String`, arrays, your own classes — is a **reference type**: the variable holds a reference (essentially an address) to an object on the heap, not the object itself.

## Type casting
Casting converts a value from one type to another.

**Widening (implicit, safe)** — going from a smaller type to a bigger one happens automatically, because no information can be lost:
```java
int wholeNumber = 42;
double asDouble = wholeNumber; // widening: int -> double, no cast needed
```

**Narrowing (explicit, risky)** — going from a bigger type to a smaller one requires you to say so explicitly with `(type)`, because information can be lost:
```java
double price = 19.99;
int wholePart = (int) price; // narrowing: truncates to 19, does NOT round
```

## Key points
- Java has no automatic conversion between unrelated types (e.g. you can't assign a `String` to an `int` without parsing it).
- Narrowing casts truncate for floating-point-to-integer conversions — `(int) 19.99` gives `19`, not `20`.
- `float` and `double` cannot represent every decimal value exactly (binary floating point), which is why financial calculations typically avoid them.
- Variables declared inside a method (local variables) are not automatically initialized — you must assign a value before using one, or the compiler will reject the code.

## Common pitfalls
- Forgetting the `L` suffix on a long literal that's too big for `int` (`long x = 3000000000;` fails to compile — it needs `3000000000L`).
- Assuming narrowing casts round; they truncate toward zero.
- Comparing `float`/`double` values with `==` and expecting exact equality — rounding error can make `0.1 + 0.2 == 0.3` evaluate to `false`.

## Try it yourself
See `src/day02/VariablesDemo.java`.
