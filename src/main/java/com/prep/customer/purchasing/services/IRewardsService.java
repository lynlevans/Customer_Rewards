package com.prep.customer.purchasing.services;

import com.prep.customer.purchasing.domain.CustomerHistory;
import com.prep.customer.purchasing.domain.enums.Status;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public interface IRewardsService {

    public Pair<Boolean, Status> isCustomerHistoryValid(CustomerHistory history);

    public Map<String, Integer> calculateMonthlyRewards(CustomerHistory history);
}
