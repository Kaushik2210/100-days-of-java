import java.util.Arrays;

public class MinHeapDemo {

    public static void main(String[] args) {
        MinHeap heap = new MinHeap(10);
        for (int value : new int[]{5, 3, 8, 1, 9, 2}) {
            heap.insert(value);
            System.out.println("after insert(" + value + "): peek() = " + heap.peek());
        }

        System.out.println();
        System.out.print("extractMin() in order: ");
        while (!heap.isEmpty()) {
            System.out.print(heap.extractMin() + " ");
        }
        System.out.println("(should come out fully sorted)");

        System.out.println();
        MaxHeap maxHeap = new MaxHeap(10);
        for (int value : new int[]{5, 3, 8, 1, 9, 2}) {
            maxHeap.insert(value);
        }
        System.out.print("MaxHeap extractMax() in order: ");
        while (!maxHeap.isEmpty()) {
            System.out.print(maxHeap.extractMax() + " ");
        }
        System.out.println("(should come out sorted descending)");
    }
}

class MinHeap {
    private int[] data;
    private int size = 0;

    MinHeap(int capacity) {
        data = new int[capacity];
    }

    boolean isEmpty() {
        return size == 0;
    }

    int peek() {
        if (isEmpty()) throw new IllegalStateException("Heap is empty");
        return data[0];
    }

    void insert(int value) {
        if (size == data.length) throw new IllegalStateException("Heap is full");
        data[size] = value;
        siftUp(size);
        size++;
    }

    int extractMin() {
        if (isEmpty()) throw new IllegalStateException("Heap is empty");
        int min = data[0];
        size--;
        data[0] = data[size];
        siftDown(0);
        return min;
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (data[parent] <= data[i]) break;
            swap(parent, i);
            i = parent;
        }
    }

    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;

            if (left < size && data[left] < data[smallest]) smallest = left;
            if (right < size && data[right] < data[smallest]) smallest = right;

            if (smallest == i) break;
            swap(i, smallest);
            i = smallest;
        }
    }

    private void swap(int a, int b) {
        int temp = data[a];
        data[a] = data[b];
        data[b] = temp;
    }
}

class MaxHeap {
    private int[] data;
    private int size = 0;

    MaxHeap(int capacity) {
        data = new int[capacity];
    }

    boolean isEmpty() {
        return size == 0;
    }

    void insert(int value) {
        if (size == data.length) throw new IllegalStateException("Heap is full");
        data[size] = value;
        siftUp(size);
        size++;
    }

    int extractMax() {
        if (isEmpty()) throw new IllegalStateException("Heap is empty");
        int max = data[0];
        size--;
        data[0] = data[size];
        siftDown(0);
        return max;
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (data[parent] >= data[i]) break; // flipped: parent must be >= child
            swap(parent, i);
            i = parent;
        }
    }

    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int largest = i;

            if (left < size && data[left] > data[largest]) largest = left;   // flipped: largest child wins
            if (right < size && data[right] > data[largest]) largest = right;

            if (largest == i) break;
            swap(i, largest);
            i = largest;
        }
    }

    private void swap(int a, int b) {
        int temp = data[a];
        data[a] = data[b];
        data[b] = temp;
    }
}
