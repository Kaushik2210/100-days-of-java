import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BitManipulationDemo {

    public static void main(String[] args) {
        int n = 0b1010; // 10
        System.out.println("n = " + binary(n) + " (" + n + ")");
        System.out.println("isSet(n, 1) = " + isSet(n, 1) + ", isSet(n, 2) = " + isSet(n, 2));
        System.out.println("setBit(n, 0)    = " + binary(setBit(n, 0)));
        System.out.println("clearBit(n, 3)  = " + binary(clearBit(n, 3)));
        System.out.println("toggleBit(n, 2) = " + binary(toggleBit(n, 2)));

        System.out.println();
        int negative = -16;
        System.out.println("-16 >> 2  = " + (negative >> 2) + "  (signed: sign bit copied in, stays negative)");
        System.out.println("-16 >>> 28 = " + (negative >>> 28) + "  (unsigned: zeros shifted in)");

        System.out.println();
        Set<Integer> powersOfTwo = new HashSet<>();
        for (int k = 0; k <= 30; k++) powersOfTwo.add(1 << k);

        int powerMismatches = 0;
        for (int i = -100; i <= 5000; i++) {
            if (isPowerOfTwo(i) != powersOfTwo.contains(i)) powerMismatches++;
        }
        int[] edgeCases = {0, 1, 2, 3, 1 << 30, Integer.MAX_VALUE, Integer.MIN_VALUE};
        for (int edge : edgeCases) {
            if (isPowerOfTwo(edge) != powersOfTwo.contains(edge)) powerMismatches++;
        }
        System.out.println("isPowerOfTwo vs a set of all 31 positive powers, over -100..5000 plus edge cases: "
            + powerMismatches + " mismatches");
        System.out.println("isPowerOfTwo(Integer.MIN_VALUE) = " + isPowerOfTwo(Integer.MIN_VALUE)
            + " (only one bit set, but negative, so correctly rejected)");

        Random random = new Random(5); // fixed seed so the run is repeatable
        int bitCountMismatches = 0;
        for (int i = -1000; i <= 1000; i++) {
            if (countSetBits(i) != Integer.bitCount(i)) bitCountMismatches++;
        }
        for (int i = 0; i < 10_000; i++) {
            int value = random.nextInt(); // covers the whole int range, including negatives
            if (countSetBits(value) != Integer.bitCount(value)) bitCountMismatches++;
        }
        for (int edge : edgeCases) {
            if (countSetBits(edge) != Integer.bitCount(edge)) bitCountMismatches++;
        }
        System.out.println("countSetBits vs Integer.bitCount over ~11,000 values incl. negatives: "
            + bitCountMismatches + " mismatches");
    }

    static boolean isSet(int n, int i) {
        return (n & (1 << i)) != 0;
    }

    static int setBit(int n, int i) {
        return n | (1 << i);
    }

    static int clearBit(int n, int i) {
        return n & ~(1 << i);
    }

    static int toggleBit(int n, int i) {
        return n ^ (1 << i);
    }

    static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    static int countSetBits(int n) {
        int count = 0;
        while (n != 0) {
            n &= n - 1;
            count++;
        }
        return count;
    }

    static String binary(int n) {
        String bits = Integer.toBinaryString(n);
        return "0b" + "0".repeat(Math.max(0, 4 - bits.length())) + bits;
    }
}
