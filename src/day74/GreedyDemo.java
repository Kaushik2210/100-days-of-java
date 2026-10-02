import java.util.Arrays;

public class GreedyDemo {

    public static void main(String[] args) {
        // {start, end} pairs; the optimal non-overlapping set here is (1,2),(3,4),(5,7),(8,9) = 4 activities
        int[][] activities = {{1, 3}, {2, 4}, {3, 5}, {1, 2}, {5, 7}, {8, 9}, {5, 9}};
        System.out.println("maxNonOverlapping = " + maxNonOverlapping(activities));
    }

    static int maxNonOverlapping(int[][] activities) {
        Arrays.sort(activities, (a, b) -> a[1] - b[1]);

        int count = 0;
        int lastEnd = Integer.MIN_VALUE;
        for (int[] activity : activities) {
            int start = activity[0];
            int end = activity[1];
            if (start >= lastEnd) {
                count++;
                lastEnd = end;
            }
        }
        return count;
    }
}
