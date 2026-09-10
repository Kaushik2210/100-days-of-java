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

## The recursion tree: when a method calls itself more than once

`factorial` makes exactly one recursive call per invocation, so its calls form a straight line. Many recursive problems make *multiple* recursive calls per invocation, forming a branching tree instead — and that shape has real performance consequences.

```java
int fibonacci(int n) {
    if (n <= 1) return n;                          // base case
    return fibonacci(n - 1) + fibonacci(n - 2);     // two recursive calls -- branches the tree
}
```

`fibonacci(5)` calls `fibonacci(4)` and `fibonacci(3)`; `fibonacci(4)` itself calls `fibonacci(3)` and `fibonacci(2)` — so `fibonacci(3)` gets computed **twice**, independently, from scratch. Drawing this out as a tree (each call as a node, branching into its two recursive calls) makes the wasted, repeated work visually obvious in a way the code alone doesn't. This naive version is O(2ⁿ) (Day 51) precisely because of that repeated work — Day 76's memoization fixes exactly this by remembering results already computed.

## Recursion and the call stack's limits

Every recursive call is a real stack frame (Day 42), so a recursion that goes too deep — or, worse, one missing its base case entirely — throws `StackOverflowError`, exactly as Day 42's `recurseForever` example did. Recursion depth is bounded by the thread's stack size, which is why deeply recursive algorithms on very large inputs sometimes need to be rewritten iteratively (with an explicit stack, Day 60) instead.

## Tail recursion (and why it doesn't help in Java)

A **tail-recursive** call is one where the recursive call is the very last operation in the method, with nothing left to do after it returns — many languages detect this pattern and reuse the current stack frame instead of pushing a new one, making tail recursion run in constant stack space. `factorial` above is *not* tail-recursive (it still has to multiply by `n` after the recursive call returns), but a rewritten version can be:

```java
int factorialTailRecursive(int n, int accumulator) {
    if (n <= 1) return accumulator;
    return factorialTailRecursive(n - 1, n * accumulator); // nothing left to do after this returns
}
```

Even so, standard Java (unlike some other JVM languages) performs **no tail-call optimization** — this version still pushes a full stack frame per call and can still overflow on deep recursion. Recognizing tail recursion is worth knowing conceptually, but in Java, a genuinely deep computation should be written as a loop rather than relying on the compiler to flatten the recursion for you.
