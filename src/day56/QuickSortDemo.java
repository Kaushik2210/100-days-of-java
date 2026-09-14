import java.util.Arrays;
import java.util.Random;

public class QuickSortDemo {

    static long comparisons;
    static final Random RANDOM = new Random(7);

    public static void main(String[] args) {
        int[] arr = {8, 3, 7, 4, 9, 1, 5, 2};
        quickSort(arr, 0, arr.length - 1);
        System.out.println("quickSort: " + Arrays.toString(arr));

        int n = 5_000;
        int[] alreadySorted = new int[n];
        for (int i = 0; i < n; i++) alreadySorted[i] = i;

        int[] fixedPivotInput = Arrays.copyOf(alreadySorted, n);
        comparisons = 0;
        quickSortCounted(fixedPivotInput, 0, n - 1, false); // always picks the last element -- worst case on sorted input
        long fixedPivotComparisons = comparisons;

        int[] randomPivotInput = Arrays.copyOf(alreadySorted, n);
        comparisons = 0;
        quickSortCounted(randomPivotInput, 0, n - 1, true); // random pivot -- avoids the degenerate case
        long randomPivotComparisons = comparisons;

        System.out.println();
        System.out.println("Sorting " + n + " already-sorted elements:");
        System.out.println("fixed last-element pivot: " + fixedPivotComparisons + " comparisons (O(n^2) territory)");
        System.out.println("random pivot:              " + randomPivotComparisons + " comparisons (O(n log n) territory)");
    }

    static void quickSort(int[] arr, int low, int high) {
        if (low >= high) return; // base case -- zero or one element is already sorted

        int pivotIndex = partition(arr, low, high);
        quickSort(arr, low, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, high);
    }

    static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    static void quickSortCounted(int[] arr, int low, int high, boolean randomPivot) {
        if (low >= high) return;

        int pivotIndex = partitionCounted(arr, low, high, randomPivot);
        quickSortCounted(arr, low, pivotIndex - 1, randomPivot);
        quickSortCounted(arr, pivotIndex + 1, high, randomPivot);
    }

    static int partitionCounted(int[] arr, int low, int high, boolean randomPivot) {
        if (randomPivot) {
            int randomIndex = low + RANDOM.nextInt(high - low + 1);
            swap(arr, randomIndex, high); // move the random pick to the usual pivot position, then partition normally
        }
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            comparisons++;
            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    static void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
}
