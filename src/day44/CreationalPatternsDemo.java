public class CreationalPatternsDemo {

    public static void main(String[] args) {
        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();
        System.out.println("config1 == config2: " + (config1 == config2));
        System.out.println("environment = " + config1.getEnvironment());

        Notification notification = NotificationFactory.create("email");
        notification.send("Your order shipped");
        NotificationFactory.create("sms").send("Your OTP is 1234");

        Pizza pizza = new Pizza.Builder()
            .size("large")
            .extraCheese(true)
            .build(); // stuffedCrust defaults to false -- never had to be mentioned
        System.out.println(pizza);
    }
}

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

class Pizza {
    private final String size;
    private final boolean extraCheese;
    private final boolean stuffedCrust;

    private Pizza(Builder builder) {
        this.size = builder.size;
        this.extraCheese = builder.extraCheese;
        this.stuffedCrust = builder.stuffedCrust;
    }

    @Override
    public String toString() {
        return size + " pizza, extraCheese=" + extraCheese + ", stuffedCrust=" + stuffedCrust;
    }

    static class Builder {
        private String size = "medium";
        private boolean extraCheese = false;
        private boolean stuffedCrust = false;

        Builder size(String size) { this.size = size; return this; }
        Builder extraCheese(boolean value) { this.extraCheese = value; return this; }
        Builder stuffedCrust(boolean value) { this.stuffedCrust = value; return this; }

        Pizza build() {
            return new Pizza(this);
        }
    }
}

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
