public class ConstructorsDemo {

    public static void main(String[] args) {
        // fields are set at creation time, in one step
        Dog myDog = new Dog("Rex", 3);
        Dog anotherDog = new Dog("Bella", 5);

        myDog.bark();
        anotherDog.bark();

        System.out.println(myDog.name + " is " + myDog.age + " years old");
        System.out.println(anotherDog.name + " is " + anotherDog.age + " years old");

        // constructor overloading: different ways to create a Dog
        Dog puppy = new Dog("Puppy");
        Dog stray = new Dog();

        System.out.println(puppy.name + " is " + puppy.age + " years old");
        System.out.println(stray.name + " is " + stray.age + " years old");

        // returning this for method chaining
        Pizza order = new Pizza().addCheese().addPepperoni();
        System.out.println(order.describe());
    }
}

class Dog {
    String name;
    int age;

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

    void bark() {
        System.out.println(name + " says woof!");
    }
}

class Pizza {
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
