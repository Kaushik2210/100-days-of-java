# Day 60: Stacks — Array-Based & Linked-List-Based Implementations

A stack is a **LIFO** (Last In, First Out) collection: the most recently added element is always the first one removed — like a stack of plates, where you only ever add or take from the top. Every recursive call in Days 52–57 was secretly managed by exactly this structure (Day 42's call stack), which is why understanding stacks explicitly closes the loop on how recursion actually works under the hood.

## The stack interface: push, pop, peek

- **`push(value)`** — add an element to the top.
- **`pop()`** — remove and return the top element.
- **`peek()`** — return the top element without removing it.
- **`isEmpty()`** — check whether the stack has anything on it at all.

All four operations are O(1), regardless of which underlying implementation is used, as long as it's built correctly — that O(1) guarantee is the entire point of a stack.

## Array-based implementation

A stack can be backed by a plain array (or an `ArrayList`, Day 23) with an index tracking the current top. Pushing and popping only ever touch that one end, so no shifting is ever needed — unlike inserting into the *middle* of an array (Day 23), which does require shifting.

```java
class ArrayStack {
    private int[] data;
    private int top = -1; // -1 means empty

    ArrayStack(int capacity) {
        data = new int[capacity];
    }

    void push(int value) {
        if (top == data.length - 1) throw new IllegalStateException("Stack is full");
        data[++top] = value; // increment top, then write
    }

    int pop() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        return data[top--]; // read, then decrement top
    }

    int peek() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        return data[top];
    }

    boolean isEmpty() {
        return top == -1;
    }
}
```

The fixed-size array here can overflow; a resizable version would grow the backing array (like `ArrayList.add`'s amortized O(1) growth, Day 51) instead of throwing.
