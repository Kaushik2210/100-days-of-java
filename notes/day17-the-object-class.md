# Day 17: The Object Class — equals(), hashCode(), toString()

Every class in Java, whether you write `extends` or not, implicitly extends `java.lang.Object`. That means every object you ever create already comes with a handful of inherited methods — `toString()`, `equals()`, `hashCode()`, `getClass()`, and a few others — whether or not you asked for them. Day 17 is about the three you override most often.

## Every class extends Object

```java
class Person {
    // implicitly: class Person extends Object
}
```

This is why you can call `person.toString()` or `person.equals(other)` on any object at all, even one from a class that never mentions `Object` — the methods are inherited from the root of the class hierarchy.

## toString()

`Object`'s default `toString()` returns something like `Person@1b6d3586` — the class name plus the object's hash code in hex. That's rarely useful for debugging or logging, so classes commonly override it to produce a meaningful description.

```java
class Person {
    String name;
    int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{name=" + name + ", age=" + age + "}";
    }
}
```

```java
Person p = new Person("Asha", 30);
System.out.println(p); // calls p.toString() automatically -- prints Person{name=Asha, age=30}
```

Anywhere an object is concatenated with a `String`, or passed to `System.out.println`, or interpolated, Java calls `toString()` on it implicitly.
