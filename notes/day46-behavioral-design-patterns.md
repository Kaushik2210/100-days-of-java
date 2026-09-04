# Day 46: Design Patterns — Behavioral (Observer, Strategy, Command)

Day 44 was about creating objects, Day 45 about composing their structure. **Behavioral** patterns are about how objects communicate and delegate responsibility — keeping objects loosely coupled while still coordinating.

## Observer: notifying dependents automatically

A subject keeps a list of observers and notifies all of them whenever its state changes, without needing to know anything concrete about who's listening — only that they implement a shared interface. This is the same idea behind GUI event listeners and the pub/sub pattern used throughout event-driven systems.

```java
interface Observer {
    void update(double temperature);
}

class WeatherStation {
    private final List<Observer> observers = new ArrayList<>();

    void subscribe(Observer observer) {
        observers.add(observer);
    }

    void setTemperature(double temperature) {
        for (Observer observer : observers) {
            observer.update(temperature); // notify every subscriber automatically
        }
    }
}

class PhoneDisplay implements Observer {
    public void update(double temperature) {
        System.out.println("Phone display: " + temperature + " degrees C");
    }
}

class WebDashboard implements Observer {
    public void update(double temperature) {
        System.out.println("Web dashboard: " + temperature + " degrees C");
    }
}
```

```java
WeatherStation station = new WeatherStation();
station.subscribe(new PhoneDisplay());
station.subscribe(new WebDashboard());
station.setTemperature(25.5); // both observers react, with zero coupling to each other
```

`WeatherStation` never references `PhoneDisplay` or `WebDashboard` by name — only the `Observer` interface — so new subscriber types can be added without ever touching `WeatherStation` itself.

## Strategy: swapping an algorithm at runtime

Strategy extracts an algorithm into its own interface, so the object using it can be handed a different implementation without any conditional logic (`if`/`switch` on a "type" field) inside the object itself. Since every strategy is a single-method interface, this is a natural home for Day 29's lambdas.

```java
interface DiscountStrategy {
    double apply(double price);
}

class Order {
    private final DiscountStrategy discount;

    Order(DiscountStrategy discount) {
        this.discount = discount;
    }

    double finalPrice(double price) {
        return discount.apply(price); // delegates the "how" to whichever strategy was injected
    }
}
```

```java
Order regular = new Order(price -> price); // no discount
Order studentOrder = new Order(price -> price * 0.9); // 10% off, as a lambda
Order vipOrder = new Order(price -> price * 0.7); // 30% off

System.out.println(regular.finalPrice(100));      // 100.0
System.out.println(studentOrder.finalPrice(100));  // 90.0
System.out.println(vipOrder.finalPrice(100));      // 70.0
```

`Order` never has an `if (customerType == STUDENT)` branch anywhere — it just calls whatever `DiscountStrategy` it was constructed with, and new pricing rules are added by writing a new strategy, not by editing `Order`.

## Command: turning a request into an object

Command wraps "do this action" as an object with a single `execute()` method, so requests can be queued, logged, undone, or passed around like any other value instead of being an immediate direct method call.

```java
interface Command {
    void execute();
}

class Light {
    void on() { System.out.println("Light is ON"); }
    void off() { System.out.println("Light is OFF"); }
}

class LightOnCommand implements Command {
    private final Light light;
    LightOnCommand(Light light) { this.light = light; }
    public void execute() { light.on(); }
}

class LightOffCommand implements Command {
    private final Light light;
    LightOffCommand(Light light) { this.light = light; }
    public void execute() { light.off(); }
}

class RemoteControl {
    private final List<Command> history = new ArrayList<>();

    void press(Command command) {
        command.execute();
        history.add(command); // the request itself is now a storable, replayable object
    }
}
```

```java
Light light = new Light();
RemoteControl remote = new RemoteControl();
remote.press(new LightOnCommand(light));
remote.press(new LightOffCommand(light));
```

Because each `Command` is a plain object, `RemoteControl` can log every command pressed, replay them in order, or (with a bit more work, adding an `undo()` method to the interface) support undo/redo — none of which is possible if `press()` just called `light.on()` directly.
