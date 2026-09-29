import java.util.Arrays;

public class TwoPointersDemo {

    public static void main(String[] args) {
        int[] sortedArr = {2, 7, 11, 15, 18, 24};

        int[] result = twoSumSorted(sortedArr, 26);
        System.out.println("twoSumSorted(target=26) -> indices " + Arrays.toString(result)
            + " -> values " + sortedArr[result[0]] + " + " + sortedArr[result[1]]);

        int[] noMatch = twoSumSorted(sortedArr, 100);
        System.out.println("twoSumSorted(target=100) -> " + Arrays.toString(noMatch) + " (no pair found)");
    }

    static int[] twoSumSorted(int[] sortedArr, int target) {
        int left = 0;
        int right = sortedArr.length - 1;

        while (left < right) {
            int sum = sortedArr[left] + sortedArr[right];
            if (sum == target) {
                return new int[]{left, right};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return new int[]{-1, -1};
    }
}
