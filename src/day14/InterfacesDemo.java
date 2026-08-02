public class InterfacesDemo {

    public static void main(String[] args) {
        Playable track = new AudioTrack("Lofi Beats");
        track.play();

        System.out.println("Highway limit: " + SpeedLimits.HIGHWAY_KMH + " km/h");

        Car car = new Car("Sedan");
        car.drive();
        car.honk(); // resolves the diamond -- calls both parent defaults explicitly
        Honkable.info();
    }
}

interface Playable {
    void play(); // implicitly public abstract
}

class AudioTrack implements Playable {
    String title;

    AudioTrack(String title) {
        this.title = title;
    }

    @Override
    public void play() {
        System.out.println("Playing audio: " + title);
    }
}

interface SpeedLimits {
    int HIGHWAY_KMH = 120; // implicitly public static final
}

interface Drivable {
    void drive();

    default void honk() {
        System.out.println("Standard horn: beep!");
    }
}

interface Honkable {
    default void honk() {
        System.out.println("Loud horn: HOOONK!");
    }

    static void info() {
        System.out.println("Honkable: anything that can announce its presence");
    }
}

// implements two interfaces with a clashing default method -- must override honk()
class Car implements Drivable, Honkable {
    String model;

    Car(String model) {
        this.model = model;
    }

    @Override
    public void drive() {
        System.out.println(model + " is driving");
    }

    @Override
    public void honk() {
        Drivable.super.honk();
        Honkable.super.honk();
    }
}
