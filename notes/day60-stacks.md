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

## Linked-list-based implementation

A singly linked list (Day 58) is a natural fit for a stack too — push and pop only ever touch the head, both O(1), and the stack never has a fixed capacity to overflow (it simply keeps allocating nodes as needed, bounded only by available memory).

```java
class LinkedStack {
    private StackNode top; // the head of the list doubles as the top of the stack

    void push(int value) {
        StackNode newNode = new StackNode(value);
        newNode.next = top; // new node points at the old top...
        top = newNode;       // ...and becomes the new top -- exactly Day 58's addFirst
    }

    int pop() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        int value = top.value;
        top = top.next; // drop the old top, exposing the next node
        return value;
    }

    int peek() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        return top.value;
    }

    boolean isEmpty() {
        return top == null;
    }
}

class StackNode {
    int value;
    StackNode next;

    StackNode(int value) {
        this.value = value;
    }
}
```

## Array-based vs linked-list-based

- **Array-based** — better cache locality (contiguous memory, Day 43/56), less per-element overhead (no `next` pointer stored), but either has a fixed capacity or needs periodic resizing.
- **Linked-list-based** — never needs resizing and never "overflows" short of running out of heap memory, but each node carries pointer overhead and is scattered across the heap rather than contiguous.

Java's own `java.util.Stack` (legacy, array-based, predates the Collections Framework) and `ArrayDeque`/`LinkedList` used as a `Deque` (Day 62, modern, either backing) mirror exactly this choice.

## A real use case: checking balanced parentheses

A classic stack application: verify that every opening bracket in a string has a matching, correctly-nested closing bracket. Push every opener; on a closer, the top of the stack must be its matching opener — if it isn't, or the stack is empty when a closer arrives, the string is unbalanced.

```java
boolean isBalanced(String s) {
    LinkedStack stack = new LinkedStack();
    for (char c : s.toCharArray()) {
        if (c == '(' || c == '[' || c == '{') {
            stack.push(c);
        } else if (c == ')' || c == ']' || c == '}') {
            if (stack.isEmpty()) return false; // closer with nothing open -- unbalanced
            char opener = (char) stack.pop();
            if (!matches(opener, c)) return false; // wrong opener for this closer
        }
    }
    return stack.isEmpty(); // unbalanced if anything was left open
}

boolean matches(char opener, char closer) {
    return (opener == '(' && closer == ')')
        || (opener == '[' && closer == ']')
        || (opener == '{' && closer == '}');
}
```

This same pattern — push on one kind of event, pop-and-check on a matching one — also underlies undo/redo stacks, the JVM's own call stack unwinding on exceptions (Day 21), and browser back-button history.
