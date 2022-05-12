package com.prep.customer.purchasing.services;

import com.prep.customer.purchasing.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BaseTest {

    private static final LocalDateTime now = LocalDateTime.now();
    private static final Integer ID1 = 1;

    Transaction buildTransaction() {
        return new Transaction(ID1, ID1.longValue(), now, BigDecimal.ONE);
    }

    Transaction buildTransaction(Boolean customerIsNull, Boolean dateIsNull, Boolean costIsNull) {
        Long custId = customerIsNull ? null : ID1.longValue();
        LocalDateTime date = dateIsNull ? null : now;
        BigDecimal cost = costIsNull ? null : BigDecimal.ONE;
        return new Transaction(ID1, custId, date, cost);
    }
}
