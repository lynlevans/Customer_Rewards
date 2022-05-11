package com.prep.customer.purchasing.controllers;

import static com.prep.customer.purchasing.domain.enums.Status.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prep.customer.purchasing.domain.CustomerHistory;
import com.prep.customer.purchasing.domain.CustomerRewards;
import com.prep.customer.purchasing.domain.Transaction;
import com.prep.customer.purchasing.domain.enums.Status;
import com.prep.customer.purchasing.services.impl.RewardsService;
import com.prep.customer.purchasing.services.impl.TransactionService;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @Autowired RewardsService rewardsService;

    @Autowired TransactionService transactionService;

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @PostConstruct
    public void init() {
        jsonMapper.registerModule(new JavaTimeModule());
    }

    @RequestMapping(method = GET, produces = "application/json")
    public ResponseEntity<String> getAll() {

        try {
            String responseStr =
                    jsonMapper.writeValueAsString(transactionService.getAllTransaction());
            return new ResponseEntity<>(responseStr, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception retrieving transactions", e);
        }

        return new ResponseEntity<>(ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "create",
            method = POST,
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<String> insertTransaction(@RequestBody Transaction transaction) {
        Pair<Boolean, Status> valid = transactionService.isValid(transaction);

        if (!valid.getLeft()) {
            return new ResponseEntity<>(valid.getRight().name(), HttpStatus.BAD_REQUEST);
        }

        try {
            transactionService.save(transaction);
            return new ResponseEntity<>(SUCCESS.name(), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Exception creating transactions", e);
        }

        return new ResponseEntity<>(ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "clearAll", method = POST)
    public ResponseEntity<Void> deleteAll() {
        try {
            transactionService.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception clearing transactions", e);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "/rewards/calculate",
            method = POST,
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<String> calculateBonus(@RequestBody CustomerHistory history) {
        Pair<Boolean, Status> valid = rewardsService.isCustomerHistoryValid(history);

        if (!valid.getLeft()) {
            return new ResponseEntity<String>(valid.getRight().getStatus(), HttpStatus.BAD_REQUEST);
        }

        CustomerRewards rewards =
                new CustomerRewards(
                        history.getCustomerId(), rewardsService.calculateMonthlyRewards(history));

        try {
            String responseStr = jsonMapper.writeValueAsString(rewards);
            return new ResponseEntity<String>(responseStr, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception writing response string", e);
        }

        return new ResponseEntity<String>(ERROR.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
