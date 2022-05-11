package com.prep.customer.purchasing.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import javax.persistence.*;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="transaction")
public class Transaction {

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    @Column(name="customer_id")
    private Integer customerId;

    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss")
    @NotNull
    @Column
    private LocalDateTime date;

    @NotNull
    @Column
    private BigDecimal cost;
}
