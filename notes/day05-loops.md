# Day 5: Loops — for, while & do-while

Today covers Java's three loop constructs, starting with `for`.

## The classic for loop

```java
for (int i = 0; i < 5; i++) {
    System.out.println("i = " + i);
}
```

Three parts, all optional, separated by `;`: an init (`int i = 0`), a
condition checked *before* each iteration (`i < 5`), and an update run
*after* each iteration body (`i++`). The loop variable declared in the init
is scoped to the loop — it doesn't exist after the closing brace.

An empty `for (;;) { ... }` is a valid infinite loop; all three clauses are
optional as long as the semicolons stay.

## Multiple loop variables

```java
for (int i = 0, j = 10; i < j; i++, j--) {
    System.out.println("i=" + i + " j=" + j);
}
```

The init and update clauses can hold comma-separated statements as long as
they all declare the same type (init) or are simple expressions (update).

## The enhanced for-each loop

```java
int[] nums = {2, 4, 6, 8};
for (int n : nums) {
    System.out.println(n);
}
```

The for-each form iterates any array or `Iterable` without manual
indexing. It reads elements in order but gives no access to the index
itself, and it copies each element into `n` — mutating `n` inside the loop
does not change the underlying array or collection.

## Key points

- The for loop's three clauses run in this order per iteration: condition
  check, body, update — except the very first iteration, where init runs
  once before the first condition check.
- The loop variable's scope is the loop itself.
- for-each hides the index; use the classic `for` when you need it or need
  to modify the collection you're iterating over.

## Common pitfalls

- Reusing a loop variable name from an outer scope — Java forbids
  shadowing a local variable in this specific case, so it's a compile
  error, not a silent bug (unlike some languages).
- Modifying a `List` while iterating it with for-each throws
  `ConcurrentModificationException` — that's a topic for the Collections
  days, but worth knowing the for-each form is unsafe for in-loop removal.

## Try it yourself

Run `src/day05/LoopsDemo.java` to see the classic for loop, the
multi-variable form, and a for-each loop over an array. `while` and
`do-while` are covered in a follow-up commit.
