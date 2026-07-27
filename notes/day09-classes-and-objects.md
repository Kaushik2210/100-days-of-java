# Day 9: Intro to OOP — Classes & Objects

Up to now everything has lived inside `main`. Today starts object-oriented
programming: modeling real things as classes and objects.

## Classes as blueprints

A **class** is a blueprint that describes what a kind of thing looks like
and what it can do. It doesn't itself hold any data — it's a template.

```java
public class Dog {
    String name;
    int age;

    void bark() {
        System.out.println(name + " says woof!");
    }
}
```

`Dog` describes that every dog has a `name` and an `age`, and can `bark()`.
No actual dog exists yet — this is just the shape.

## Objects as instances

An **object** is a concrete instance created from a class, using `new`:

```java
Dog myDog = new Dog();
myDog.name = "Rex";
myDog.age = 3;
myDog.bark(); // Rex says woof!
```

`new Dog()` allocates a real `Dog` in memory and gives you a reference to
it (`myDog`). Each object gets its own copy of the fields defined by the
class — `myDog.name` is independent of any other `Dog` object's `name`.

## Fields and instance methods

- **Fields** (also called instance variables) are the data each object
  carries — `name` and `age` above.
- **Instance methods** are behavior that operates on that data —
  `bark()` reads `this` object's `name` without needing it passed in as a
  parameter.

Unlike the `static` methods from Day 8, instance methods belong to a
*specific object*, not to the class as a whole. You can't call `bark()`
without first having a `Dog` object to call it on.

## The dot operator

`.` is used both to access a field (`myDog.name`) and to call a method on
an object (`myDog.bark()`). In both cases you're saying "look at this
specific object, then act on it."

Run `src/day09/ClassesDemo.java` to see a `Dog` class defined and a couple
of `Dog` objects created and used.
