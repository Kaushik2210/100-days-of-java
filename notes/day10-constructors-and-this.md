# Day 10: Constructors & the `this` Keyword

Yesterday we built objects and then set their fields one at a time after
`new`. Today's cleaner approach: initialize fields at the moment of
creation, using a constructor.

## What a constructor is

A **constructor** is a special block that runs automatically when an
object is created with `new`. It looks like a method but has no return
type, and its name must exactly match the class name:

```java
public class Dog {
    String name;
    int age;

    public Dog(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

Now creating a `Dog` and setting its fields happens in one step:

```java
Dog myDog = new Dog("Rex", 3);
```

No more separate `myDog.name = "Rex";` lines, and no window where the
object exists in a half-initialized state.

## The default constructor

If you don't write any constructor at all, Java silently provides a
no-argument **default constructor** that does nothing but create the
object with fields at their default values (`0`, `false`, `null`, etc. —
same defaults as arrays from Day 6). This is exactly what let `new Dog()`
work on Day 9, before we'd written any constructor.

The moment you write **any** constructor yourself, that automatic default
constructor disappears. If you still want a no-argument way to create the
object, you have to write one explicitly.

## Why `this` is needed

Inside the constructor, the parameter `name` and the field `name` have the
same identifier. Without `this`, `name = name;` would just assign the
parameter to itself and leave the field untouched. `this.name` explicitly
means "the field belonging to the object being constructed," disambiguating
it from the parameter `name` that's currently shadowing it.

Run `src/day10/ConstructorsDemo.java` to see a `Dog` class with a
constructor that initializes both fields at creation time.

## Constructor overloading

Just like regular methods (Day 8), a class can have multiple constructors
as long as their parameter lists differ. This lets callers create an
object in whichever way is convenient for them:

```java
public class Dog {
    String name;
    int age;

    public Dog(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Dog(String name) {
        this.name = name;
        this.age = 0; // default age when not specified
    }

    public Dog() {
        this.name = "Unnamed";
        this.age = 0;
    }
}
```

Now all of these are valid:

```java
Dog rex = new Dog("Rex", 3);
Dog puppy = new Dog("Puppy");
Dog stray = new Dog();
```

## Constructor chaining with `this(...)`

Repeating `this.age = 0;` in two constructors above is duplicated logic.
A constructor can call **another constructor in the same class** using
`this(...)` as its very first statement, avoiding that repetition:

```java
public Dog(String name, int age) {
    this.name = name;
    this.age = age;
}

public Dog(String name) {
    this(name, 0); // delegates to the two-argument constructor
}

public Dog() {
    this("Unnamed"); // delegates to the one-argument constructor
}
```

`this(...)` must be the first line in the constructor body — Java needs to
know immediately whether this constructor is doing its own initialization
or delegating to another one first.

Run `src/day10/ConstructorsDemo.java` for the overloaded and chained
`Dog` constructors in action.

## Returning `this` for method chaining

`this` isn't limited to constructors — any instance method can reference
it too. A common pattern is having a "setter-style" method return `this`,
so calls can be chained together fluently:

```java
public class Pizza {
    private boolean cheese;
    private boolean pepperoni;

    public Pizza addCheese() {
        this.cheese = true;
        return this;
    }

    public Pizza addPepperoni() {
        this.pepperoni = true;
        return this;
    }

    public String describe() {
        return "Pizza[cheese=" + cheese + ", pepperoni=" + pepperoni + "]";
    }
}
```

Because each method returns the same object it was called on (`this`),
the calls can be chained directly:

```java
Pizza order = new Pizza().addCheese().addPepperoni();
System.out.println(order.describe());
```

Without returning `this`, each of those methods would return `void`, and
you'd have to write three separate statements instead of one fluent chain.

Run `src/day10/ConstructorsDemo.java` for a `Pizza` example that builds up
an object using chained method calls.
