package com.prep.customer.purchasing.domain.enums;

public enum Status {
    CUSTOMER_ID_REQUIRED("Customer id cannot be null."),
    COST_REQUIRED("Purchase cost cannot be null."),
    COST_FORMAT_ERROR(
            "Purchase cost must be non-negative and include either zero or 2 decimal places."),
    CUSTOMER_HISTORY_INVALID("Customer history is invalid."),
    MONTH_ID_ERROR("Month ids must be between 1 and 12 inclusive."),
    PURCHASE_DATE_REQUIRED("Purchase date cannot be null."),
    SUCCESS("Success"),
    ERROR("An error has occurred");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
