public class CustomExceptionsDemo {

    public static void main(String[] args) {
        BankAccount account = new BankAccount(100.0);
        try {
            account.withdraw(500.0);
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }

        try {
            new ReportService().generate();
        } catch (ReportGenerationException e) {
            System.out.println(e.getMessage());
            System.out.println("Caused by: " + e.getCause()); // the original NumberFormatException
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

class ReportGenerationException extends RuntimeException {
    ReportGenerationException(String message, Throwable cause) {
        super(message, cause); // preserves the original exception as the cause
    }
}

class ReportService {
    void generate() {
        try {
            parseData();
        } catch (NumberFormatException e) {
            throw new ReportGenerationException("Failed to generate report", e); // wrap, don't discard
        }
    }

    private void parseData() {
        Integer.parseInt("not-a-number"); // throws NumberFormatException
    }
}
