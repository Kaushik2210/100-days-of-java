import java.util.HashSet;
import java.util.Set;

public class SlidingWindowDemo {

    public static void main(String[] args) {
        int[] arr = {2, 1, 5, 1, 3, 2};
        System.out.println("maxSumFixedWindow(k=3) = " + maxSumFixedWindow(arr, 3)); // window [5,1,3] = 9

        System.out.println();
        String[] tests = {"abcabcbb", "bbbbb", "pwwkew", "", "abcdef"};
        for (String test : tests) {
            System.out.println("longestUniqueSubstring(\"" + test + "\") = " + longestUniqueSubstring(test));
        }
    }

    static int longestUniqueSubstring(String s) {
        Set<Character> window = new HashSet<>();
        int left = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            while (window.contains(c)) {
                window.remove(s.charAt(left));
                left++;
            }
            window.add(c);
            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }

    static int maxSumFixedWindow(int[] arr, int k) {
        int windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += arr[i];
        }

        int maxSum = windowSum;
        for (int i = k; i < arr.length; i++) {
            windowSum += arr[i] - arr[i - k];
            maxSum = Math.max(maxSum, windowSum);
        }
        return maxSum;
    }
}
