public class QueueDemo {

    public static void main(String[] args) {
        LinkedQueue queue = new LinkedQueue();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);

        System.out.println("peek() = " + queue.peek());
        System.out.println("dequeue() = " + queue.dequeue());
        System.out.println("dequeue() = " + queue.dequeue());
        System.out.println("isEmpty() = " + queue.isEmpty());
        System.out.println("dequeue() = " + queue.dequeue());
        System.out.println("isEmpty() = " + queue.isEmpty());

        // exercises the "queue became empty, tail must be cleared" edge case
        queue.enqueue(99);
        System.out.println("after re-enqueue, peek() = " + queue.peek());

        System.out.println();
        System.out.println("Circular queue, capacity 3 -- forcing the indices to wrap around:");
        CircularQueue ring = new CircularQueue(3);
        ring.enqueue(1);
        ring.enqueue(2);
        ring.enqueue(3);
        System.out.println("dequeue() = " + ring.dequeue()); // frees slot 0
        System.out.println("dequeue() = " + ring.dequeue()); // frees slot 1
        ring.enqueue(4); // reuses slot 0 -- rear wrapped around past the end of the array
        ring.enqueue(5); // reuses slot 1
        System.out.println("dequeue() = " + ring.dequeue()); // 3
        System.out.println("dequeue() = " + ring.dequeue()); // 4
        System.out.println("dequeue() = " + ring.dequeue()); // 5

        try {
            ring.dequeue();
        } catch (IllegalStateException e) {
            System.out.println("dequeue on empty queue correctly threw: " + e.getMessage());
        }
    }
}

class LinkedQueue {
    private QueueNode head;
    private QueueNode tail;

    void enqueue(int value) {
        QueueNode newNode = new QueueNode(value);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    int dequeue() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
        int value = head.value;
        head = head.next;
        if (head == null) tail = null;
        return value;
    }

    int peek() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
        return head.value;
    }

    boolean isEmpty() {
        return head == null;
    }
}

class QueueNode {
    int value;
    QueueNode next;

    QueueNode(int value) {
        this.value = value;
    }
}

class CircularQueue {
    private int[] data;
    private int front = 0;
    private int size = 0;

    CircularQueue(int capacity) {
        data = new int[capacity];
    }

    void enqueue(int value) {
        if (size == data.length) throw new IllegalStateException("Queue is full");
        int rear = (front + size) % data.length;
        data[rear] = value;
        size++;
    }

    int dequeue() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
        int value = data[front];
        front = (front + 1) % data.length;
        size--;
        return value;
    }

    int peek() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
        return data[front];
    }

    boolean isEmpty() {
        return size == 0;
    }
}
