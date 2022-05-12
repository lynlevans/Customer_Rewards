package com.prep.customer.purchasing.services.impl;

import static com.prep.customer.purchasing.domain.enums.Status.*;

import com.prep.customer.purchasing.domain.Transaction;
import com.prep.customer.purchasing.domain.enums.Status;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired com.prep.customer.purchasing.repository.TransactionRepository transactionRepository;

    static final List<Integer> REQUIRED_DECIMAL_PLACES = Arrays.asList(0, 2);
    static final BigDecimal FIFTY = new BigDecimal(50.00);
    static final BigDecimal HUNDRED = new BigDecimal(100.00);
    static final Integer FIFTY_VALUE = 50;
    static final Integer HUNDRED_VALUE = 100;
    static final Integer FIFTY_MULTIPLIER = 1;
    static final Integer HUNDRED_MULTIPLIER = 2;

    public List<Transaction> getAllTransaction() {
        List<Transaction> Transactions = new ArrayList<Transaction>();
        transactionRepository.findAll().forEach(Transaction -> Transactions.add(Transaction));
        return Transactions;
    }

    public Transaction getTransactionById(int id) {
        return transactionRepository.findById(id).get();
    }

    public List<Transaction> getTransactionByCustomerId(Long custId) {
        return transactionRepository.findByCustomerId(custId);
    }

    public void save(Transaction Transaction) {
        transactionRepository.save(Transaction);
    }

    public void delete(int id) {
        transactionRepository.deleteById(id);
    }

    public void deleteAll() {
        transactionRepository.deleteAll();
    }

    public Pair<Boolean, Status> isValid(final Transaction t) {
        if (t.getCustomerId() == null) {
            return new ImmutablePair<>(false, CUSTOMER_ID_REQUIRED);

        } else if (t.getCost() == null) {
            return new ImmutablePair<>(false, COST_REQUIRED);

        } else if (t.getCost().compareTo(BigDecimal.ZERO) < 0
                || !REQUIRED_DECIMAL_PLACES.contains(t.getCost().scale())) {
            return new ImmutablePair<>(false, COST_FORMAT_ERROR);

        } else if (t.getDate() == null) {
            return new ImmutablePair<>(false, PURCHASE_DATE_REQUIRED);
        }

        return new ImmutablePair<>(true, SUCCESS);
    }
}
