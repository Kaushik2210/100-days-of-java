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

## Removing a node in O(1) — the real advantage of `prev`

Given a direct reference to a node (not its position, an actual `DNode` reference), a doubly linked list can unlink it without walking the list at all — `prev` already tells you who to re-point.

```java
void removeNode(DNode node) {
    if (node.prev != null) {
        node.prev.next = node.next; // predecessor skips over `node`
    } else {
        head = node.next; // `node` was the head -- the next node becomes the new head
    }

    if (node.next != null) {
        node.next.prev = node.prev; // successor skips back over `node`
    } else {
        tail = node.prev; // `node` was the tail -- update tail too
    }
}
```

Contrast this with Day 58's singly linked `delete`, which had to walk from the head just to *find* the predecessor before it could unlink anything — the extra `prev` pointer trades a small amount of memory per node for removing that search entirely.

## Circular linked lists: no end at all

A circular linked list connects the last node's `next` back to the first node (`head`), instead of leaving it `null` — there is no "end" to walk off of, only a loop. This is useful for **round-robin** scenarios: a rotation that should cycle back to the start indefinitely, like turn order in a game, or scheduling CPU time slices across processes in a fixed cycle.

```java
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
            newNode.next = head; // a single node points to itself
            return;
        }
        tail.next = newNode;
        newNode.next = head; // always loop back to the head
        tail = newNode;
    }
}
```

Because there's no `null` to stop at, traversal code must track *how many* nodes it has visited instead of checking for the end — looping forever by accident is the classic circular-list bug.

## A doubly linked list can be circular too

The two ideas combine: a **circular doubly linked list** connects `tail.next` to `head` *and* `head.prev` to `tail`, giving O(1) access to both ends *and* full bidirectional traversal, with no special-casing for "am I at the boundary" anywhere — every node genuinely looks the same. This is the structure behind Day 62's `Deque` and is a common choice for LRU-cache implementations (Day 97).
