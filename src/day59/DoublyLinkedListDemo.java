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
}
