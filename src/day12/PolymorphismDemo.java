public class PolymorphismDemo {

    public static void main(String[] args) {
        Animal[] animals = { new Cat("Whiskers"), new Dog("Rex") };

        // same call, different behavior depending on the actual object
        for (Animal a : animals) {
            a.makeSound();
        }

        // overload resolution uses the DECLARED type, not the runtime type
        Animal declaredAsAnimal = new Cat("Whiskers");
        greet(declaredAsAnimal); // resolves to greet(Animal) at compile time

        Cat declaredAsCat = new Cat("Whiskers");
        greet(declaredAsCat); // resolves to greet(Cat) at compile time

        // upcast: Cat -> Animal, always safe, no cast operator needed
        Animal upcast = new Cat("Mittens");

        // downcast: Animal -> Cat, needs an explicit cast and an instanceof check first
        if (upcast instanceof Cat) {
            Cat pounced = (Cat) upcast;
            pounced.pounce();
        }

        // guard prevents a ClassCastException for objects that aren't really a Cat
        for (Animal a : animals) {
            if (a instanceof Cat) {
                ((Cat) a).pounce();
            } else {
                System.out.println(a.name + " can't pounce");
            }
        }
    }

    static void greet(Animal a) {
        System.out.println("Hello, animal");
    }

    static void greet(Cat c) {
        System.out.println("Hello, cat");
    }
}

class Animal {
    String name;

    public Animal(String name) {
        this.name = name;
    }

    void makeSound() {
        System.out.println(name + " makes a generic animal sound");
    }
}

class Cat extends Animal {
    public Cat(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " says meow");
    }

    void pounce() {
        System.out.println(name + " pounces!");
    }
}

class Dog extends Animal {
    public Dog(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " says woof");
    }
}
