# Day 35: Serialization

Serialization is converting an object's in-memory state into a byte stream that can be saved to a file, sent over a network, or otherwise stored outside the running JVM — and deserialization reverses that, rebuilding an equivalent object from the bytes.

## The Serializable interface

A class opts into Java's built-in serialization by implementing `java.io.Serializable`. This is a **marker interface** — it declares no methods at all — it exists purely so the JVM can check `instanceof Serializable` before attempting to serialize an object.

```java
class Person implements Serializable {
    String name;
    int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

## Writing and reading objects

`ObjectOutputStream` writes a whole object graph to a stream; `ObjectInputStream` reads it back. Both wrap an underlying `java.io` stream (Day 34), and both are used with try-with-resources like any other closeable resource.

```java
Person original = new Person("Asha", 30);

try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
    out.writeObject(original);
}

try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
    Person restored = (Person) in.readObject(); // requires a cast -- readObject() returns Object
    System.out.println(restored.name + ", " + restored.age);
}
```

The restored object is a genuinely new instance — `restored == original` is `false` — but it has the same field values, reconstructed purely from the byte stream, without ever calling `Person`'s constructor.

## serialVersionUID

Every `Serializable` class should declare a `private static final long serialVersionUID`, a version number embedded in the serialized bytes. If the class changes shape (new/removed fields) without updating this ID consistently, deserializing old data can throw `InvalidClassException`. Declaring it explicitly (rather than letting the JVM compute one from the class's structure) gives control over exactly when compatibility breaks.

```java
class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    // ...
}
```
