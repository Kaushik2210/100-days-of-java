public class NestedClassesDemo {

    public static void main(String[] args) {
        Computer.Battery battery = new Computer.Battery(5000); // no Computer instance needed
        System.out.println("battery capacity = " + battery.capacityMah + " mAh");

        Computer laptop = new Computer("ThinkPad");
        laptop.fanSpeed = 2200;
        Computer.DiagnosticsReport report = laptop.new DiagnosticsReport(); // built through the outer instance
        System.out.println(report.summarize());
    }
}

class Computer {
    String model;
    int fanSpeed;

    Computer(String model) {
        this.model = model;
    }

    static class Battery { // does not need a Computer instance to exist
        int capacityMah;

        Battery(int capacityMah) {
            this.capacityMah = capacityMah;
        }
    }

    class DiagnosticsReport { // tied to one specific Computer instance
        String summarize() {
            return model + " running fan at " + fanSpeed + " RPM";
        }
    }
}
