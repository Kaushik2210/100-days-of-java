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

## equals()

`Object`'s default `equals()` does exactly what `==` does for objects: compares references, so two objects are only "equal" if they're literally the same object in memory.

```java
Person a = new Person("Asha", 30);
Person b = new Person("Asha", 30);
System.out.println(a.equals(b)); // false -- default equals() is reference equality
```

That's usually not what you want. If two `Person` objects represent the same person, you probably want `equals()` to compare their *data*, not their identity. Override it to define logical equality:

```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;              // same reference -- trivially equal
    if (!(obj instanceof Person other)) return false; // different type, or null -- not equal
    return age == other.age && name.equals(other.name);
}
```

## hashCode() and the equals/hashCode contract

Every object also has a `hashCode()`, an `int` used by hash-based collections (`HashMap`, `HashSet`) to bucket objects for fast lookup. Java enforces one hard rule: **if two objects are equal according to `equals()`, they must return the same `hashCode()`.** The reverse isn't required — unequal objects are allowed to share a hash code (a "collision"), just not the other way around.

```java
@Override
public int hashCode() {
    return java.util.Objects.hash(name, age); // combines both fields the same way equals() uses them
}
```

Breaking this contract — overriding `equals()` without also overriding `hashCode()` — is one of the most common Java bugs. A `HashSet` or `HashMap` key that seems "equal" by your own logic can behave as if it isn't present at all, because the collection looks in the wrong bucket first.

```java
Set<Person> people = new HashSet<>();
people.add(new Person("Asha", 30));
System.out.println(people.contains(new Person("Asha", 30)));
// true only if both equals() AND hashCode() are overridden consistently;
// with only equals() overridden, this can incorrectly print false
```

**Rule of thumb**: always override `equals()` and `hashCode()` together, never just one, and base both on the same set of fields.
