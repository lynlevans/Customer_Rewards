package com.prep.customer.purchasing.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Purchase {

    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss")
    LocalDateTime date;

    BigDecimal cost;

}
