# Day 18: Packages & Access Control

Once a project grows past a handful of classes, two problems show up. First, names start colliding — your `List` versus the JDK's `List`, your `Order` versus the billing team's `Order`. Second, everything can see everything, so a class you meant as an internal helper gets used from the other side of the codebase and you can never change it. Packages solve the first problem; access modifiers solve the second. They're covered together because in Java the two are the same mechanism: **a package is the unit that access control is defined against.**

## What a package is

A package is a namespace, declared as the first line of a source file:

```java
package day18.library;

public class Book {
    // ...
}
```

The package name and the directory structure must match. A class declared `package day18.library;` has to live in a `day18/library/` folder, relative to the root of your source tree:

```
src/
  day18/
    PackagesDemo.java        <- package day18;
    library/
      Book.java              <- package day18.library;
```

The class's real name — its **fully-qualified name** — is the package plus the class name: `day18.library.Book`. That's what makes collisions survivable. Two classes can both be called `Book` as long as they sit in different packages, because `day18.library.Book` and `com.acme.store.Book` are distinct names as far as the compiler is concerned.

A file with no `package` line lands in the **default package**, which is fine for a scratch file and a bad idea for anything else — classes in the default package can't be imported by packaged classes at all.

### Naming convention

Package names are lowercase and conventionally use a reversed domain name to guarantee global uniqueness: `com.google.gson`, `org.junit.jupiter`, `java.util`. The reversal exists so that the most general part comes first and related packages sort together.

## Importing

To use a class from another package you either import it or spell out its fully-qualified name every time.

```java
import day18.library.Book;      // single-type import -- preferred
import java.util.*;             // on-demand (wildcard) import

java.util.List<String> names;   // fully-qualified, no import needed
```

A few rules worth knowing:

- A wildcard import does **not** import subpackages. `import java.util.*;` does not bring in `java.util.concurrent`.
- Wildcard imports cost nothing at runtime — they're a compile-time convenience, not a "load everything" instruction. The real argument against them is ambiguity: if two wildcard-imported packages both contain a `Date`, the compiler makes you disambiguate.
- `java.lang` is imported automatically. That's why `String`, `Integer`, `System`, and `Object` work with no import line.
- Classes in the **same package** need no import of each other. They're already in the same namespace.

```java
import static java.lang.Math.max;   // static import -- brings in a member, not a type

int biggest = max(3, 7);            // instead of Math.max(3, 7)
```

Static imports pull in a single `static` member. They read nicely in small doses (test assertions are the classic case) and become unreadable when overused, because the reader loses track of where `max` came from.

## Access control, seen through packages

Day 15 introduced the four access levels from the point of view of a single class. The table there still holds — what's worth adding now is that three of those four levels are defined *in terms of the package*, so they only really make sense once packages are in the picture.

### package-private is the default, and that's deliberate

Leave the modifier off entirely and you get package-private: visible inside the package, invisible outside it.

```java
package day18.library;

class InventoryRecord {   // no modifier -- package-private class
    String shelfCode;     // no modifier -- package-private field
}
```

`InventoryRecord` cannot be imported from `day18`, or from anywhere else. It doesn't appear in autocomplete outside its own package; from the rest of the program it may as well not exist. This is the tool for implementation-detail classes, and it's underused — a lot of codebases mark everything `public` out of habit and then discover they can't refactor anything without breaking a caller.

A useful way to think about it: **`public` is a promise.** Anything public is API you have agreed to keep working. Everything else is yours to change freely.

### Top-level classes get only two of the four

A class declared directly in a file can be `public` or package-private — nothing else:

```java
public class Book { }   // legal
class Helper { }        // legal -- package-private
private class Nope { }  // will not compile at the top level
```

`private` and `protected` describe visibility *relative to an enclosing class*, and a top-level class has no enclosing class. (Nested classes do, which is why they can use all four — that's Day 20.)

Also: a source file may contain only **one** public top-level class, and the file must be named after it. `Book.java` can hold `public class Book` plus any number of package-private classes alongside it.

### The `protected` rule has a catch

"`protected` means package plus subclasses" is the version everyone learns, and it's slightly too generous. Outside the declaring package, a subclass can access a protected member only **through a reference of its own type** — not through a reference to the parent in general.

```java
package day18;

import day18.library.Book;

public class SignedBook extends Book {

    void demo(Book other, SignedBook sibling) {
        System.out.println(this.isbn);     // OK -- own inherited member
        System.out.println(sibling.isbn);  // OK -- reference typed as SignedBook
        System.out.println(other.isbn);    // will NOT compile -- reference typed as Book
    }
}
```

The reasoning: `protected` exists so a subclass can manage *its own* internals. It was never meant to be a back door into arbitrary instances of the parent that happen to belong to someone else's subclass.

### Packages are not a security boundary

Package-private keeps honest callers out at compile time. It does not stop reflection, and it does not stop someone from declaring their own class in your package name to gain access. If you need a real boundary, that's what the module system (`module-info.java`, Java 9+) is for — modules control which packages are `exports`ed, and the JVM enforces it at runtime rather than only at compile time.

**Rule of thumb**: start every class and member at the most restrictive level that compiles, and widen only when a real caller needs it. It is easy to make something more visible later; making it less visible means breaking whoever started depending on it.

