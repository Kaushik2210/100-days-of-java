import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ObjectMethodsDemo {

    public static void main(String[] args) {
        Person p = new Person("Asha", 30);
        System.out.println(p); // implicitly calls p.toString()

        Raw r = new Raw();
        System.out.println(r); // default Object.toString() -- ClassName@hashcode

        Person a = new Person("Asha", 30);
        Person b = new Person("Asha", 30);
        System.out.println("a.equals(b) = " + a.equals(b)); // true -- logical equality, not reference

        Set<Person> people = new HashSet<>();
        people.add(a);
        System.out.println("people.contains(b) = " + people.contains(b)); // true because hashCode() is consistent too
    }
}

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Person other)) return false;
        return age == other.age && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}

class Raw {
    // no toString() override -- uses Object's default
}
