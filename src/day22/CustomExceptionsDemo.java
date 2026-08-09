public class CustomExceptionsDemo {

    public static void main(String[] args) {
        BankAccount account = new BankAccount(100.0);
        try {
            account.withdraw(500.0);
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }
}

class InsufficientFundsException extends Exception { // checked: caller must handle it
    InsufficientFundsException(String message) {
        super(message);
    }
}

class BankAccount {
    private double balance;

    BankAccount(double balance) {
        this.balance = balance;
    }

    void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException(
                "Cannot withdraw " + amount + "; balance is only " + balance);
        }
        balance -= amount;
    }
}
