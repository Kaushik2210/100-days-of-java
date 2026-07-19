# Day 1 — Introduction to Java & Environment Setup

## What is Java?
Java is a class-based, object-oriented, statically-typed language that compiles to **bytecode** instead of native machine code. That bytecode runs on the **Java Virtual Machine (JVM)**, which is what gives Java its "write once, run anywhere" property — the same `.class` file runs unmodified on Windows, Linux, or macOS as long as a JVM is installed.

## JDK vs JRE vs JVM
- **JVM (Java Virtual Machine)** — executes bytecode. Every platform has its own JVM implementation, but the bytecode it runs is identical everywhere.
- **JRE (Java Runtime Environment)** — the JVM plus the standard class libraries needed to *run* Java programs. No compiler.
- **JDK (Java Development Kit)** — the JRE plus development tools: `javac` (compiler), `javadoc`, `jar`, debuggers, etc. You need the JDK to *write and compile* Java; the JRE alone can't compile anything.

## From source to a running program
1. You write source code in a `.java` file.
2. `javac Hello.java` compiles it to `Hello.class` — platform-independent bytecode.
3. `java Hello` starts a JVM, loads `Hello.class`, and executes its `main` method.

```
HelloJava.java --(javac)--> HelloJava.class --(java)--> running program
```

## Anatomy of a minimal Java program
```java
public class HelloJava {
    public static void main(String[] args) {
        System.out.println("Hello, Java!");
    }
}
```
- `public class HelloJava` — the file must be named `HelloJava.java` to match the public class name.
- `public static void main(String[] args)` — the JVM looks for exactly this method signature as the program's entry point. `static` means it belongs to the class itself, not an instance, so the JVM can call it without creating an object first.
- `System.out` is a `PrintStream` connected to standard output; `println` writes a line and appends a newline.

## Key points
- Java is statically typed — types are checked at compile time, catching many errors before the program ever runs.
- Everything (aside from primitives) is an object, and every class ultimately extends `java.lang.Object`.
- Semicolons terminate statements; braces `{}` define blocks and scope.

## Try it yourself
See `src/day01/HelloJava.java`. Compile and run it with:
```
javac HelloJava.java
java HelloJava
```
