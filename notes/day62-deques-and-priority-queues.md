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

## Naive implementations, and why they fall short

There are two obvious ways to build one from structures already covered, and each pays O(n) somewhere:

- **Unsorted list** — `insert` is O(1) (just append), but `poll` has to scan every element to find the smallest, O(n).
- **Sorted list** — `poll` is O(1) (the smallest is always at the front), but `insert` has to walk the list to find the right position, O(n).

```java
class SortedListPriorityQueue {
    private QNode head; // kept sorted ascending, so the smallest value is always at the head

    void insert(int value) {
        QNode node = new QNode(value);
        if (head == null || value < head.value) {
            node.next = head;
            head = node;
            return;
        }
        QNode current = head;
        while (current.next != null && current.next.value <= value) {
            current = current.next; // walk to the correct sorted position -- this walk is the O(n) cost
        }
        node.next = current.next;
        current.next = node;
    }

    int poll() {
        if (head == null) throw new IllegalStateException("Priority queue is empty");
        int value = head.value;
        head = head.next;
        return value;
    }
}
```

Both are correct; neither is fast at *both* operations. For a priority queue that gets many inserts and many polls, that O(n) per operation adds up to O(n²) overall (Day 51).

## The fix: a binary heap

A **binary heap** (Day 57 built one inside an array for heap sort; Day 66 builds it as a standalone data structure) gets `insert` and `poll` both to O(log n) by keeping elements only *partially* ordered — enough to always know where the smallest is, without paying to keep everything fully sorted. This is what Java's `java.util.PriorityQueue` uses internally:

```java
PriorityQueue<Integer> pq = new PriorityQueue<>(); // min-heap by default
pq.offer(5);
pq.offer(1);
pq.offer(3);
System.out.println(pq.poll()); // 1 -- always the smallest, regardless of insertion order
System.out.println(pq.poll()); // 3
```

To flip it into a max-priority queue, pass a reversed `Comparator` (Day 26): `new PriorityQueue<>(Comparator.reverseOrder())`.

## Choosing the right structure

- **Need only the most recent item?** Stack (Day 60).
- **Need items in arrival order?** Queue (Day 61).
- **Need to add or remove at either end?** Deque.
- **Need the smallest/largest item next, regardless of arrival order?** Priority queue.
