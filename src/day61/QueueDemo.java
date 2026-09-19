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
