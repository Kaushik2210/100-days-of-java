public class SinglyLinkedListDemo {

    public static void main(String[] args) {
        SinglyLinkedList list = new SinglyLinkedList();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);
        list.addFirst(0);
        list.printAll();

        System.out.println("contains(2) = " + list.contains(2));
        System.out.println("contains(99) = " + list.contains(99));

        list.delete(2);
        System.out.print("after delete(2): ");
        list.printAll();

        list.delete(0); // deleting the head
        System.out.print("after delete(0): ");
        list.printAll();

        list.reverse();
        System.out.print("after reverse(): ");
        list.printAll();
    }
}

class Node {
    int value;
    Node next; // reference to the next node in the chain, or null if this is the last one

    Node(int value) {
        this.value = value;
    }
}

class SinglyLinkedList {
    Node head; // null when the list is empty

    void addFirst(int value) {
        Node newNode = new Node(value);
        newNode.next = head;
        head = newNode;
    }

    void addLast(int value) {
        Node newNode = new Node(value);
        if (head == null) {
            head = newNode;
            return;
        }
        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = newNode;
    }

    void printAll() {
        Node current = head;
        while (current != null) {
            System.out.print(current.value + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }

    boolean contains(int value) {
        Node current = head;
        while (current != null) {
            if (current.value == value) return true;
            current = current.next;
        }
        return false;
    }

    void delete(int value) {
        if (head == null) return;

        if (head.value == value) { // special case: deleting the head itself
            head = head.next;
            return;
        }

        Node current = head;
        while (current.next != null && current.next.value != value) {
            current = current.next;
        }
        if (current.next != null) {
            current.next = current.next.next; // skip over the target node
        }
    }

    void reverse() {
        Node previous = null;
        Node current = head;
        while (current != null) {
            Node next = current.next;
            current.next = previous;
            previous = current;
            current = next;
        }
        head = previous;
    }
}
