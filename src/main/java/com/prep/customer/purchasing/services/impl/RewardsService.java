package com.prep.customer.purchasing.services.impl;

import com.prep.customer.purchasing.domain.CustomerHistory;
import com.prep.customer.purchasing.domain.enums.StatusEnum;
import com.prep.customer.purchasing.domain.Transaction;
import com.prep.customer.purchasing.services.IRewardsService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.*;

import static com.prep.customer.purchasing.domain.enums.StatusEnum.*;

@Service
public class RewardsService implements IRewardsService {

    private final static List<Integer> REQUIRED_DECIMAL_PLACES = Arrays.asList(0, 2);
    private final static BigDecimal FIFTY = new BigDecimal(50.00);
    private final static BigDecimal HUNDRED = new BigDecimal(100.00);
    private final static Integer FIFTY_VALUE = 50;
    private final static Integer HUNDRED_VALUE = 100;
    private final static Integer FIFTY_MULTIPLIER = 1;
    private final static Integer HUNDRED_MULTIPLIER = 2;

    /**
     * isCustomerHistoryValid
     * - verify:
     *       customer id, purchases is not null
     *       cost is not null and non-negative
     *       cost has zero or  null and non-negative
     *
     * @param history
     * @return Pair of boolean and valid status
     */
    public Pair<Boolean, StatusEnum> isCustomerHistoryValid(CustomerHistory history) {
        if (history != null) {
            if (history.getCustomerId() != null) {
                if (history.getTransactions() != null) {
                    for (Transaction p : history.getTransactions()) {
                        if (p.getCost() == null) {
                            return new ImmutablePair<>(false, COST_REQUIRED);

                        } else if (p.getCost().compareTo(BigDecimal.ZERO) < 0
                                || !REQUIRED_DECIMAL_PLACES.contains(p.getCost().scale())) {
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

    /**
     *
     * @param history
     * @return Map of date and total bonus
     */
    public Map<String, Integer> calculateMonthlyRewards(CustomerHistory history) {
        Map<String, Integer> resultMap = new HashMap<>();

        history.getTransactions().forEach(
                tx -> {
                    String currMon = tx.getDate().getMonth().name();
                    resultMap.computeIfAbsent(currMon, f -> 0);
                    int reward = calculateReward(tx.getCost());
                    resultMap.put(currMon, resultMap.get(currMon) + reward);
                });

        return resultMap;
    }

    /**
     * calculateReward
     *   - perform reward calculation from cost
     * @param cost
     * @return int reward value
     */
    private int calculateReward(BigDecimal cost) {
        int reward = 0;

        if (cost.compareTo(HUNDRED) > 0)  {
            reward += (cost.intValue() - HUNDRED_VALUE) * HUNDRED_MULTIPLIER;
            reward += FIFTY_VALUE * FIFTY_MULTIPLIER;

        } else if (cost.compareTo(FIFTY) > 0) {
            reward += (cost.intValue() - FIFTY_VALUE) * FIFTY_MULTIPLIER;
        }
        
        return reward;
    }
}


