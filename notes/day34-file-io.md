# Day 34: File I/O — java.io & java.nio

Java has two generations of file-handling APIs: the original `java.io` (stream-based, from Java 1.0) and the modern `java.nio.file` (path-based, added in Java 7). Both are still in active use — `java.nio.file` is preferred for most new code, but `java.io` streams remain common, especially wrapped by higher-level libraries.

## java.io: File and streams

`java.io.File` represents a path on disk (which may or may not exist). Reading and writing go through **streams** — `FileWriter`/`FileReader` for text, wrapped in a `BufferedWriter`/`BufferedReader` for efficient line-based access. Day 21's try-with-resources is the standard way to guarantee these get closed.

```java
File file = new File("notes.txt");

try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
    writer.write("First line");
    writer.newLine();
    writer.write("Second line");
} // writer.close() happens automatically, even if an exception is thrown

try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}
```

Every one of these constructors can throw `IOException` (a checked exception, per Day 21) — reading and writing files is inherently something that can fail (permissions, missing disk, a locked file), so the compiler forces callers to acknowledge that.
