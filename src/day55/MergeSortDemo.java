import java.util.Arrays;

public class MergeSortDemo {

    public static void main(String[] args) {
        int[] arr = {8, 3, 7, 4, 9, 1, 5, 2};
        mergeSort(arr, 0, arr.length - 1);
        System.out.println("mergeSort: " + Arrays.toString(arr));
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
