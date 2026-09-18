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

        System.out.println();
        LinkedStack linkedStack = new LinkedStack();
        linkedStack.push(10);
        linkedStack.push(20);
        linkedStack.push(30);
        System.out.println("linked pop() = " + linkedStack.pop());
        System.out.println("linked peek() = " + linkedStack.peek());

        System.out.println();
        String[] tests = {"({[]})", "([)]", "(()", "", "{[()()]}"};
        for (String test : tests) {
            System.out.println("isBalanced(\"" + test + "\") = " + isBalanced(test));
        }
    }

    static boolean isBalanced(String s) {
        LinkedStack stack = new LinkedStack();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) return false;
                char opener = (char) stack.pop();
                if (!matches(opener, c)) return false;
            }
        }
        return stack.isEmpty();
    }

    static boolean matches(char opener, char closer) {
        return (opener == '(' && closer == ')')
            || (opener == '[' && closer == ']')
            || (opener == '{' && closer == '}');
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

class LinkedStack {
    private StackNode top;

    void push(int value) {
        StackNode newNode = new StackNode(value);
        newNode.next = top;
        top = newNode;
    }

    int pop() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        int value = top.value;
        top = top.next;
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
