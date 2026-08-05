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
