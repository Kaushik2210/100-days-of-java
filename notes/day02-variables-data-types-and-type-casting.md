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

## Wrapper classes and autoboxing
Every primitive type has a corresponding **wrapper class** — an object version of it: `int` → `Integer`, `double` → `Double`, `char` → `Character`, `boolean` → `Boolean`, and so on. Wrapper classes exist because primitives can't be used where an object is required — for example, the generic collections you'll meet later (`List<Integer>`) can't hold a raw `int`.

```java
int primitive = 42;
Integer boxed = primitive;       // autoboxing: primitive -> wrapper, automatic
int backToPrimitive = boxed;     // unboxing: wrapper -> primitive, automatic
```

This automatic conversion is called **autoboxing** (primitive to wrapper) and **unboxing** (wrapper to primitive). It's convenient, but it has a real cost: a wrapper is a full object on the heap, not a few bytes on the stack, so autoboxing inside a tight loop can noticeably hurt performance. Wrapper classes also carry useful static helpers, e.g. `Integer.parseInt("42")` to convert a `String` to an `int`, and `Integer.MAX_VALUE` / `Integer.MIN_VALUE` for the type's limits.

## Type inference with `var`
Since Java 10, you can let the compiler infer a local variable's type from the value you assign, using `var` instead of the explicit type:

```java
var count = 10;          // inferred as int
var name = "Kaushik";    // inferred as String
var price = 19.99;       // inferred as double
```

`var` is still statically typed — the compiler locks in the inferred type at compile time, it just saves you from writing it out. It only works for local variables with an initializer; you can't write `var x;` with no value, because there'd be nothing to infer from.

## Characters are numbers underneath
A `char` is really a 16-bit unsigned integer holding a Unicode code point, so it supports arithmetic directly:

```java
char letter = 'A';
int code = letter;        // widening: char -> int, gives 65
char next = (char) (letter + 1); // narrowing back to char gives 'B'
```

This is how alphabet-shifting logic (like a Caesar cipher) works under the hood — you're just doing integer math on the underlying code points.

## Readable numeric literals and constants
Java lets you use underscores inside numeric literals purely for readability — the compiler ignores them:
```java
int million = 1_000_000; // identical to 1000000
long creditCardNumber = 1234_5678_9012_3456L;
```

Use `final` to declare a constant — a variable whose value can never be reassigned after initialization:
```java
final double TAX_RATE = 0.08;
// TAX_RATE = 0.09; // compile error: cannot assign a value to final variable
```

## Exception handling basics for narrowing casts
Here's the part that surprises people coming from other languages: **a primitive narrowing cast never throws an exception, no matter how much data it loses.** `(int) 3000000000.5` doesn't crash — it just silently produces the wrong number. The compiler requires you to write the cast explicitly precisely because it's your way of telling Java "I know this might lose information, do it anyway."

```java
long tooBig = 5_000_000_000L;
int overflowed = (int) tooBig; // silently wraps around to a nonsense negative number
System.out.println(overflowed); // 705032704 — no exception, no warning
```

So if primitive casts don't throw, where does exception handling actually come in for type conversion? Two places:

**1. Parsing a `String` into a number can throw `NumberFormatException`** — this isn't a cast at all, it's `Integer.parseInt`/`Double.parseDouble` trying to interpret text, and it fails loudly if the text isn't a valid number:
```java
try {
    int value = Integer.parseInt("not a number");
} catch (NumberFormatException e) {
    System.out.println("Couldn't parse that as an int: " + e.getMessage());
}
```

**2. Casting one reference type to an incompatible one throws `ClassCastException`** at runtime — this is the reference-type equivalent of a narrowing cast, and unlike primitives, Java *does* check it and fails loudly:
```java
Object value = "a String, not a number";
try {
    Integer number = (Integer) value; // compiles fine, blows up at runtime
} catch (ClassCastException e) {
    System.out.println("Cannot cast that Object to Integer: " + e.getMessage());
}
```

The `try`/`catch` block lets the program recover instead of crashing: code inside `try` runs normally until something throws; if the matching exception type is thrown, execution jumps straight to the matching `catch` block instead of terminating the program.

## Key points
- Java has no automatic conversion between unrelated types (e.g. you can't assign a `String` to an `int` without parsing it).
- Narrowing casts truncate for floating-point-to-integer conversions — `(int) 19.99` gives `19`, not `20`.
- `float` and `double` cannot represent every decimal value exactly (binary floating point), which is why financial calculations typically avoid them.
- Variables declared inside a method (local variables) are not automatically initialized — you must assign a value before using one, or the compiler will reject the code.
- Autoboxing/unboxing is automatic but not free — it allocates a wrapper object, which matters in performance-sensitive code.
- `var` only affects how you *write* the declaration; the variable's type is still fixed at compile time.
- Primitive narrowing casts fail silently (wrong value, no exception); reference-type casts and `String` parsing fail loudly (an exception you can catch).

## Common pitfalls
- Forgetting the `L` suffix on a long literal that's too big for `int` (`long x = 3000000000;` fails to compile — it needs `3000000000L`).
- Assuming narrowing casts round; they truncate toward zero.
- Comparing `float`/`double` values with `==` and expecting exact equality — rounding error can make `0.1 + 0.2 == 0.3` evaluate to `false`.
- Comparing two boxed `Integer` objects with `==` instead of `.equals()` — for values outside -128 to 127, `==` compares references, not numeric value, and can silently give `false` for equal numbers.
- Trying to cast a `boolean` to or from any numeric type — Java doesn't allow it at all, unlike C.
- Assuming a primitive narrowing cast will throw if the value overflows — it won't; you have to range-check manually before casting if that matters.
- Forgetting to wrap `Integer.parseInt` in a `try`/`catch` when the input comes from outside the program (user input, a file, a network request) — untrusted text is never guaranteed to be a valid number.

## Try it yourself
See `src/day02/VariablesDemo.java`.
