public class EncapsulationDemo {

    public static void main(String[] args) {
        BankAccount account = new BankAccount(100.0);
        account.deposit(50.0);
        System.out.println("Balance: " + account.getBalance());

        try {
            account.deposit(-10.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }

        Point origin = new Point(0.0, 0.0);
        Point moved = origin.translated(3.0, 4.0); // origin itself is untouched
        System.out.println("origin=(" + origin.getX() + ", " + origin.getY() + ")");
        System.out.println("moved=(" + moved.getX() + ", " + moved.getY() + ")");
    }
}

class BankAccount {
    private double balance; // outside code cannot touch this directly

    BankAccount(double openingBalance) {
        this.balance = openingBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        balance += amount;
    }
}

final class Point {
    private final double x;
    private final double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    Point translated(double dx, double dy) { // returns a new Point instead of mutating this one
        return new Point(x + dx, y + dy);
    }
}
