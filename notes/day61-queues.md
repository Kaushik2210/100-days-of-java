# Day 61: Queues — Array, Linked List & Circular Queue

Where Day 60's stack is LIFO (last in, first out), a **queue** is **FIFO** (First In, First Out): elements leave in the same order they arrived, like a line at a ticket counter. Queues model anything where fairness by arrival order matters — task scheduling, print jobs, and, as Day 40's `BlockingQueue` showed, coordinating producer and consumer threads.

## The queue interface: enqueue, dequeue, peek

- **`enqueue(value)`** — add an element to the **back** (rear).
- **`dequeue()`** — remove and return the element at the **front**.
- **`peek()`** — return the front element without removing it.
- **`isEmpty()`** — check whether the queue has anything in it.

As with stacks, all of these should be O(1) — which requires choosing the underlying implementation carefully, since a queue touches *both ends* (add at one, remove from the other).

## Linked-list-based queue: the natural fit

A singly linked list (Day 58) with both a `head` and a `tail` reference supports exactly what a queue needs: dequeue from the head and enqueue at the tail, both O(1) — this is the reason Day 58 noted that tracking a `tail` reference makes `addLast` O(1) instead of O(n).

```java
class LinkedQueue {
    private QueueNode head; // front of the queue -- dequeue from here
    private QueueNode tail; // back of the queue -- enqueue here

    void enqueue(int value) {
        QueueNode newNode = new QueueNode(value);
        if (tail == null) {
            head = tail = newNode; // first element is both front and back
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    int dequeue() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
        int value = head.value;
        head = head.next;
        if (head == null) tail = null; // queue became empty -- clear tail too, or it would dangle
        return value;
    }

    int peek() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
        return head.value;
    }

    boolean isEmpty() {
        return head == null;
    }
}

class QueueNode {
    int value;
    QueueNode next;

    QueueNode(int value) {
        this.value = value;
    }
}
```

The `if (head == null) tail = null;` line in `dequeue` is easy to forget and a classic bug: if the last element is removed but `tail` keeps pointing at the now-detached node, the next `enqueue` would attach to a node no longer part of the list, silently losing the new element.

## Why a plain array makes a poor queue

Day 60's array-backed stack worked cleanly because every operation touched only *one* end. A queue touches both, and a naive array version runs into a real problem:

- If `dequeue` removes from index 0, every remaining element has to shift left by one to fill the gap — **O(n) per dequeue**, not O(1).
- If instead a `front` index just advances past dequeued elements (no shifting), the front of the array becomes dead, wasted space: after enough enqueue/dequeue cycles the `rear` index hits the end of the array even though most of the array is empty, and the queue reports "full" while holding only a couple of elements.

## Circular queue: reusing the wasted space

A **circular queue** (or ring buffer) fixes this by treating the array as if its end wraps around to its beginning. When `rear` reaches the last index, the next enqueue wraps back to index 0 — reusing the slots that dequeues already vacated. Modular arithmetic (`% capacity`) does the wrapping.

```java
class CircularQueue {
    private int[] data;
    private int front = 0;
    private int size = 0;

    CircularQueue(int capacity) {
        data = new int[capacity];
    }

    void enqueue(int value) {
        if (size == data.length) throw new IllegalStateException("Queue is full");
        int rear = (front + size) % data.length; // wraps around to index 0 once it passes the end
        data[rear] = value;
        size++;
    }

    int dequeue() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
        int value = data[front];
        front = (front + 1) % data.length; // advance front, wrapping around if needed
        size--;
        return value;
    }

    int peek() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
        return data[front];
    }

    boolean isEmpty() {
        return size == 0;
    }
}
```

Tracking an explicit `size` (rather than trying to infer fullness from `front`/`rear` alone) cleanly avoids the classic ambiguity where "empty" and "full" look identical when only the two indices are compared.

## Choosing between them

- **Linked-list queue** — unbounded, simple; per-node pointer overhead and scattered memory.
- **Circular array queue** — fixed capacity (or resized as needed), contiguous memory and better cache behavior (Day 43/56), no per-element pointer overhead — the common choice when a maximum size is known ahead of time, like the ring buffers used in network packet handling and audio processing.

Java's `java.util.ArrayDeque` is a resizable circular array under the hood, and is generally the recommended `Queue` implementation in modern Java over `LinkedList` for exactly these reasons.
