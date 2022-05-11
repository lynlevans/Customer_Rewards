package com.prep.customer.purchasing.services.impl;

import static com.prep.customer.purchasing.domain.enums.Status.*;

import com.prep.customer.purchasing.domain.CustomerHistory;
import com.prep.customer.purchasing.domain.Transaction;
import com.prep.customer.purchasing.domain.enums.Status;
import java.math.BigDecimal;
import java.util.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
public class RewardsService extends TransactionService {
    /**
     * isCustomerHistoryValid - verify: customer id, purchases is not null cost is not null and
     * non-negative cost has zero or null and non-negative
     *
     * @param history
     * @return Pair of boolean and valid status
     */
    public Pair<Boolean, Status> isCustomerHistoryValid(CustomerHistory history) {

        Pair<Boolean, Status> valid;

        if (history != null) {
            if (history.getCustomerId() != null) {
                if (history.getTransactions() != null) {
                    for (Transaction t : history.getTransactions()) {
                        //                        if (p.getCost() == null) {
                        //                            return new ImmutablePair<>(false,
                        // COST_REQUIRED);
                        //
                        //                        } else if (p.getCost().compareTo(BigDecimal.ZERO)
                        // < 0
                        //                                ||
                        // !REQUIRED_DECIMAL_PLACES.contains(p.getCost().scale())) {
                        //                            return new ImmutablePair<>(false,
                        // COST_FORMAT_ERROR);
                        //
                        //                        }else if (p.getDate() == null) {
                        //                            return new ImmutablePair<>(false,
                        // PURCHASE_DATE_REQUIRED);
                        //                        }
                        //                    }
                        //                    return new ImmutablePair<>(true, SUCCESS);
                        //                }
                        valid = isValid(t);
                        if (!valid.getLeft()) {
                            return valid;
                        }
                    }
                    return new ImmutablePair<>(true, SUCCESS);
                }
            }
        }

        return new ImmutablePair<>(false, CUSTOMER_HISTORY_INVALID);
    }

    /**
     * @param history
     * @return Map of date and total bonus
     */
    public Map<String, Integer> calculateMonthlyRewards(CustomerHistory history) {
        Map<String, Integer> resultMap = new HashMap<>();

        history.getTransactions()
                .forEach(
                        tx -> {
                            String currMon = tx.getDate().getMonth().name();
                            resultMap.computeIfAbsent(currMon, f -> 0);
                            int reward = calculateReward(tx.getCost());
                            resultMap.put(currMon, resultMap.get(currMon) + reward);
                        });

        return resultMap;
    }

    /**
     * calculateReward - perform reward calculation from cost
     *
     * @param cost
     * @return int reward value
     */
    private int calculateReward(BigDecimal cost) {
        int reward = 0;

        if (cost.compareTo(HUNDRED) > 0) {
            reward += (cost.intValue() - HUNDRED_VALUE) * HUNDRED_MULTIPLIER;
            reward += FIFTY_VALUE * FIFTY_MULTIPLIER;

        } else if (cost.compareTo(FIFTY) > 0) {
            reward += (cost.intValue() - FIFTY_VALUE) * FIFTY_MULTIPLIER;
        }

        return reward;
    }
}
