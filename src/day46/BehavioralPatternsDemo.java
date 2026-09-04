import java.util.ArrayList;
import java.util.List;

public class BehavioralPatternsDemo {

    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();
        station.subscribe(new PhoneDisplay());
        station.subscribe(new WebDashboard());
        station.setTemperature(25.5); // both observers react, with zero coupling to each other

        Order regular = new Order(price -> price);
        Order studentOrder = new Order(price -> price * 0.9);
        Order vipOrder = new Order(price -> price * 0.7);
        System.out.println("regular: " + regular.finalPrice(100));
        System.out.println("student: " + studentOrder.finalPrice(100));
        System.out.println("vip: " + vipOrder.finalPrice(100));

        Light light = new Light();
        RemoteControl remote = new RemoteControl();
        remote.press(new LightOnCommand(light));
        remote.press(new LightOffCommand(light));
    }
}

interface DiscountStrategy {
    double apply(double price);
}

class Order {
    private final DiscountStrategy discount;

    Order(DiscountStrategy discount) {
        this.discount = discount;
    }

    double finalPrice(double price) {
        return discount.apply(price);
    }
}

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
        history.add(command);
    }
}

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
            observer.update(temperature);
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
