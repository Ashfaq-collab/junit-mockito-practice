package com.example.testing_demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService();
    }

    @ParameterizedTest
    @CsvSource({
            "2, 3, 5",
            "10, 20, 30",
            "-5, 3, -2",
            "0, 0, 0"
    })
    void shouldAddNumbers(int a, int b, int expected) {

        int actual = calculatorService.add(a, b);

        assertEquals(expected, actual);
    }

    @Test
    void shouldSubtractTwoNumbers() {
        int result = calculatorService.subtract(10, 5);

        assertEquals(5, result);
    }

    @Test
    void shouldMultiplyTwoNumbers() {
        int result = calculatorService.multiply(4, 5);

        assertEquals(20, result);
    }

    @Test
    void shouldThrowExceptionWhenDividingByZero() {
        assertEquals(5, calculatorService.divide(20, 4));
        assertThrows(
                IllegalArgumentException.class,
                () -> calculatorService.divide(10, 0)
        );
    }
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 100})
    void shouldReturnTrueForPositiveNumbers(int number) {

        assertTrue(calculatorService.isPositive(number));
    }
}