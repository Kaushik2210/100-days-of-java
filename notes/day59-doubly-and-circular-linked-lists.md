# Day 59: Doubly Linked Lists & Circular Linked Lists

Day 58's singly linked list can only move forward, and deleting a node requires walking from the head to find its predecessor. Two variations fix different limitations: the **doubly linked list** adds backward links, and the **circular linked list** removes the concept of an "end" entirely.

## Doubly linked lists: a `prev` reference too

Each node holds both a `next` and a `prev` reference, letting the list be traversed in either direction and letting a node be removed in O(1) once you already have a reference to it — no need to walk from the head to find its predecessor, since the node already knows its own predecessor.

```java
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
    DNode tail; // tracked directly -- makes addLast O(1), unlike Day 58's singly linked addLast

    void addLast(int value) {
        DNode newNode = new DNode(value);
        if (head == null) {
            head = tail = newNode;
            return;
        }
        tail.next = newNode;
        newNode.prev = tail; // the new node links back to the old tail
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
```

This is exactly how the JDK's own `LinkedList` (Day 23) is implemented internally — a doubly linked list with a tracked head and tail, which is why it supports O(1) operations at both ends (`addFirst`, `addLast`, `removeFirst`, `removeLast`) and why it doubles as a `Deque`.
