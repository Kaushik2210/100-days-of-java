public class ClassLoadingDemo {

    public static void main(String[] args) {
        System.out.println("main() started");
        System.out.println("Referencing Config.class does not initialize it: " + Config.class.getName());

        System.out.println("Accessing a static field now:");
        System.out.println("VERSION = " + Config.VERSION); // this is what triggers initialization

        System.out.println("ClassLoader for this class: " + ClassLoadingDemo.class.getClassLoader());
        System.out.println("ClassLoader for String: " + String.class.getClassLoader()); // null -- loaded by the bootstrap loader
    }
}

class Config {
    static final String VERSION = computeVersion(); // runs during initialization, not loading

    static String computeVersion() {
        System.out.println("Config is being initialized");
        return "1.0";
    }
}
