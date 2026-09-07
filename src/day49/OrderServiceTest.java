import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    @Test
    void placesOrderWhenGatewayApprovesCharge() {
        PaymentGateway mockGateway = mock(PaymentGateway.class); // a fake PaymentGateway, no real network call

        when(mockGateway.charge("4111-1111-1111-1111", 50.0)).thenReturn(true); // program its behavior

        OrderService service = new OrderService(mockGateway);
        boolean result = service.placeOrder("4111-1111-1111-1111", 50.0);

        assertTrue(result);
        verify(mockGateway).charge("4111-1111-1111-1111", 50.0); // asserts the mock was actually called this way
    }
}
