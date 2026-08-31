public class ClassLoadingDemo {

    public static void main(String[] args) {
        System.out.println("main() started");
        System.out.println("Referencing Config.class does not initialize it: " + Config.class.getName());

        System.out.println("Accessing a static field now:");
        System.out.println("VERSION = " + Config.VERSION); // this is what triggers initialization

        System.out.println("ClassLoader for this class: " + ClassLoadingDemo.class.getClassLoader());
        System.out.println("ClassLoader for String: " + String.class.getClassLoader()); // null -- loaded by the bootstrap loader

        try {
            recurseForever(0);
        } catch (StackOverflowError e) {
            System.out.println("Caught StackOverflowError -- the per-thread stack ran out of frames");
        }
    }

    static void recurseForever(long depth) {
        recurseForever(depth + 1); // no base case -- each call pushes another stack frame
    }
}

class Config {
    static final String VERSION = computeVersion(); // runs during initialization, not loading

    static String computeVersion() {
        System.out.println("Config is being initialized");
        return "1.0";
    }
}
