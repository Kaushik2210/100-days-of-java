# Day 58: Custom Data Structures — Building a Singly Linked List

Day 23 used the JDK's built-in `LinkedList`. From here through Day 70, the goal shifts to *building* the data structures themselves from scratch — understanding what `LinkedList`, `HashMap`, and friends actually do underneath, by implementing the same ideas directly.

## The Node: a link in the chain

A singly linked list is a chain of **nodes**, each holding a value and a reference to the *next* node — no array, no fixed size, no shifting elements on insert like `ArrayList` (Day 23) needs.

```java
class Node {
    int value;
    Node next; // reference to the next node in the chain, or null if this is the last one

    Node(int value) {
        this.value = value;
    }
}
```

## The list itself: a head reference

The list as a whole only needs to remember one thing: the **head** — a reference to the first node. Every other node is reachable by following `next` references from there.

```java
class SinglyLinkedList {
    Node head; // null when the list is empty

    void addFirst(int value) {
        Node newNode = new Node(value);
        newNode.next = head; // the new node points at the old head...
        head = newNode;      // ...and becomes the new head itself
    }

    void addLast(int value) {
        Node newNode = new Node(value);
        if (head == null) {
            head = newNode;
            return;
        }
        Node current = head;
        while (current.next != null) { // walk to the last node
            current = current.next;
        }
        current.next = newNode; // attach the new node after it
    }

    void printAll() {
        Node current = head;
        while (current != null) {
            System.out.print(current.value + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }
}
```

`addFirst` is O(1) — it only ever touches the head. `addLast` is O(n) here, since reaching the end requires walking the whole chain (a real `LinkedList` implementation, like the JDK's, keeps a `tail` reference too, to make `addLast` O(1) as well).
