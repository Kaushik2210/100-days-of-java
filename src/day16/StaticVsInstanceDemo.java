public class StaticVsInstanceDemo {

    public static void main(String[] args) {
        Counter a = new Counter();
        Counter b = new Counter();
        a.increment();
        a.increment();
        b.increment();

        System.out.println("a.count = " + a.count);
        System.out.println("b.count = " + b.count);
        System.out.println("Counter.totalCreated = " + Counter.totalCreated);

        System.out.println("square(6) = " + MathUtils.square(6)); // no instance needed
        System.out.println("AppConfig.VERSION = " + AppConfig.VERSION);
    }
}

class Counter {
    static int totalCreated; // one copy, shared by every Counter
    int count;               // each Counter has its own copy
    int id;

    Counter() {
        totalCreated++;
        id = totalCreated;
    }

    void increment() {
        count++;
    }
}

class MathUtils {
    static int square(int n) {
        return n * n;
    }
}

class AppConfig {
    static final String VERSION;

    static {
        VERSION = "1.0.0"; // computed once, when the class is first loaded
        System.out.println("AppConfig loaded, version " + VERSION);
    }
}
