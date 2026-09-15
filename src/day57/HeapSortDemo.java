import java.util.Arrays;

public class HeapSortDemo {

    public static void main(String[] args) {
        int[] arr = {8, 3, 7, 4, 9, 1, 5, 2};
        heapSort(arr);
        System.out.println("heapSort: " + Arrays.toString(arr));
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
}
