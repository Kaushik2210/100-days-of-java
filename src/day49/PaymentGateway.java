public interface PaymentGateway {
    boolean charge(String cardNumber, double amount);
}
