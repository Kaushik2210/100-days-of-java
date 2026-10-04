public class DynamicProgrammingDemo {

    static int naiveCalls = 0;
    static int memoCalls = 0;

    public static void main(String[] args) {
        naiveCalls = 0;
        long naiveResult = fibNaive(10);
        System.out.println("fibNaive(10)  = " + naiveResult + ", using " + naiveCalls + " calls (matches Day 52's 177)");

        memoCalls = 0;
        long memoResult = fibMemo(10, new long[11]);
        System.out.println("fibMemo(10)   = " + memoResult + ", using " + memoCalls + " calls");

        System.out.println("fibTable(10)  = " + fibTable(10));

        System.out.println();
        System.out.println("fibMemo(50)  = " + fibMemo(50, new long[51]) + " (known: 12586269025)");
        System.out.println("fibTable(50) = " + fibTable(50));
        System.out.println("fibTable(90) = " + fibTable(90) + " (known: 2880067194370816120)");
    }

    static long fibNaive(int n) {
        naiveCalls++;
        if (n <= 1) return n;
        return fibNaive(n - 1) + fibNaive(n - 2);
    }

    static long fibMemo(int n, long[] cache) {
        memoCalls++;
        if (n <= 1) return n;
        if (cache[n] != 0) return cache[n];

        cache[n] = fibMemo(n - 1, cache) + fibMemo(n - 2, cache);
        return cache[n];
    }

    static long fibTable(int n) {
        if (n <= 1) return n;
        long[] table = new long[n + 1];
        table[0] = 0;
        table[1] = 1;
        for (int i = 2; i <= n; i++) {
            table[i] = table[i - 1] + table[i - 2];
        }
        return table[n];
    }
}
