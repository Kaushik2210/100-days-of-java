import java.util.Arrays;
import java.util.Random;

public class MergeSortDemo {

    public static void main(String[] args) {
        int[] arr = {8, 3, 7, 4, 9, 1, 5, 2};
        mergeSort(arr, 0, arr.length - 1);
        System.out.println("mergeSort: " + Arrays.toString(arr));

        int n = 20_000;
        Random random = new Random(42); // fixed seed -- same "random" data on every run
        int[] forMergeSort = new int[n];
        for (int i = 0; i < n; i++) forMergeSort[i] = random.nextInt(1_000_000);
        int[] forInsertionSort = Arrays.copyOf(forMergeSort, n); // identical data for a fair comparison

        long start = System.nanoTime();
        mergeSort(forMergeSort, 0, forMergeSort.length - 1);
        long mergeMillis = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        insertionSort(forInsertionSort);
        long insertionMillis = (System.nanoTime() - start) / 1_000_000;

        System.out.println();
        System.out.println("Sorting " + n + " random elements:");
        System.out.println("mergeSort (O(n log n)): " + mergeMillis + " ms");
        System.out.println("insertionSort (O(n^2)): " + insertionMillis + " ms");
        System.out.println("Both produce the same sorted result: "
            + Arrays.equals(forMergeSort, forInsertionSort));
    }

    static void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    static void mergeSort(int[] arr, int left, int right) {
        if (left >= right) return; // base case -- a single element (or empty range) is already sorted

        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    static void merge(int[] arr, int left, int mid, int right) {
        int[] leftPart = Arrays.copyOfRange(arr, left, mid + 1);
        int[] rightPart = Arrays.copyOfRange(arr, mid + 1, right + 1);

        int i = 0, j = 0, k = left;
        while (i < leftPart.length && j < rightPart.length) {
            if (leftPart[i] <= rightPart[j]) {
                arr[k++] = leftPart[i++];
            } else {
                arr[k++] = rightPart[j++];
            }
        }
        while (i < leftPart.length) arr[k++] = leftPart[i++];
        while (j < rightPart.length) arr[k++] = rightPart[j++];
    }
}
