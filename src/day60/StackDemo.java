public class StackDemo {

    public static void main(String[] args) {
        ArrayStack stack = new ArrayStack(5);
        stack.push(1);
        stack.push(2);
        stack.push(3);

        System.out.println("peek() = " + stack.peek());
        System.out.println("pop() = " + stack.pop());
        System.out.println("pop() = " + stack.pop());
        System.out.println("isEmpty() = " + stack.isEmpty());
        System.out.println("pop() = " + stack.pop());
        System.out.println("isEmpty() = " + stack.isEmpty());
    }
}

class ArrayStack {
    private int[] data;
    private int top = -1; // -1 means empty

    ArrayStack(int capacity) {
        data = new int[capacity];
    }

    void push(int value) {
        if (top == data.length - 1) throw new IllegalStateException("Stack is full");
        data[++top] = value;
    }

    int pop() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        return data[top--];
    }

    int peek() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        return data[top];
    }

    boolean isEmpty() {
        return top == -1;
    }
}
