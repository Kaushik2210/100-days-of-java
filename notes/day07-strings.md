# Day 7: Strings & the String Pool

Today covers `String` fundamentals: immutability, how string literals are
stored, and why `==` on strings is a trap.

## Strings are immutable

```java
String s = "hello";
s.toUpperCase();          // returns a NEW string, does not modify s
System.out.println(s);    // still "hello"

String upper = s.toUpperCase(); // must capture the return value
System.out.println(upper);      // "HELLO"
```

Every `String` method that looks like it "changes" the string
(`toUpperCase`, `trim`, `concat`, `replace`, `substring`, ...) actually
returns a brand-new `String` object and leaves the original untouched.
Once a `String` object is created, its internal character data can never
change. This is a deliberate design choice: it makes strings safe to
share freely (multiple variables can point at the same string with no
risk of one mutating it under another's feet) and is what makes the
string pool (below) safe to do at all.

## The string pool

```java
String a = "hello";
String b = "hello";
System.out.println(a == b); // true - same pooled object

String c = new String("hello");
System.out.println(a == c); // false - c is a distinct heap object
```

String literals are interned automatically: the JVM keeps a special pool
of string literals, and any two identical literals in the source code
refer to the exact same object in memory. This only works because strings
are immutable — sharing an object across unrelated variables is only safe
if none of them can change it.

`new String("hello")` deliberately bypasses the pool and allocates a new,
separate object on the heap, even though its contents are identical.

## == vs equals()

```java
String a = "hello";
String c = new String("hello");
System.out.println(a == c);        // false - compares references
System.out.println(a.equals(c));   // true - compares contents
```

`==` on reference types (including `String`) always compares *identity*
(are these the same object in memory?), never content. `equals()` is
overridden by `String` to compare the actual characters. Because pooled
literals often happen to share identity, `==` can *appear* to work in
simple tests and then fail mysteriously once a string arrives from
`new String(...)`, string concatenation of variables, `substring`, or I/O
— always use `.equals()` (or `.equalsIgnoreCase()`) to compare string
content.

## Key points

- `String` objects are immutable: no method on `String` ever changes the
  object it's called on.
- Identical string literals are interned and share one object in the
  string pool; `new String(...)` opts out of that sharing.
- `==` compares references, not content, for any reference type including
  `String` — use `.equals()` for content comparison.

## Common pitfalls

- Calling `s.toUpperCase();` without assigning the result and expecting
  `s` itself to change.
- Comparing strings with `==` and having it "work" in a quick test only
  because both came from literals, then breaking once one comes from user
  input, `new String(...)`, or concatenation at runtime.
- Assuming `new String("hello") == "hello"` — it's `false` even though the
  contents are identical, because `new` always allocates off the pool.

## Try it yourself

Run `src/day07/StringsDemo.java` to see immutability in action, the string
pool sharing identical literals, and `==` vs `equals()` behaving
differently for pooled vs. `new`-allocated strings. Common `String`
methods and `StringBuilder` are covered in follow-up commits.
