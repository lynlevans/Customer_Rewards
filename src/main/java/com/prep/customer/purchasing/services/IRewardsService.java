package com.prep.customer.purchasing.services;

import com.prep.customer.purchasing.domain.CustomerHistory;
import com.prep.customer.purchasing.domain.enums.Status;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

public interface IRewardsService {

    public Pair<Boolean, Status> isCustomerHistoryValid(CustomerHistory history);

    public Map<String, Integer> calculateMonthlyRewards(CustomerHistory history);
}
