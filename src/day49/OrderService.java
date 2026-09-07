public class OrderService {
    private final PaymentGateway gateway;

    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway; // dependency injected in, not created internally -- this is what makes mocking possible
    }

    public boolean placeOrder(String cardNumber, double amount) {
        if (amount <= 0) return false;
        return gateway.charge(cardNumber, amount);
    }
}
