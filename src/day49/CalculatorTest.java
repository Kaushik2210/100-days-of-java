import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorTest {
    Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator(); // fresh instance before every test method
    }

    @Test
    void addsTwoPositiveNumbers() {
        assertEquals(5, calculator.add(2, 3)); // fails the test if the actual value differs
    }

    @Test
    void dividingByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> calculator.divide(10, 0));
    }
}
