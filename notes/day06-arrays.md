# Day 6: Arrays & Multidimensional Arrays

Today covers Java arrays: fixed-size, single-type containers. One-
dimensional arrays, multidimensional/jagged arrays, and the
`java.util.Arrays` helper class.

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

## Two-dimensional arrays

```java
int[][] grid = new int[3][4]; // 3 rows, 4 columns, all zeros
grid[1][2] = 7;
```

A Java 2D array is really an array of arrays: `grid` is an `int[3][]`
where each of the 3 slots holds its own `int[4]`. `new int[3][4]`
allocates all of that in one call, but the "rectangular" shape is a
convenience, not a distinct data structure.

```java
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6}
};
for (int row = 0; row < matrix.length; row++) {
    for (int col = 0; col < matrix[row].length; col++) {
        System.out.print(matrix[row][col] + " ");
    }
    System.out.println();
}
```

Note `matrix.length` gives the number of rows, and `matrix[row].length`
gives that specific row's length — asking for "the number of columns" only
makes sense per-row, because rows don't have to be the same length.

## Jagged arrays

```java
int[][] jagged = new int[3][];
jagged[0] = new int[]{1};
jagged[1] = new int[]{1, 2};
jagged[2] = new int[]{1, 2, 3};
```

Because a 2D array is just an array of array references, each row can be a
different length, or even `null` until assigned. This is what "jagged"
means — genuinely uneven rows, not a rectangular grid padded with zeros.
Iterating a jagged array without checking `row.length` per row (or
handling a possible `null` row) is a common source of bugs.

## The java.util.Arrays helper class

Arrays themselves have almost no methods (just `length` and `clone()`);
everything else lives in the static `java.util.Arrays` class.

```java
import java.util.Arrays;

int[] nums = {5, 3, 1, 4, 2};

Arrays.sort(nums);                       // sorts in place: [1, 2, 3, 4, 5]
int index = Arrays.binarySearch(nums, 4); // requires a sorted array first
int[] copy = Arrays.copyOf(nums, 3);      // [1, 2, 3] - truncates/pads
boolean same = Arrays.equals(nums, copy); // element-wise equality
Arrays.fill(nums, 0);                     // overwrites every element with 0
System.out.println(Arrays.toString(nums)); // readable [0, 0, 0, 0, 0]
```

`Arrays.sort` mutates the array in place and returns `void`. `==` on two
arrays compares references, not contents — `Arrays.equals` is what
actually compares elements. `System.out.println(nums)` on a raw array
prints something like `[I@1b6d3586` (the type and a hash), not the
contents — `Arrays.toString` (or `Arrays.deepToString` for 2D arrays) is
needed to print readable contents.

## Key points

- Array size is fixed at creation; there's no built-in resize.
- `array.length` is a field, not a method — no `()`.
- Arrays are reference types: assignment shares the underlying data, and
  `clone()`/`Arrays.copyOf` are needed for an independent copy.
- Numeric arrays default to `0`/`0.0`, `boolean[]` defaults to `false`,
  reference-type arrays default to `null`.
- A Java 2D array is an array of array references; `new int[3][4]` is
  shorthand for a rectangular shape, but rows can be independently sized
  or left `null` (jagged arrays).
- Array behavior (sorting, searching, comparing, printing) lives in the
  static `java.util.Arrays` class, not on the array instance.

## Common pitfalls

- Calling `array.length()` instead of `array.length` — compile error,
  since arrays have no such method.
- Off-by-one loops (`for (int i = 0; i <= arr.length; i++)`) that walk
  past the last valid index and throw
  `ArrayIndexOutOfBoundsException`.
- Assuming `int[] b = a;` makes an independent copy — it doesn't; mutating
  through `b` is visible through `a` too.
- Assuming every row of a 2D array has the same length — only true for
  arrays created with `new type[rows][cols]`; jagged arrays built row by
  row can differ.
- Forgetting a row in a jagged array can be `null` (from `new int[3][]`
  before assignment), causing `NullPointerException` on access.
- Comparing arrays with `==` expecting element-wise equality — it checks
  reference identity; use `Arrays.equals` instead.
- Calling `Arrays.binarySearch` on an unsorted array — the result is
  undefined unless the array is already sorted.

## Try it yourself

Run `src/day06/ArraysDemo.java` to see array creation, indexing, default
values, reference-sharing behavior, a rectangular 2D array, a jagged
array, and common `Arrays` utility methods.
