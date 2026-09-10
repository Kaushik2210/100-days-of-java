# Day 52: Recursion Fundamentals & the Recursion Tree

A recursive method is one that calls itself, solving a problem by breaking it into a smaller version of the same problem. Day 42 already covered the mechanism that makes this possible: every method call pushes a new stack frame, holding that call's own local variables and parameters — recursion is simply many stack frames of the same method, each working on a smaller piece of the problem.

## Base case and recursive case

Every correct recursive method needs two parts:

- **Base case** — the simplest input, answered directly with no further recursive call. Without one, recursion never stops.
- **Recursive case** — breaks the problem into a smaller version of itself, and combines the smaller result into the answer for the current call.

```java
int factorial(int n) {
    if (n <= 1) return 1;       // base case -- stops the recursion
    return n * factorial(n - 1); // recursive case -- smaller problem, same shape
}
```

`factorial(4)` calls `factorial(3)`, which calls `factorial(2)`, which calls `factorial(1)` — the base case — which returns `1` back up the chain: `2 * 1`, then `3 * 2`, then `4 * 6`, giving `24`. Each of those calls is a genuinely separate stack frame (Day 42), with its own copy of `n`, alive only until that specific call returns.

## Tracing the call stack

```java
int factorialTraced(int n, int depth) {
    System.out.println("  ".repeat(depth) + "factorial(" + n + ") called");
    if (n <= 1) {
        System.out.println("  ".repeat(depth) + "-> base case, returning 1");
        return 1;
    }
    int result = n * factorialTraced(n - 1, depth + 1);
    System.out.println("  ".repeat(depth) + "-> factorial(" + n + ") returns " + result);
    return result;
}
```

Printing on the way *down* (before the recursive call) and again on the way *back up* (after it returns) makes the stack's actual shape visible: every call finishes its "on the way up" work only after every call it made has already fully returned — exactly the last-in-first-out order Day 42's stack frames guarantee.
