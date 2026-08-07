public class NestedClassesDemo {

    public static void main(String[] args) {
        Computer.Battery battery = new Computer.Battery(5000); // no Computer instance needed
        System.out.println("battery capacity = " + battery.capacityMah + " mAh");

        Computer laptop = new Computer("ThinkPad");
        laptop.fanSpeed = 2200;
        Computer.DiagnosticsReport report = laptop.new DiagnosticsReport(); // built through the outer instance
        System.out.println(report.summarize());

        ReportGenerator generator = new ReportGenerator();
        System.out.println(generator.buildGreeting("Asha")); // local class used inside the method

        Greeting g = new Greeting() { // anonymous class implementing Greeting on the spot
            @Override
            public String message() {
                return "Hi there!";
            }
        };
        System.out.println(g.message());
    }
}

class ReportGenerator {
    String buildGreeting(String name) {
        class Greeter { // only exists inside this method
            String greet() {
                return "Hello, " + name + "!";
            }
        }
        return new Greeter().greet();
    }
}

interface Greeting {
    String message();
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
