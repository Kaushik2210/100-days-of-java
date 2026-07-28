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
