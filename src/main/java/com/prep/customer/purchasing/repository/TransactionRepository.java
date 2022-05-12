package com.prep.customer.purchasing.repository;

import com.prep.customer.purchasing.domain.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    public List<Transaction> findByCustomerId(Long custId);

    @Query(value = "select distinct customerId from Transaction")
    public List<Long> getAllCustomerIds();
}
