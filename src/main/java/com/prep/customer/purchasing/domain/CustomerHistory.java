package com.prep.customer.purchasing.domain;

import lombok.Data;

import java.util.List;

@Data
public class CustomerHistory {

    private Long customerId;

    private List<Transaction> transactions;
}
