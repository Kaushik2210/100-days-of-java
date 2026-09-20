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
