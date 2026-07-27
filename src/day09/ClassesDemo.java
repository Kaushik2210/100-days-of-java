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

        // encapsulation: balance is private, only reachable through methods
        Account account = new Account();
        account.deposit(100.0);
        account.deposit(-50.0); // ignored, invalid deposit
        System.out.println("Balance: " + account.getBalance());

        // object references: b points to the same Account as a
        Account a = new Account();
        a.deposit(100.0);

        Account b = a;
        b.deposit(50.0);

        System.out.println("a's balance: " + a.getBalance()); // 150.0
        System.out.println("b's balance: " + b.getBalance()); // 150.0, same object
    }
}

class Dog {
    String name;
    int age;

    void bark() {
        System.out.println(name + " says woof!");
    }
}

class Account {
    private double balance;

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
}
