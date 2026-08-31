# Day 42: JVM Internals — Class Loading & Memory Areas

Everything covered so far has been the Java *language*. Day 42 opens Phase 6 by looking one layer down, at how the **JVM** actually turns a `.class` file into running objects and manages the memory they live in.

## Class loading: three phases

A class isn't fully "ready" the moment the JVM sees it referenced — it goes through three phases, lazily, the first time it's actually needed:

1. **Loading** — the JVM finds the compiled `.class` bytecode (from the classpath, a JAR, etc.) and creates an in-memory representation of the class.
2. **Linking** — split into **verification** (checking the bytecode is structurally valid and safe), **preparation** (allocating memory for static fields, filled with default values like `0`/`null`), and **resolution** (turning symbolic references to other classes into direct references).
3. **Initialization** — static field initializers and `static { }` blocks (Day 16) actually run, in the order they appear in the source, exactly once per class.

```java
class Config {
    static final String VERSION = computeVersion(); // runs during initialization, not loading

    static String computeVersion() {
        System.out.println("Config is being initialized");
        return "1.0";
    }
}
```

Referencing `Config.class` or declaring a `Config` variable doesn't trigger this — initialization happens on first *active* use: creating an instance, accessing a static field/method, or a subclass being initialized.

## The ClassLoader hierarchy

Classes are loaded by a `ClassLoader`, and the JVM uses a delegation hierarchy: the **Bootstrap** class loader (loads core `java.*` classes, written in native code) is the parent of the **Platform** class loader, which is the parent of the **Application** (or "system") class loader, which loads your own compiled classes. Each loader delegates upward first — asking its parent to load a class before trying itself — which is why your own code can never accidentally shadow `java.lang.String` with a class of the same name.

## JVM runtime memory areas

Once running, the JVM divides memory into several distinct regions, each serving a different purpose:

- **Heap** — where every object (via `new`) lives, shared across all threads. This is what the garbage collector (Day 43) manages, and what `OutOfMemoryError: Java heap space` refers to when exhausted.
- **Stack** — each thread gets its own stack, holding **stack frames**: one per active method call, storing local variables, method parameters, and the return address. A stack frame is pushed on every method call and popped on return — this is the mechanism behind recursion (Day 5's loops and any recursive method both rely on it).
- **Method Area / Metaspace** — stores per-class data: the bytecode itself, static fields (Day 16), constant pool entries, and method metadata. (Renamed and reworked from "PermGen" to "Metaspace" in Java 8, moving it out of the heap and into native memory.)
- **PC (Program Counter) Register** — each thread has one, tracking the address of the currently executing bytecode instruction — how the JVM knows where to resume after a method call returns.
- **Native Method Stack** — supports calls into native (non-Java) code, such as JNI calls into C/C++ libraries.

## StackOverflowError: what happens when the stack runs out

Since every method call pushes a new stack frame, unbounded recursion with no base case (Day 5) exhausts the per-thread stack and throws `StackOverflowError` — a subclass of `Error` (Day 21), not `Exception`, since it signals a serious problem the JVM itself hit rather than an ordinary application-level failure.

```java
static void recurseForever() {
    recurseForever(); // no base case -- each call pushes another stack frame, forever
}
```

Contrast this with the heap: a program that keeps creating objects and holding references to all of them eventually exhausts the heap instead, throwing `OutOfMemoryError: Java heap space` — a different region, a different failure mode, for a fundamentally different reason.
