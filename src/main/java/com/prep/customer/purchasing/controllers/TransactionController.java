package com.prep.customer.purchasing.controllers;

import static com.prep.customer.purchasing.domain.enums.Status.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prep.customer.purchasing.domain.Transaction;
import com.prep.customer.purchasing.domain.enums.Status;
import com.prep.customer.purchasing.services.TransactionService;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {

    @Autowired TransactionService transactionService;

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @PostConstruct
    public void init() {
        jsonMapper.registerModule(new JavaTimeModule());
    }

    @RequestMapping(value = "/transactions", method = GET, produces = "application/json")
    public ResponseEntity<String> getAll() {

        try {
            String responseStr =
                    jsonMapper.writeValueAsString(transactionService.getAllTransaction());
            return new ResponseEntity<>(responseStr, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception retrieving transactions", e);
        }

        return new ResponseEntity<>(ERROR.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/transactions/{custId}", method = GET, produces = "application/json")
    public ResponseEntity<String> getOne(@PathVariable Long custId) {

        try {
            String responseStr =
                    jsonMapper.writeValueAsString(
                            transactionService.getTransactionByCustomerId(custId));
            return new ResponseEntity<>(responseStr, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception retrieving transactions", e);
        }

        return new ResponseEntity<>(ERROR.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "/transactions/create",
            method = POST,
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<String> insertTransaction(@RequestBody Transaction transaction) {

        if (transaction != null) {
            try {
                Pair<Boolean, Status> valid = TransactionService.isValid(transaction);
                if (!valid.getLeft()) {
                    return new ResponseEntity<>(
                            valid.getRight().getStatus(), HttpStatus.BAD_REQUEST);
                }

                transactionService.save(transaction);
                return new ResponseEntity<>(SUCCESS.getStatus(), HttpStatus.CREATED);
            } catch (Exception e) {
                logger.error("Exception creating transactions", e);
            }
        }
        return new ResponseEntity<>(ERROR.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/transactions/clearAll", method = POST)
    public ResponseEntity<Void> deleteAll() {
        try {
            transactionService.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception clearing transactions", e);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
