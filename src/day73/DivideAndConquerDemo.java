public class DivideAndConquerDemo {

    static int fastPowerCalls = 0;

    public static void main(String[] args) {
        System.out.println("fastPower(2, 10) = " + fastPower(2, 10));
        System.out.println("fastPower(3, 13) = " + fastPower(3, 13));

        fastPowerCalls = 0;
        fastPowerCounted(2, 10);
        System.out.println("fastPower(2, 10) made " + fastPowerCalls + " recursive calls (vs. 10 naive multiplications)");
    }

    static long fastPower(long base, int exponent) {
        if (exponent == 0) return 1;

        long half = fastPower(base, exponent / 2);
        long result = half * half;

        if (exponent % 2 != 0) {
            result *= base;
        }
        return result;
    }

    static long fastPowerCounted(long base, int exponent) {
        fastPowerCalls++;
        if (exponent == 0) return 1;

        long half = fastPowerCounted(base, exponent / 2);
        long result = half * half;

        if (exponent % 2 != 0) {
            result *= base;
        }
        return result;
    }
}
