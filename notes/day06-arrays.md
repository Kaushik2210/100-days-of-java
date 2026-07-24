# Day 6: Arrays & Multidimensional Arrays

Today covers Java arrays: fixed-size, single-type containers, starting
with one-dimensional arrays.

## Declaring and creating arrays

```java
int[] scores = new int[5];        // all elements default to 0
int[] primes = {2, 3, 5, 7, 11};  // array literal, size inferred
```

`new int[5]` allocates a fixed-size block of 5 ints, all initialized to
the type's default value (`0` for numeric types, `false` for `boolean`,
`null` for reference types). An array's length is fixed at creation time —
there's no way to grow or shrink it; "resizing" always means creating a
new array and copying elements over.

## Length, indexing, and bounds

```java
System.out.println(scores.length); // 5, no parentheses - it's a field, not a method
scores[0] = 100;
System.out.println(scores[0]);
```

`length` is a public final field on the array object itself, not a method
call like `String.length()` — a very common mix-up. Valid indices run from
`0` to `length - 1`; anything outside that range throws
`ArrayIndexOutOfBoundsException` at runtime, not a compile error, since
Java can't always know the index in advance.

## Arrays are reference types

```java
int[] a = {1, 2, 3};
int[] b = a;       // b now points at the same array as a
b[0] = 99;
System.out.println(a[0]); // 99 - a and b share the same underlying array
```

An array variable holds a reference, not the data itself. Assigning one
array variable to another copies the reference, so both variables see the
same underlying elements. To get an independent copy, use
`Arrays.copyOf(a, a.length)` or `a.clone()`.

## Default values by type

```java
boolean[] flags = new boolean[3]; // {false, false, false}
String[] names = new String[3];   // {null, null, null}
double[] prices = new double[3];  // {0.0, 0.0, 0.0}
```

A newly created array of a reference type is filled with `null`, not empty
strings or empty objects — iterating and calling a method on an
uninitialized element throws `NullPointerException`.

## Key points

- Array size is fixed at creation; there's no built-in resize.
- `array.length` is a field, not a method — no `()`.
- Arrays are reference types: assignment shares the underlying data, and
  `clone()`/`Arrays.copyOf` are needed for an independent copy.
- Numeric arrays default to `0`/`0.0`, `boolean[]` defaults to `false`,
  reference-type arrays default to `null`.

## Common pitfalls

- Calling `array.length()` instead of `array.length` — compile error,
  since arrays have no such method.
- Off-by-one loops (`for (int i = 0; i <= arr.length; i++)`) that walk
  past the last valid index and throw
  `ArrayIndexOutOfBoundsException`.
- Assuming `int[] b = a;` makes an independent copy — it doesn't; mutating
  through `b` is visible through `a` too.

## Try it yourself

Run `src/day06/ArraysDemo.java` to see array creation, indexing, default
values, and the reference-sharing behavior. Multidimensional arrays are
covered in a follow-up commit.
