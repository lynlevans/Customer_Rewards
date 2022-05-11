package com.prep.customer.purchasing.domain;

import lombok.Data;

@Data
public class CustomerHistory {

    Long customerId;

    Purchase[] purchases;
}
