package com.prep.customer.purchasing.services.impl;

import static com.prep.customer.purchasing.domain.enums.Status.*;

import com.prep.customer.purchasing.domain.*;
import com.prep.customer.purchasing.domain.enums.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RewardsService extends TransactionService {

    @Value("${app.thread.pool.size}")
    public Integer threadPoolSize;

    // @Autowired TransactionRepository transactionRepository;

    ExecutorService executorService;

    private final Logger logger = LoggerFactory.getLogger(RewardsService.class);

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(threadPoolSize);
    }
    /**
     * process - Manage rewards calculation thread per each customer Id
     *
     * @param customerIds
     * @param startMonth
     * @param endMonth
     * @return List CustomerRewards
     */
    public List<CustomerRewards> process(
            List<Long> customerIds, Integer startMonth, Integer endMonth) {

        List<CustomerRewards> aggregatedResults = new ArrayList<>();
        List<Future<Pair<Boolean, CustomerRewards>>> tasks = new ArrayList<>();

        for (Long cId : customerIds) {
            tasks.add(
                    executorService.submit(
                            new CalculateRewardsCallable(cId, startMonth, endMonth)));
        }

        for (Future f : tasks) {
            try {
                Pair<Boolean, CustomerRewards> result = (Pair<Boolean, CustomerRewards>) f.get();
                if (result.getLeft()) {
                    aggregatedResults.add(result.getValue());
                } else {
                    logger.error(
                            "Exception calculating rewards for custId {}.",
                            result.getValue().getCustomerId());
                }
            } catch (Exception e) {
                logger.error("Exception completing task.", e);
            }
        }

        return aggregatedResults;
    }

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
                        valid = isValid(t);
                        System.out.println(valid);
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
     * CalculateRewardsCallable - Callable task to calculate customer's reward poins
     *
     * @return <Pair<Boolean, CustomerRewards>
     */
    public class CalculateRewardsCallable implements Callable<Pair<Boolean, CustomerRewards>> {

        private Long customerId;
        private Integer startMonth, endMonth;
        private List<Transaction> custTransactions;
        Map<String, Integer> rewardsMap = new HashMap<>();

        public CalculateRewardsCallable() {}

        public CalculateRewardsCallable(Long customerId) {
            this.customerId = customerId;
        }

        public CalculateRewardsCallable(Long customerId, Integer startMonth, Integer endMonth) {
            this.customerId = customerId;
            this.startMonth = startMonth;
            this.endMonth = endMonth;
        }

        public Pair<Boolean, CustomerRewards> call() {
            if (!areMonthsValid(startMonth, endMonth)) {
                return new ImmutablePair<>(false, new CustomerRewards(customerId));
            }

            custTransactions = transactionRepository.findByCustomerId(customerId);

            startMonth =
                    startMonth == null ? LocalDateTime.now().getMonth().getValue() - 2 : startMonth;
            endMonth = endMonth == null ? LocalDateTime.now().getMonth().getValue() : endMonth;

            custTransactions =
                    custTransactions.stream()
                            .filter(
                                    t ->
                                            t.getDate().getMonth().getValue() >= startMonth
                                                    && t.getDate().getMonth().getValue()
                                                            <= endMonth)
                            .collect(Collectors.toList());

            custTransactions.forEach(
                    tx -> {
                        String currMon = tx.getDate().getMonth().name();
                        rewardsMap.computeIfAbsent(currMon, f -> 0);
                        int reward = calculateReward(tx.getCost());
                        rewardsMap.put(currMon, rewardsMap.get(currMon) + reward);
                    });

            return new ImmutablePair(true, new CustomerRewards(customerId, rewardsMap));
        }
    }

    /**
     * calculateReward - perform reward calculation for individual cost
     *
     * @param cost
     * @return int reward value
     */
    public static int calculateReward(BigDecimal cost) {
        int reward = 0;

        if (!isCostValid.apply(cost)) {
            return reward;
        }

        if (cost.compareTo(HUNDRED) > 0) {
            reward += (cost.intValue() - HUNDRED_VALUE) * HUNDRED_MULTIPLIER;
            reward += FIFTY_VALUE * FIFTY_MULTIPLIER;

        } else if (cost.compareTo(FIFTY) > 0) {
            reward += (cost.intValue() - FIFTY_VALUE) * FIFTY_MULTIPLIER;
        }

        return reward;
    }

    /**
     * monthsAreValid - Check that integer months value are valid
     *
     * @param startMonth
     * @param endMonth
     * @return boolean
     */
    private Boolean areMonthsValid(Integer startMonth, Integer endMonth) {
        if (startMonth == null && endMonth == null) {
            return true;
        } else if (startMonth != null
                && startMonth >= 0
                && startMonth < 12
                && endMonth != null
                && endMonth >= 0
                && endMonth < 12) {
            return true;
        }

        return false;
    }
}
