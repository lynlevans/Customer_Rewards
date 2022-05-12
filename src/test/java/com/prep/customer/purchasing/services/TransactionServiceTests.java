package com.prep.customer.purchasing.services;

import static org.junit.jupiter.api.Assertions.*;

import com.prep.customer.purchasing.domain.enums.Status;
import com.prep.customer.purchasing.services.impl.TransactionService;
import java.math.BigDecimal;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransactionServiceTests extends BaseTest {

    // Cost must be non-negative
    @Test
    public void testZeroCostIsValid() {
        Boolean result = TransactionService.isCostValid.apply(new BigDecimal("0.00"));
        assertTrue(result);
    }

    @Test
    public void testPositiveCostIsValid() {
        Boolean result = TransactionService.isCostValid.apply(new BigDecimal("50.00"));
        assertTrue(result);
    }

    @Test
    public void testNegativeCostIsInvalid() {
        Boolean result = TransactionService.isCostValid.apply(new BigDecimal("-50.00"));
        assertFalse(result);
    }

    // Cost scale must be 0 or 2
    @Test
    public void testScaleOfZeroIsValid() {
        Boolean result = TransactionService.isCostValid.apply(new BigDecimal("50"));
        assertTrue(result);
    }

    @Test
    public void testScaleOfOneIsInvalid() {
        Boolean result = TransactionService.isCostValid.apply(new BigDecimal("5.0"));
        assertFalse(result);
    }

    @Test
    public void testScaleGTTwoIsInvalid() {
        Boolean result = TransactionService.isCostValid.apply(new BigDecimal("5.100"));
        assertFalse(result);

        result = TransactionService.isCostValid.apply(new BigDecimal("5.1001"));
        assertFalse(result);
    }

    // Transaction data must be non-null
    @Test
    void testTransactionIsValid() {
        Pair<Boolean, Status> result = TransactionService.isValid(buildTransaction());
        assertTrue(result.getKey());
    }

    @Test
    void testTransactionIsInvalid() {
        Pair<Boolean, Status> result =
                TransactionService.isValid(buildTransaction(true, false, false));
        assertFalse(result.getKey());
        assertEquals(result.getValue(), Status.CUSTOMER_ID_REQUIRED);

        result = TransactionService.isValid(buildTransaction(false, true, false));
        assertFalse(result.getKey());
        assertEquals(result.getValue(), Status.PURCHASE_DATE_REQUIRED);

        result = TransactionService.isValid(buildTransaction(false, false, true));
        assertFalse(result.getKey());
        assertEquals(result.getValue(), Status.COST_REQUIRED);
    }
}
