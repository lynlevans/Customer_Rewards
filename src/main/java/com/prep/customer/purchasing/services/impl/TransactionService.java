package com.prep.customer.purchasing.services.impl;

import com.prep.customer.purchasing.domain.Transaction;
import com.prep.customer.purchasing.domain.enums.Status;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.prep.customer.purchasing.domain.enums.Status.*;

@Service
public class TransactionService {

    @Autowired
    com.prep.customer.purchasing.repository.TransactionRepository transactionRepository;

    final static List<Integer> REQUIRED_DECIMAL_PLACES = Arrays.asList(0, 2);
    final static BigDecimal FIFTY = new BigDecimal(50.00);
    final static BigDecimal HUNDRED = new BigDecimal(100.00);
    final static Integer FIFTY_VALUE = 50;
    final static Integer HUNDRED_VALUE = 100;
    final static Integer FIFTY_MULTIPLIER = 1;
    final static Integer HUNDRED_MULTIPLIER = 2;

    public List<Transaction> getAllTransaction()
    {
        List<Transaction> Transactions = new ArrayList<Transaction>();
        transactionRepository.findAll().forEach(Transaction -> Transactions.add(Transaction));
        return Transactions;
    }

    public Transaction getTransactionById(int id)
    {
        return transactionRepository.findById(id).get();
    }

    public void save(Transaction Transaction)
    {
        transactionRepository.save(Transaction);
    }

    public void delete(int id)
    {
        transactionRepository.deleteById(id);
    }

    public void deleteAll()
    {
        transactionRepository.deleteAll();
    }

    public Pair<Boolean, Status> isValid(final Transaction t) {
        if (t.getCustomerId() == null) {
            return new ImmutablePair<>(false, COST_REQUIRED);

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
