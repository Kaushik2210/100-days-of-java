public class InheritanceDemo {

    public static void main(String[] args) {
        Cat cat = new Cat("Whiskers", "Tabby");
        cat.eat();  // inherited from Animal
        cat.meow(); // defined in Cat

        System.out.println(cat.name + " is a " + cat.breed);

        // method overriding: Cat replaces Animal's makeSound()
        cat.makeSound();

        // species() is final in Animal, so Cat cannot override it
        System.out.println(cat.name + " species: " + cat.species());
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

    void makeSound() {
        System.out.println("Some generic animal sound");
    }

    // final: subclasses cannot change what "species" means for an Animal
    final String species() {
        return "Animal";
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

    @Override
    void makeSound() {
        super.makeSound(); // still prints the generic message first
        System.out.println("...but really, meow!");
    }
}
