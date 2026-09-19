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
