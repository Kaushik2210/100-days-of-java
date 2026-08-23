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

## java.nio.file: Path and Files

`java.nio.file.Path` replaces `File` as the modern representation of a filesystem location, and the `Files` utility class provides static methods for nearly every common operation in one line — no manual stream wiring required for simple cases.

```java
Path path = Path.of("notes.txt"); // or Paths.get("notes.txt") on older code

Files.writeString(path, "First line\nSecond line"); // one call -- creates or overwrites the file

List<String> lines = Files.readAllLines(path); // whole file, already split into lines
System.out.println(lines);

String content = Files.readString(path); // whole file as one String
System.out.println(content);
```

`Files` also covers everyday filesystem checks and operations directly: `Files.exists(path)`, `Files.delete(path)`, `Files.copy(source, target)`, `Files.createDirectories(path)`, `Files.size(path)` — replacing a lot of the manual, stream-juggling code `java.io` required for the same tasks.

## java.io vs java.nio.file

- **`java.io`** (`File` + streams) — still common for streaming large files incrementally (line by line, or in chunks) without loading the whole thing into memory, and for interoperating with older APIs that expect `InputStream`/`OutputStream`.
- **`java.nio.file`** (`Path` + `Files`) — the better default for most modern code: simpler one-line whole-file reads/writes, clearer exceptions, and a richer set of filesystem operations (symbolic links, file attributes, directory walking via `Files.walk`).

Both can be mixed freely — `Files.newBufferedReader(path)` returns a `java.io.BufferedReader`, bridging the two APIs when you need `nio`'s path handling with `io`'s streaming style.
