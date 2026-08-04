public class ObjectMethodsDemo {

    public static void main(String[] args) {
        Person p = new Person("Asha", 30);
        System.out.println(p); // implicitly calls p.toString()

        Raw r = new Raw();
        System.out.println(r); // default Object.toString() -- ClassName@hashcode
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
}

class Raw {
    // no toString() override -- uses Object's default
}
