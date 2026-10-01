public class DivideAndConquerDemo {

    static int fastPowerCalls = 0;

    public static void main(String[] args) {
        System.out.println("fastPower(2, 10) = " + fastPower(2, 10));
        System.out.println("fastPower(3, 13) = " + fastPower(3, 13));

        fastPowerCalls = 0;
        fastPowerCounted(2, 10);
        System.out.println("fastPower(2, 10) made " + fastPowerCalls + " recursive calls (vs. 10 naive multiplications)");

        System.out.println();
        int[] arr = {-2, 1, -3, 4, -1, 2, 1, -5, 4}; // the classic maximum-subarray example; answer is [4,-1,2,1] = 6
        int dcResult = maxSubarraySum(arr, 0, arr.length - 1);
        int bruteForceResult = bruteForceMaxSubarray(arr);
        System.out.println("maxSubarraySum (divide and conquer) = " + dcResult);
        System.out.println("bruteForceMaxSubarray (O(n^2) check) = " + bruteForceResult);
        System.out.println("they match: " + (dcResult == bruteForceResult));
    }

    static int maxSubarraySum(int[] arr, int left, int right) {
        if (left == right) return arr[left];

        int mid = left + (right - left) / 2;
        int leftMax = maxSubarraySum(arr, left, mid);
        int rightMax = maxSubarraySum(arr, mid + 1, right);
        int crossMax = maxCrossingSum(arr, left, mid, right);

        return Math.max(Math.max(leftMax, rightMax), crossMax);
    }

    static int maxCrossingSum(int[] arr, int left, int mid, int right) {
        int leftSum = Integer.MIN_VALUE;
        int sum = 0;
        for (int i = mid; i >= left; i--) {
            sum += arr[i];
            leftSum = Math.max(leftSum, sum);
        }

        int rightSum = Integer.MIN_VALUE;
        sum = 0;
        for (int i = mid + 1; i <= right; i++) {
            sum += arr[i];
            rightSum = Math.max(rightSum, sum);
        }

        return leftSum + rightSum;
    }

    // O(n^2) brute force, used only to cross-check the divide-and-conquer result
    static int bruteForceMaxSubarray(int[] arr) {
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            int sum = 0;
            for (int j = i; j < arr.length; j++) {
                sum += arr[j];
                best = Math.max(best, sum);
            }
        }
        return best;
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
