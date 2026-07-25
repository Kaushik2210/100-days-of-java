# Day 7: Strings & the String Pool

Today covers `String` fundamentals: immutability, how string literals are
stored, common `String` methods, and mutable alternatives.

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

## Common String methods

```java
String text = "  Hello, Java World!  ";

text.length();                  // 22 - includes the surrounding spaces
text.trim();                    // "Hello, Java World!" - no return capture, no effect on text
text.strip();                   // like trim(), but Unicode-whitespace-aware
text.toLowerCase();             // "  hello, java world!  "
text.contains("Java");          // true
text.indexOf("Java");           // position of first match, or -1 if absent
text.replace("Java", "Kotlin"); // new string with all occurrences replaced
text.substring(2, 7);           // "Hello" - start inclusive, end exclusive
text.split(",");                // String[] split on the delimiter
String.join("-", "a", "b", "c"); // "a-b-c" - static helper, not an instance method
text.isEmpty();                 // false - length == 0
text.isBlank();                 // false - empty or only whitespace
```

`substring(start, end)` takes a half-open range: `end` is exclusive, so
`substring(2, 7)` gives characters at indices 2 through 6. `isEmpty()` only
checks length; `isBlank()` (Java 11+) also treats a whitespace-only string
as blank. Every one of these methods returns a new `String` — none of them
mutate `text`.

## Key points

- `String` objects are immutable: no method on `String` ever changes the
  object it's called on.
- Identical string literals are interned and share one object in the
  string pool; `new String(...)` opts out of that sharing.
- `==` compares references, not content, for any reference type including
  `String` — use `.equals()` for content comparison.
- `substring(start, end)` excludes `end`; `String.join` is a static
  method, not called on an instance.

## Common pitfalls

- Calling `s.toUpperCase();` without assigning the result and expecting
  `s` itself to change.
- Comparing strings with `==` and having it "work" in a quick test only
  because both came from literals, then breaking once one comes from user
  input, `new String(...)`, or concatenation at runtime.
- Assuming `new String("hello") == "hello"` — it's `false` even though the
  contents are identical, because `new` always allocates off the pool.
- Off-by-one errors with `substring` from forgetting the end index is
  exclusive.
- Calling `.trim()`/`.strip()` and discarding the result, then being
  surprised the original still has leading/trailing whitespace.

## Try it yourself

Run `src/day07/StringsDemo.java` to see immutability in action, the string
pool sharing identical literals, `==` vs `equals()`, and common `String`
methods in use. Mutable strings via `StringBuilder` are covered in a
follow-up commit.
