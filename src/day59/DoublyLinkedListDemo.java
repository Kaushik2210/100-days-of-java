public class DoublyLinkedListDemo {

    public static void main(String[] args) {
        DoublyLinkedList list = new DoublyLinkedList();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        System.out.print("forward:  ");
        list.printForward();

        System.out.print("backward: ");
        list.printBackward();

        list.removeNode(list.head.next); // removes the middle node (value 2) via direct reference, O(1)
        System.out.print("after removing the middle node: ");
        list.printForward();

        System.out.println();
        System.out.println("Round-robin elimination (Josephus-style) on a circular linked list:");
        CircularLinkedList ring = new CircularLinkedList();
        for (int i = 1; i <= 5; i++) ring.add(i);
        ring.eliminateEveryKth(2); // classic Josephus problem: eliminate every 2nd person until one remains
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

class DoublyLinkedList {
    DNode head;
    DNode tail;

    void addLast(int value) {
        DNode newNode = new DNode(value);
        if (head == null) {
            head = tail = newNode;
            return;
        }
        tail.next = newNode;
        newNode.prev = tail;
        tail = newNode;
    }

    void printForward() {
        DNode current = head;
        while (current != null) {
            System.out.print(current.value + " <-> ");
            current = current.next;
        }
        System.out.println("null");
    }

    void printBackward() {
        DNode current = tail;
        while (current != null) {
            System.out.print(current.value + " <-> ");
            current = current.prev;
        }
        System.out.println("null");
    }

    void removeNode(DNode node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}

class CircularNode {
    int value;
    CircularNode next;

    CircularNode(int value) {
        this.value = value;
    }
}

class CircularLinkedList {
    CircularNode head;
    CircularNode tail;

    void add(int value) {
        CircularNode newNode = new CircularNode(value);
        if (head == null) {
            head = tail = newNode;
            newNode.next = head;
            return;
        }
        tail.next = newNode;
        newNode.next = head;
        tail = newNode;
    }

    // classic Josephus problem: starting at head, count k nodes and eliminate the k-th, repeat until one remains
    void eliminateEveryKth(int k) {
        CircularNode current = head;
        CircularNode prev = tail;
        int remaining = count();

        while (remaining > 1) {
            for (int step = 1; step < k; step++) { // walk k-1 steps to land on the k-th node
                prev = current;
                current = current.next;
            }
            System.out.println("Eliminating " + current.value);
            prev.next = current.next; // unlink the eliminated node from the ring
            if (current == head) head = current.next; // keep head valid if it was just eliminated
            current = current.next;
            remaining--;
        }
        System.out.println("Survivor: " + current.value);
    }

    int count() {
        if (head == null) return 0;
        int n = 1;
        CircularNode current = head.next;
        while (current != head) {
            n++;
            current = current.next;
        }
        return n;
    }
}
