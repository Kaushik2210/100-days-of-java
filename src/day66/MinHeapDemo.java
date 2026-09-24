import java.util.Arrays;

public class MinHeapDemo {

    public static void main(String[] args) {
        MinHeap heap = new MinHeap(10);
        for (int value : new int[]{5, 3, 8, 1, 9, 2}) {
            heap.insert(value);
            System.out.println("after insert(" + value + "): peek() = " + heap.peek());
        }
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

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (data[parent] <= data[i]) break;
            swap(parent, i);
            i = parent;
        }
    }

    private void swap(int a, int b) {
        int temp = data[a];
        data[a] = data[b];
        data[b] = temp;
    }
}
