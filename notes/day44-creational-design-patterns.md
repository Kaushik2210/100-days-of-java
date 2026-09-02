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

## Factory: hiding the choice of which class to instantiate

A Factory centralizes object creation behind a method, so callers ask for *what they want* rather than choosing *which concrete class* to `new` up themselves. This is especially useful when the exact subclass depends on runtime input, or when construction involves logic worth keeping in one place (Day 13's abstraction, applied to construction itself).

```java
interface Notification {
    void send(String message);
}

class EmailNotification implements Notification {
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}

class SmsNotification implements Notification {
    public void send(String message) {
        System.out.println("SMS: " + message);
    }
}

class NotificationFactory {
    static Notification create(String type) {
        return switch (type) {
            case "email" -> new EmailNotification();
            case "sms" -> new SmsNotification();
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
}
```

```java
Notification notification = NotificationFactory.create("email"); // caller never sees EmailNotification directly
notification.send("Your order shipped");
```

If a new notification type is added later, only the factory needs updating — every caller that already goes through `NotificationFactory.create(...)` is unaffected.

## Builder: constructing complex objects step by step

When a class has many optional fields, a constructor with a long parameter list becomes unreadable and error-prone (which `boolean` was that fourth argument again?). A Builder collects parameters through a fluent chain of method calls, then constructs the final immutable object in one step at the end — mirroring Day 15's immutability pattern for the resulting object.

```java
class Pizza {
    private final String size;
    private final boolean extraCheese;
    private final boolean stuffedCrust;

    private Pizza(Builder builder) { // private -- only Builder can construct one
        this.size = builder.size;
        this.extraCheese = builder.extraCheese;
        this.stuffedCrust = builder.stuffedCrust;
    }

    @Override
    public String toString() {
        return size + " pizza, extraCheese=" + extraCheese + ", stuffedCrust=" + stuffedCrust;
    }

    static class Builder {
        private String size = "medium"; // sensible defaults
        private boolean extraCheese = false;
        private boolean stuffedCrust = false;

        Builder size(String size) { this.size = size; return this; } // returns `this` -- enables chaining
        Builder extraCheese(boolean value) { this.extraCheese = value; return this; }
        Builder stuffedCrust(boolean value) { this.stuffedCrust = value; return this; }

        Pizza build() {
            return new Pizza(this);
        }
    }
}
```

```java
Pizza pizza = new Pizza.Builder()
    .size("large")
    .extraCheese(true)
    .build(); // stuffedCrust defaults to false -- never had to be mentioned
System.out.println(pizza);
```
