public class SearchDemo {

    public static void main(String[] args) {
        int[] unsorted = {42, 7, 19, 3, 88, 15};
        System.out.println("linearSearch(unsorted, 19) = " + linearSearch(unsorted, 19));
        System.out.println("linearSearch(unsorted, 100) = " + linearSearch(unsorted, 100));

        int[] sorted = {3, 7, 15, 19, 42, 88};
        System.out.println("binarySearch(sorted, 19) = " + binarySearch(sorted, 19));
        System.out.println("binarySearch(sorted, 100) = " + binarySearch(sorted, 100));
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
