public class ClassesDemo {

    public static void main(String[] args) {
        // creating a Dog object
        Dog myDog = new Dog();
        myDog.name = "Rex";
        myDog.age = 3;
        myDog.bark();

        // a second, independent Dog object
        Dog anotherDog = new Dog();
        anotherDog.name = "Bella";
        anotherDog.age = 5;
        anotherDog.bark();

        // each object has its own copy of the fields
        System.out.println(myDog.name + " is " + myDog.age + " years old");
        System.out.println(anotherDog.name + " is " + anotherDog.age + " years old");
    }
}

class Dog {
    String name;
    int age;

    void bark() {
        System.out.println(name + " says woof!");
    }
}
