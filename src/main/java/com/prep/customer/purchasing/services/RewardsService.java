package com.prep.customer.purchasing.services;

import com.prep.customer.purchasing.domain.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
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

        List<Long> validCustIds = getAllCustomerIds();

        // Filter for valid custIds from incoming list. If empty, process all custIds.
        customerIds =
                (customerIds != null && !customerIds.isEmpty())
                        ? customerIds.stream()
                                .filter(s -> validCustIds.contains(s))
                                .collect(Collectors.toList())
                        : validCustIds;

        for (Long cId : customerIds) {
            tasks.add(
                    executorService.submit(
                            new CalculateRewardsCallable(cId, startMonth, endMonth)));
        }

        for (Future<Pair<Boolean, CustomerRewards>> f : tasks) {
            try {
                Pair<Boolean, CustomerRewards> result = f.get();
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
     * CalculateRewardsCallable - Callable task to calculate customer's reward poins
     *
     * @return <Pair<Boolean, CustomerRewards>
     */
    private class CalculateRewardsCallable implements Callable<Pair<Boolean, CustomerRewards>> {

        private Long customerId;
        private Integer startMonth, endMonth;
        private List<Transaction> custTransactions;
        Map<String, Integer> rewardsMap = new LinkedHashMap<>();

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

            custTransactions.stream()
                    .filter(
                            tx ->
                                    tx.getDate().getMonth().getValue() >= startMonth
                                            && tx.getDate().getMonth().getValue() <= endMonth)
                    .forEach(
                            tx -> {
                                String currMon = tx.getDate().getMonth().name();
                                rewardsMap.computeIfAbsent(currMon, f -> 0);
                                int reward = calculateReward(tx.getCost());
                                rewardsMap.put(currMon, rewardsMap.get(currMon) + reward);
                            });

            return new ImmutablePair<>(true, new CustomerRewards(customerId, rewardsMap));
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
     * monthsAreValid - Check that integer month values are valid
     *
     * @param startMonth
     * @param endMonth
     * @return boolean
     */
    public static Boolean areMonthsValid(Integer startMonth, Integer endMonth) {
        if (startMonth == null && endMonth == null) {
            return true;
        } else if (startMonth != null
                && startMonth >= Month.JANUARY.getValue()
                && startMonth <= Month.DECEMBER.getValue()
                && endMonth != null
                && endMonth >= Month.JANUARY.getValue()
                && endMonth <= Month.DECEMBER.getValue()) {
            return true;
        }

        return false;
    }
}
