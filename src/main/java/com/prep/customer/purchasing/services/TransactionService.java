package com.prep.customer.purchasing.services;

import static com.prep.customer.purchasing.domain.enums.Status.*;

import com.prep.customer.purchasing.domain.Transaction;
import com.prep.customer.purchasing.domain.enums.Status;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired com.prep.customer.purchasing.repository.TransactionRepository transactionRepository;

    public static final List<Integer> REQUIRED_DECIMAL_PLACES = Arrays.asList(0, 2);
    public static final BigDecimal FIFTY = new BigDecimal(50.00);
    public static final BigDecimal HUNDRED = new BigDecimal(100.00);
    public static final Integer FIFTY_VALUE = 50;
    public static final Integer HUNDRED_VALUE = 100;
    public static final Integer FIFTY_MULTIPLIER = 1;
    public static final Integer HUNDRED_MULTIPLIER = 2;

    public static final Function<BigDecimal, Boolean> isCostValid =
            (cost) ->
                    cost.compareTo(BigDecimal.ZERO) >= 0
                            && REQUIRED_DECIMAL_PLACES.contains(cost.scale());

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

    public List<Long> getAllCustomerIds() {
        return transactionRepository.getAllCustomerIds();
    }

    public static Pair<Boolean, Status> isValid(final Transaction t) {
        if (t.getCustomerId() == null) {
            return new ImmutablePair<>(false, CUSTOMER_ID_REQUIRED);

        } else if (t.getCost() == null) {
            return new ImmutablePair<>(false, COST_REQUIRED);

        } else if (!isCostValid.apply(t.getCost())) {
            return new ImmutablePair<>(false, COST_FORMAT_ERROR);

        } else if (t.getDate() == null) {
            return new ImmutablePair<>(false, PURCHASE_DATE_REQUIRED);
        }

        return new ImmutablePair<>(true, SUCCESS);
    }
}
