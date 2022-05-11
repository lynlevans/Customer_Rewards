package com.prep.customer.purchasing.domain;

import java.util.List;
import lombok.Data;

@Data
public class CustomerHistory {

    private Long customerId;

    private List<Transaction> transactions;
}
