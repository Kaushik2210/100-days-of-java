public class ConstructorsDemo {

    public static void main(String[] args) {
        // fields are set at creation time, in one step
        Dog myDog = new Dog("Rex", 3);
        Dog anotherDog = new Dog("Bella", 5);

        myDog.bark();
        anotherDog.bark();

        System.out.println(myDog.name + " is " + myDog.age + " years old");
        System.out.println(anotherDog.name + " is " + anotherDog.age + " years old");
    }
}

class Dog {
    String name;
    int age;

    public Dog(String name, int age) {
        this.name = name;
        this.age = age;
    }

    void bark() {
        System.out.println(name + " says woof!");
    }
}
