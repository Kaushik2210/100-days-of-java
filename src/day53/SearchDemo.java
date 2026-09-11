public class SearchDemo {

    public static void main(String[] args) {
        int[] unsorted = {42, 7, 19, 3, 88, 15};
        System.out.println("linearSearch(unsorted, 19) = " + linearSearch(unsorted, 19));
        System.out.println("linearSearch(unsorted, 100) = " + linearSearch(unsorted, 100));

        int[] sorted = {3, 7, 15, 19, 42, 88};
        System.out.println("binarySearch(sorted, 19) = " + binarySearch(sorted, 19));
        System.out.println("binarySearch(sorted, 100) = " + binarySearch(sorted, 100));

        System.out.println("binarySearchRecursive(sorted, 42) = "
            + binarySearchRecursive(sorted, 42, 0, sorted.length - 1));

        System.out.println();
        int size = 1_000_000;
        int[] bigSorted = new int[size];
        for (int i = 0; i < size; i++) bigSorted[i] = i;
        int target = size - 1; // worst case for both: the very last element

        comparisons = 0;
        linearSearchCounted(bigSorted, target);
        System.out.println("Linear search comparisons on " + size + " elements: " + comparisons);

        comparisons = 0;
        binarySearchCounted(bigSorted, target);
        System.out.println("Binary search comparisons on " + size + " elements: " + comparisons);
    }

    static int comparisons = 0;

    static int linearSearchCounted(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            comparisons++;
            if (arr[i] == target) return i;
        }
        return -1;
    }

    static int binarySearchCounted(int[] sortedArr, int target) {
        int low = 0;
        int high = sortedArr.length - 1;
        while (low <= high) {
            comparisons++;
            int mid = low + (high - low) / 2;
            if (sortedArr[mid] == target) {
                return mid;
            } else if (sortedArr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    static int binarySearchRecursive(int[] sortedArr, int target, int low, int high) {
        if (low > high) return -1; // base case -- search range is empty, target isn't present

        int mid = low + (high - low) / 2;
        if (sortedArr[mid] == target) {
            return mid;
        } else if (sortedArr[mid] < target) {
            return binarySearchRecursive(sortedArr, target, mid + 1, high);
        } else {
            return binarySearchRecursive(sortedArr, target, low, mid - 1);
        }
    }

    static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }

    static int binarySearch(int[] sortedArr, int target) {
        int low = 0;
        int high = sortedArr.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (sortedArr[mid] == target) {
                return mid;
            } else if (sortedArr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }
}
