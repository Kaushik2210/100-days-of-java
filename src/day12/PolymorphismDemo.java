public class PolymorphismDemo {

    public static void main(String[] args) {
        Animal[] animals = { new Cat("Whiskers"), new Dog("Rex") };

        // same call, different behavior depending on the actual object
        for (Animal a : animals) {
            a.makeSound();
        }
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
