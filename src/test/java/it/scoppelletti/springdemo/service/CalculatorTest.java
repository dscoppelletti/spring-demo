package it.scoppelletti.springdemo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CalculatorTest {

    @Autowired
    private Calculator calculator;

    @Test
    void divisionTest() {
        Integer result = calculator.div(5, 2).block();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result);
    }

    @Test
    void divisionByZeroTest() {
        Assertions.assertThrows(ArithmeticException.class, () ->
                calculator.div(5, 0).block());
    }
}
