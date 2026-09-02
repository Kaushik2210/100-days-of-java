public class CreationalPatternsDemo {

    public static void main(String[] args) {
        AppConfig config1 = AppConfig.getInstance();
        AppConfig config2 = AppConfig.getInstance();
        System.out.println("config1 == config2: " + (config1 == config2));
        System.out.println("environment = " + config1.getEnvironment());
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
