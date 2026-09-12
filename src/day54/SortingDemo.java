import java.util.Arrays;

public class SortingDemo {

    public static void main(String[] args) {
        int[] a = {5, 2, 9, 1, 5, 6};
        bubbleSort(a);
        System.out.println("bubbleSort:    " + Arrays.toString(a));

        int[] b = {5, 2, 9, 1, 5, 6};
        selectionSort(b);
        System.out.println("selectionSort: " + Arrays.toString(b));

        int[] c = {5, 2, 9, 1, 5, 6};
        insertionSort(c);
        System.out.println("insertionSort: " + Arrays.toString(c));

        System.out.println();
        System.out.println("Stability check -- sort by value, watch tagged duplicates:");
        Tagged[] tagged = {
            new Tagged(5, "A"), new Tagged(2, "B"), new Tagged(5, "C"), new Tagged(1, "D"), new Tagged(5, "E")
        };
        insertionSortTagged(tagged);
        System.out.println("insertion (stable):   " + Arrays.toString(tagged));

        Tagged[] tagged2 = {
            new Tagged(5, "A"), new Tagged(2, "B"), new Tagged(5, "C"), new Tagged(1, "D"), new Tagged(5, "E")
        };
        selectionSortTagged(tagged2);
        System.out.println("selection (unstable): " + Arrays.toString(tagged2));
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

    static void insertionSortTagged(Tagged[] arr) {
        for (int i = 1; i < arr.length; i++) {
            Tagged key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].value > key.value) { // strictly greater -- equal elements never swap past each other
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    static void selectionSortTagged(Tagged[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j].value < arr[minIndex].value) {
                    minIndex = j;
                }
            }
            Tagged temp = arr[minIndex]; // this swap can jump an equal element out of its original relative order
            arr[minIndex] = arr[i];
            arr[i] = temp;
        }
    }

    static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    static void selectionSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            int temp = arr[minIndex];
            arr[minIndex] = arr[i];
            arr[i] = temp;
        }
    }
}

class Tagged {
    int value;
    String tag; // identifies which original element this is, so reordering among equals is visible

    Tagged(int value, String tag) {
        this.value = value;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return value + tag;
    }
}
