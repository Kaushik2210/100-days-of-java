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

## Searching

Since there's no index-based access at all (no array underneath), finding a value means walking the chain from the head, exactly like linear search (Day 53) over an array — O(n) either way.

```java
boolean contains(int value) {
    Node current = head;
    while (current != null) {
        if (current.value == value) return true;
        current = current.next;
    }
    return false;
}
```

## Deleting a value

Deleting requires finding the node *before* the target, then re-pointing its `next` to skip over the target entirely — the target node becomes unreferenced and eligible for garbage collection (Day 43), with no explicit "free" needed.

```java
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
        current.next = current.next.next; // skip over the target node, unlinking it from the chain
    }
}
```

Deleting the head is a special case precisely because there's no "previous" node to update — everything else in the list is reached by following `next` from somewhere, but the head itself is reached from the list's own `head` field.

## Reversing the list

Reversing in place means walking the list once, re-pointing every node's `next` to point *backward* instead of forward, tracking the previous node as you go.

```java
void reverse() {
    Node previous = null;
    Node current = head;
    while (current != null) {
        Node next = current.next; // save the forward link before overwriting it
        current.next = previous;  // reverse this node's pointer
        previous = current;       // advance previous
        current = next;           // advance current, using the saved reference
    }
    head = previous; // the old last node is now the new head
}
```

This is O(n) time and O(1) extra space — no new nodes are created, only pointers rewired.
