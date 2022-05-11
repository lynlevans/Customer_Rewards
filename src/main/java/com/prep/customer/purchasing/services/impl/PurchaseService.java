package com.prep.customer.purchasing.services.impl;

import com.prep.customer.purchasing.domain.CustomerHistory;
import com.prep.customer.purchasing.domain.StatusEnum;
import com.prep.customer.purchasing.domain.Purchase;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.prep.customer.purchasing.domain.StatusEnum.*;

@Service
public class PurchaseService {

    private final static Short REQUIRED_DECIMAL_PLACES = 2;

    /**
     * isCustomerHistoryValid
     * - verify:
     *       customer id, purchases is not null
     *       cost is not null and non-negative
     *       cost has zero or  null and non-negative
     *
     * @param history
     * @return
     */
    public Pair<Boolean, StatusEnum> isCustomerHistoryValid(CustomerHistory history) {
        if (history != null) {
            if (history.getCustomerId() != null) {
                if (history.getPurchases() != null) {
                    for (Purchase p : history.getPurchases()) {
                        if (p.getCost() == null) {
                            return new ImmutablePair<>(false, COST_REQUIRED);

                        } else if (p.getCost().compareTo(BigDecimal.ZERO) < 0
                                || p.getCost().scale() != REQUIRED_DECIMAL_PLACES) {
                            return new ImmutablePair<>(false, COST_FORMAT_ERROR);

                        }else if (p.getDate() == null) {
                            return new ImmutablePair<>(false, PURCHASE_DATE_REQUIRED);
                        }
                    }
                    return new ImmutablePair<>(true, SUCCESS);
                }
            }
        }
        return new ImmutablePair<>(false, CUSTOMER_HISTORY_INVALID);
    }

    public Map<LocalDateTime, BigDecimal> calculateDailyBonus(CustomerHistory history) {
        return new HashMap<>();
    }

    public Map<String, BigDecimal> calculateMonthlyBonus(CustomerHistory history) {
        return new HashMap<>();
    }
}


