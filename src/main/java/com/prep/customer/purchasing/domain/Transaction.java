package com.prep.customer.purchasing.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "customer_id")
    private Long customerId;

    @JsonFormat(pattern = "MM-dd-yyyy HH:mm:ss")
    @NotNull
    @Column
    private LocalDateTime date;

    @NotNull @Column private BigDecimal cost;
}
