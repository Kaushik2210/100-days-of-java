# Day 44: Design Patterns — Creational (Singleton, Factory, Builder)

A design pattern is a named, reusable solution to a recurring design problem — not a library to import, but a shape of code worth recognizing. **Creational** patterns specifically deal with *how objects get created*, so that construction logic doesn't leak all over a codebase.

## Singleton: exactly one instance

A Singleton guarantees a class has exactly one instance, globally accessible, for something genuinely single by nature — an application-wide configuration object, a connection pool, a logging registry.

The classic thread-safe approach uses a private constructor plus a static holder, relying on Day 42's class-initialization guarantee (a class initializes exactly once, and the JVM handles the synchronization for that automatically):

```java
class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig(); // created once, when the class initializes

    private String environment = "production";

    private AppConfig() {} // private -- no one else can call `new AppConfig()`

    static AppConfig getInstance() {
        return INSTANCE;
    }

    String getEnvironment() {
        return environment;
    }
}
```

```java
AppConfig config1 = AppConfig.getInstance();
AppConfig config2 = AppConfig.getInstance();
System.out.println(config1 == config2); // true -- always the same object
```

This "eager holder" style is simple and thread-safe without any explicit locking, because class initialization (Day 42) is already guaranteed by the JVM to happen exactly once, even under concurrent access. An `enum` with a single constant (Day 19) is another common, even simpler way to implement a Singleton, since enum instances are inherently created exactly once.
