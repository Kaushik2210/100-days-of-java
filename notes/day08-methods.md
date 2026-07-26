# Day 8: Methods & Method Overloading

Today is about breaking code into reusable pieces: methods.

## Why methods

Without methods, any logic you want to reuse has to be copy-pasted everywhere
it's needed. Methods let you name a chunk of behavior once and call it as
many times as you want. They also make code easier to read — a well-named
method tells you *what* happens without forcing you to read *how*.

## Anatomy of a method

```java
public static int add(int a, int b) {
    return a + b;
}
```

- `public` — access modifier (who can call this method)
- `static` — belongs to the class itself, not to an instance (more on this
  when we get to OOP)
- `int` — the return type: what kind of value this method hands back
- `add` — the method name
- `(int a, int b)` — the parameter list: the inputs the method needs
- `return a + b;` — sends a value back to the caller and ends the method

## `void` methods

Not every method needs to return something. A method that just performs an
action (like printing) uses `void` as its return type, meaning "returns
nothing":

```java
public static void greet(String name) {
    System.out.println("Hello, " + name + "!");
}
```

You can still use a bare `return;` inside a `void` method to exit early —
it just can't return a value.

## Parameters vs. arguments

These two words get used interchangeably but technically mean different
things:

- **Parameters** are the variables listed in the method's definition
  (`int a, int b` above).
- **Arguments** are the actual values you pass in when you call the method
  (`add(3, 5)` — here `3` and `5` are the arguments).

## Pass-by-value

Java is strictly pass-by-value. When you pass a variable into a method,
the method gets a *copy* of the value:

```java
public static void increment(int n) {
    n = n + 1;
}

int x = 5;
increment(x);
System.out.println(x); // still 5 — the method changed its own copy
```

This trips people up most with objects, because the "value" being copied
for an object variable is a reference (an address), not the object itself.
So the method can't replace the caller's object, but it *can* still call
mutating methods on the object the reference points to, and the caller
will see those changes. We'll see concrete examples of this once arrays
and objects are back in play together with methods.

## Method calls end at `return`

As soon as `return` executes, the method exits immediately — any code
after it in the same execution path never runs. This is useful for early
exits:

```java
public static boolean isValidAge(int age) {
    if (age < 0) {
        return false;
    }
    return age <= 150;
}
```

Run `src/day08/MethodsDemo.java` to see basic methods with parameters,
return values, and a pass-by-value demonstration.
