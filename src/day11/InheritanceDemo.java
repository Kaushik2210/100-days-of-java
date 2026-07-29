public class InheritanceDemo {

    public static void main(String[] args) {
        Cat cat = new Cat("Whiskers", "Tabby");
        cat.eat();  // inherited from Animal
        cat.meow(); // defined in Cat

        System.out.println(cat.name + " is a " + cat.breed);
    }
}

class Animal {
    String name;

    public Animal(String name) {
        this.name = name;
    }

    void eat() {
        System.out.println(name + " is eating.");
    }
}

class Cat extends Animal {
    String breed;

    public Cat(String name, String breed) {
        super(name); // calls Animal(String name)
        this.breed = breed;
    }

    void meow() {
        System.out.println(name + " says meow!");
    }
}
