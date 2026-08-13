import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class IteratorsAndOrderingDemo {

    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("Asha", "Kiran", "Ravi"));
        Iterator<String> it = names.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }

        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        Iterator<Integer> numIt = numbers.iterator();
        while (numIt.hasNext()) {
            if (numIt.next() % 2 == 0) {
                numIt.remove(); // safe -- the iterator updates its own bookkeeping
            }
        }
        System.out.println(numbers); // [1, 3, 5]

        List<Person> people = new ArrayList<>(List.of(
            new Person("Kiran", 25),
            new Person("Asha", 30),
            new Person("Ravi", 25)));

        Collections.sort(people); // uses Person.compareTo() -- natural order: age ascending
        System.out.println(people);

        Comparator<Person> byName = (a, b) -> a.name.compareTo(b.name);
        people.sort(byName);
        System.out.println(people);

        Comparator<Person> byAgeThenName = Comparator
            .comparingInt((Person p) -> p.age)
            .thenComparing(p -> p.name);
        people.sort(byAgeThenName.reversed());
        System.out.println(people);
    }
}

class Person implements Comparable<Person> {
    String name;
    int age;

    Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Person other) {
        return Integer.compare(this.age, other.age); // natural order: youngest first
    }

    @Override
    public String toString() {
        return name + "(" + age + ")";
    }
}
