public class NestedClassesDemo {

    public static void main(String[] args) {
        Computer.Battery battery = new Computer.Battery(5000); // no Computer instance needed
        System.out.println("battery capacity = " + battery.capacityMah + " mAh");
    }
}

class Computer {
    String model;

    Computer(String model) {
        this.model = model;
    }

    static class Battery { // does not need a Computer instance to exist
        int capacityMah;

        Battery(int capacityMah) {
            this.capacityMah = capacityMah;
        }
    }
}
