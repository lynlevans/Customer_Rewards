package com.prep.customer.purchasing.services;

import com.prep.customer.purchasing.domain.CustomerHistory;
import com.prep.customer.purchasing.domain.StatusEnum;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface IPurchaseService {

    public Pair<Boolean, StatusEnum> isCustomerHistoryValid(CustomerHistory history);

    public Map<LocalDateTime, BigDecimal> calculateDailyBonus(CustomerHistory history);

    public Map<String, BigDecimal> calculateMonthlyBonus(CustomerHistory history);
}
