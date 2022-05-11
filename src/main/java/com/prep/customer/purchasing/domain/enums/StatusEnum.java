package com.prep.customer.purchasing.domain.enums;

public enum StatusEnum {
    COST_REQUIRED("Purchase cost cannot be null."),
    COST_FORMAT_ERROR("Purchase cost must be non-negative and include either zero or 2 decimal places."),
    CUSTOMER_HISTORY_INVALID("Customer history is invalid."),
    PURCHASE_DATE_REQUIRED("Purchase date cannot be null."),
    SUCCESS("Success"),
    ERROR("An error has occurred");

    private String status;

    StatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}