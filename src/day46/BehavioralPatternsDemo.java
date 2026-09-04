import java.util.ArrayList;
import java.util.List;

public class BehavioralPatternsDemo {

    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();
        station.subscribe(new PhoneDisplay());
        station.subscribe(new WebDashboard());
        station.setTemperature(25.5); // both observers react, with zero coupling to each other
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
