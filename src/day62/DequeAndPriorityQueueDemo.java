import java.util.Comparator;
import java.util.PriorityQueue;

public class DequeAndPriorityQueueDemo {

    public static void main(String[] args) {
        Deque deque = new Deque();
        deque.addLast(2);
        deque.addLast(3);
        deque.addFirst(1);
        deque.addFirst(0);
        // deque is now 0, 1, 2, 3

        System.out.println("removeFirst() = " + deque.removeFirst()); // 0
        System.out.println("removeLast()  = " + deque.removeLast());  // 3
        System.out.println("removeFirst() = " + deque.removeFirst()); // 1
        System.out.println("removeLast()  = " + deque.removeLast());  // 2

        // both ends now empty -- exercises the "deque became empty, clear both ends" edge case
        deque.addLast(99);
        System.out.println("after refilling, removeFirst() = " + deque.removeFirst());

        System.out.println();
        System.out.println("Hand-built sorted-list priority queue (inserts 5, 1, 3, 2, 4):");
        SortedListPriorityQueue sortedPq = new SortedListPriorityQueue();
        for (int value : new int[]{5, 1, 3, 2, 4}) sortedPq.insert(value);
        StringBuilder sortedOrder = new StringBuilder();
        for (int i = 0; i < 5; i++) sortedOrder.append(sortedPq.poll()).append(' ');
        System.out.println("polled in order: " + sortedOrder.toString().trim());

        System.out.println();
        System.out.println("java.util.PriorityQueue (min-heap by default):");
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        for (int value : new int[]{5, 1, 3, 2, 4}) {
            minHeap.offer(value);
            maxHeap.offer(value);
        }
        StringBuilder minOrder = new StringBuilder();
        StringBuilder maxOrder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            minOrder.append(minHeap.poll()).append(' ');
            maxOrder.append(maxHeap.poll()).append(' ');
        }
        System.out.println("min-heap polled in order: " + minOrder.toString().trim());
        System.out.println("max-heap polled in order: " + maxOrder.toString().trim());

        System.out.println();
        int n = 30_000;
        long start = System.nanoTime();
        SortedListPriorityQueue slow = new SortedListPriorityQueue();
        for (int i = n; i > 0; i--) slow.insert(i); // descending input forces every insert to the front: cheap
        for (int i = 0; i < n; i++) slow.poll();
        long descendingMillis = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        SortedListPriorityQueue slowAscending = new SortedListPriorityQueue();
        for (int i = 1; i <= n; i++) slowAscending.insert(i); // ascending input forces a full walk on every insert
        for (int i = 0; i < n; i++) slowAscending.poll();
        long ascendingMillis = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int i = 1; i <= n; i++) heap.offer(i);
        for (int i = 0; i < n; i++) heap.poll();
        long heapMillis = (System.nanoTime() - start) / 1_000_000;

        System.out.println("Inserting and polling " + n + " elements:");
        System.out.println("sorted list, best-case input (descending):  " + descendingMillis + " ms");
        System.out.println("sorted list, worst-case input (ascending):  " + ascendingMillis + " ms");
        System.out.println("binary heap (java.util.PriorityQueue):      " + heapMillis + " ms");
    }
}

class QNode {
    int value;
    QNode next;

    QNode(int value) {
        this.value = value;
    }
}

class SortedListPriorityQueue {
    private QNode head; // kept sorted ascending, so the smallest value is always at the head

    void insert(int value) {
        QNode node = new QNode(value);
        if (head == null || value < head.value) {
            node.next = head;
            head = node;
            return;
        }
        QNode current = head;
        while (current.next != null && current.next.value <= value) {
            current = current.next; // walk to the correct sorted position -- this walk is the O(n) cost
        }
        node.next = current.next;
        current.next = node;
    }

    int poll() {
        if (head == null) throw new IllegalStateException("Priority queue is empty");
        int value = head.value;
        head = head.next;
        return value;
    }
}

class DNode {
    int value;
    DNode next;
    DNode prev;

    DNode(int value) {
        this.value = value;
    }
}

class Deque {
    private DNode head;
    private DNode tail;

    void addFirst(int value) {
        DNode node = new DNode(value);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }

    void addLast(int value) {
        DNode node = new DNode(value);
        if (tail == null) {
            head = tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
    }

    int removeFirst() {
        if (head == null) throw new IllegalStateException("Deque is empty");
        int value = head.value;
        head = head.next;
        if (head == null) tail = null;
        else head.prev = null;
        return value;
    }

    int removeLast() {
        if (tail == null) throw new IllegalStateException("Deque is empty");
        int value = tail.value;
        tail = tail.prev;
        if (tail == null) head = null;
        else tail.next = null;
        return value;
    }
}
