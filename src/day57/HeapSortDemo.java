import java.util.Arrays;

public class HeapSortDemo {

    static long comparisons;

    public static void main(String[] args) {
        int[] arr = {8, 3, 7, 4, 9, 1, 5, 2};
        heapSort(arr);
        System.out.println("heapSort: " + Arrays.toString(arr));

        System.out.println();
        System.out.println("Stability check -- sort by value, watch tagged duplicates:");
        Tagged[] tagged = {
            new Tagged(5, "A"), new Tagged(2, "B"), new Tagged(5, "C"), new Tagged(1, "D"), new Tagged(5, "E")
        };
        heapSortTagged(tagged);
        System.out.println("heap sort result: " + Arrays.toString(tagged) + " (original relative order among 5s not guaranteed)");

        System.out.println();
        int n = 5_000;
        int[] alreadySorted = new int[n];
        for (int i = 0; i < n; i++) alreadySorted[i] = i;

        comparisons = 0;
        heapSortCounted(alreadySorted);
        System.out.println("heapSort comparisons on " + n + " ALREADY-SORTED elements: " + comparisons);
        System.out.println("(Day 56's quick sort with a fixed pivot took 12,497,500 comparisons on this exact input --");
        System.out.println(" heap sort's structure never depends on the input's original order, so no such blowup happens here)");
    }

    static void heapSort(int[] arr) {
        int n = arr.length;

        // build-heap: heapify every non-leaf node, from the bottom up
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // repeatedly move the current max (the root) to the end, then re-heapify the shrunken heap
        for (int end = n - 1; end > 0; end--) {
            int temp = arr[0];
            arr[0] = arr[end];
            arr[end] = temp;
            heapify(arr, end, 0);
        }
    }

    static void heapify(int[] arr, int heapSize, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < heapSize && arr[left] > arr[largest]) largest = left;
        if (right < heapSize && arr[right] > arr[largest]) largest = right;

        if (largest != i) {
            int temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;
            heapify(arr, heapSize, largest);
        }
    }

    static void heapSortCounted(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) heapifyCounted(arr, n, i);
        for (int end = n - 1; end > 0; end--) {
            int temp = arr[0];
            arr[0] = arr[end];
            arr[end] = temp;
            heapifyCounted(arr, end, 0);
        }
    }

    static void heapifyCounted(int[] arr, int heapSize, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < heapSize) { comparisons++; if (arr[left] > arr[largest]) largest = left; }
        if (right < heapSize) { comparisons++; if (arr[right] > arr[largest]) largest = right; }

        if (largest != i) {
            int temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;
            heapifyCounted(arr, heapSize, largest);
        }
    }

    static void heapSortTagged(Tagged[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) heapifyTagged(arr, n, i);
        for (int end = n - 1; end > 0; end--) {
            Tagged temp = arr[0];
            arr[0] = arr[end];
            arr[end] = temp;
            heapifyTagged(arr, end, 0);
        }
    }

    static void heapifyTagged(Tagged[] arr, int heapSize, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < heapSize && arr[left].value > arr[largest].value) largest = left;
        if (right < heapSize && arr[right].value > arr[largest].value) largest = right;

        if (largest != i) {
            Tagged temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;
            heapifyTagged(arr, heapSize, largest);
        }
    }
}

class Tagged {
    int value;
    String tag;

    Tagged(int value, String tag) {
        this.value = value;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return value + tag;
    }
}
