# Day 62: Deques & Priority Queues

Day 60's stack only touches one end and Day 61's queue adds at one end and removes from the other. Two generalizations break each of those restrictions in a different direction: the **deque** allows adding and removing at *both* ends, and the **priority queue** stops caring about arrival order altogether.

## Deque: a double-ended queue

A deque (pronounced "deck") supports `addFirst`, `addLast`, `removeFirst`, and `removeLast`, all O(1). It's a strict superset of both structures already built: use only `addLast`/`removeFirst` and it behaves as a queue; use only `addFirst`/`removeFirst` (or the last-end equivalents) and it behaves as a stack.

The right backing structure is the doubly linked list from Day 59 — `prev` and `next` pointers plus tracked `head` and `tail` give O(1) at both ends, which a singly linked list can't do for `removeLast` (it would have to walk to find the second-to-last node).

```java
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
        if (head == null) tail = null;   // deque became empty
        else head.prev = null;
        return value;
    }

    int removeLast() {
        if (tail == null) throw new IllegalStateException("Deque is empty");
        int value = tail.value;
        tail = tail.prev;
        if (tail == null) head = null;   // deque became empty
        else tail.next = null;
        return value;
    }
}
```

In Java, `ArrayDeque` and `LinkedList` both implement the `Deque` interface, and `ArrayDeque` is the usual recommendation for both stack and queue use (Day 60/61) over the legacy `Stack` class.

## Priority queue: highest priority out first, not oldest

A priority queue drops FIFO ordering. Each element has a priority, and `dequeue` (often called `poll`) always returns the element with the *highest* priority — or the lowest value, for a "min" priority queue — no matter when it arrived. Hospital triage, operating-system task scheduling, and Dijkstra's shortest-path algorithm (Day 81) all need this.
