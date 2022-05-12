package com.prep.customer.purchasing.domain;

import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class CustomerRewards {

    private Long customerId;

    @Setter(AccessLevel.NONE)
    private Map<String, Integer> monthlyRewards;

    @Setter(AccessLevel.NONE)
    private Integer grandTotal = 0;

    public CustomerRewards(Long customerId) {
        this.customerId = customerId;
    }

    public CustomerRewards(Long customerId, Map<String, Integer> monthlyRewards) {
        this.customerId = customerId;
        this.monthlyRewards = monthlyRewards;
        grandTally();
    }

    private void grandTally() {
        grandTotal = monthlyRewards.values().stream().reduce(0, (a, b) -> a + b);
    }
}
