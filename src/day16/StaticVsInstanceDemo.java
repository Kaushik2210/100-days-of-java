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
