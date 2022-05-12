package com.prep.customer.purchasing.services;

import static com.prep.customer.purchasing.services.TransactionService.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RewardsServiceTests extends BaseTest {

    private static final Integer FIFTY_ONE_VALUE = 51;
    private static final Integer HUNDRED_ONE_VALUE = 101;
    private static final Integer NINETY_VALUE = 90;

    // Rewards
    @Test
    public void testRewardsCostIsLessThanOrEqual50() {
        int result = RewardsService.calculateReward(new BigDecimal("0.00"));
        assertEquals(result, 0);

        result = RewardsService.calculateReward(new BigDecimal("49.00"));
        assertEquals(result, 0);

        result = RewardsService.calculateReward(new BigDecimal(FIFTY_VALUE));
        assertEquals(result, 0);
    }

    @Test
    public void testRewardsCostBetween50And100() {
        int result = RewardsService.calculateReward(new BigDecimal(FIFTY_ONE_VALUE));
        int expected = (FIFTY_ONE_VALUE - FIFTY_VALUE) * FIFTY_MULTIPLIER;
        assertEquals(result, expected);

        result = RewardsService.calculateReward(new BigDecimal(NINETY_VALUE));
        expected = (NINETY_VALUE - FIFTY_VALUE) * FIFTY_MULTIPLIER;
        assertEquals(result, expected);

        result = RewardsService.calculateReward(HUNDRED);
        expected = (HUNDRED.intValue() - FIFTY_VALUE) * FIFTY_MULTIPLIER;
        assertEquals(result, expected);
    }

    @Test
    public void testRewardsCostGT100() {
        int result = RewardsService.calculateReward(new BigDecimal(HUNDRED_ONE_VALUE));
        int expected = (HUNDRED_ONE_VALUE - HUNDRED_VALUE) * HUNDRED_MULTIPLIER;
        expected += FIFTY_VALUE * FIFTY_MULTIPLIER;
        assertEquals(result, expected);

        result = RewardsService.calculateReward(new BigDecimal(HUNDRED_VALUE + NINETY_VALUE));
        expected = (HUNDRED_VALUE + NINETY_VALUE - HUNDRED_VALUE) * HUNDRED_MULTIPLIER;
        expected += FIFTY_VALUE * FIFTY_MULTIPLIER;
        assertEquals(result, expected);

        result = RewardsService.calculateReward(new BigDecimal(HUNDRED_VALUE * 2));
        expected = ((HUNDRED_VALUE * 2) - HUNDRED_VALUE) * HUNDRED_MULTIPLIER;
        expected += FIFTY_VALUE * FIFTY_MULTIPLIER;
        assertEquals(result, expected);
    }
}
