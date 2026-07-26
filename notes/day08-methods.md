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

## Method overloading

Java lets you define multiple methods with the **same name** as long as
their **parameter lists differ** — either in number of parameters, or in
parameter types. This is called overloading.

```java
public static int multiply(int a, int b) {
    return a * b;
}

public static double multiply(double a, double b) {
    return a * b;
}

public static int multiply(int a, int b, int c) {
    return a * b * c;
}
```

At compile time, the compiler looks at the arguments you pass and figures
out which overload matches. This is called **compile-time (static)
polymorphism** — the decision of which method body runs is made before the
program even executes, based purely on the argument types.

### What counts as a different signature

A method's **signature** is its name plus its parameter types (in order).
Overloads must have different signatures. These are all valid overloads of
each other because their parameter lists differ:

```java
void show(int a)
void show(int a, int b)
void show(String a)
void show(int a, String b)
void show(String a, int b)   // order matters — different from the one above
```

### What does NOT count

The **return type alone** cannot distinguish two methods. This does not
compile, because the parameter list is identical:

```java
int show(int a) { ... }
double show(int a) { ... }   // compile error: duplicate method
```

### Widening and overload resolution

If there's no exact match for the argument types, Java will widen a
primitive argument to fit an available overload (e.g. an `int` argument can
widen to match a `double` parameter) rather than fail to compile. If more
than one overload could apply, Java picks the most specific match it can
find.

Run `src/day08/MethodsDemo.java` for the overloading examples, which
show `multiply` being called with different argument counts and types.

## Varargs: variable-length argument lists

Sometimes you don't know in advance how many arguments a caller will pass.
Instead of writing an overload for every possible count, Java lets you
declare a **varargs** parameter using `...`:

```java
public static int sum(int... numbers) {
    int total = 0;
    for (int n : numbers) {
        total += n;
    }
    return total;
}
```

Inside the method, `numbers` is just an `int[]` — varargs are really syntax
sugar for an array parameter. You can call it with any number of
arguments, including zero:

```java
sum();           // numbers = {}
sum(5);          // numbers = {5}
sum(1, 2, 3, 4); // numbers = {1, 2, 3, 4}
```

You can also pass an actual array directly instead of listing individual
values — Java accepts either form.

### Rules for varargs

- A method can have **at most one** varargs parameter.
- If there are other parameters, the varargs parameter **must come last**:

```java
public static void logMessage(String prefix, int... codes) { ... }
```

- Varargs interacts with overloading: if both an exact-match overload and
  a varargs overload could apply, Java prefers the more specific
  (non-varargs) match first.

Run `src/day08/MethodsDemo.java` for a `sum` varargs example called with
zero, one, and several arguments.
