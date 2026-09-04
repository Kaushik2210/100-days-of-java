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
        System.out.println("Phone display: " + temperature + "°C");
    }
}

class WebDashboard implements Observer {
    public void update(double temperature) {
        System.out.println("Web dashboard: " + temperature + "°C");
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
