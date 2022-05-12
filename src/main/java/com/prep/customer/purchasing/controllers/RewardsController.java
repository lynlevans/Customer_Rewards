package com.prep.customer.purchasing.controllers;

import static com.prep.customer.purchasing.domain.enums.Status.ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prep.customer.purchasing.domain.CustomerHistory;
import com.prep.customer.purchasing.domain.CustomerRewards;
import com.prep.customer.purchasing.domain.enums.Status;
import com.prep.customer.purchasing.services.impl.RewardsService;
import com.prep.customer.purchasing.services.impl.TransactionService;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RewardsController {

    @Autowired RewardsService rewardsService;

    @Autowired TransactionService transactionService;

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(RewardsController.class);

    @PostConstruct
    public void init() {
        jsonMapper.registerModule(new JavaTimeModule());
    }

    @RequestMapping(value = "/rewards", method = GET, produces = "application/json")
    public ResponseEntity<String> calculateRewards(
            @RequestParam List<Long> customerIds,
            @RequestParam(required = false) Integer startMonth,
            @RequestParam(required = false) Integer endMonth) {

        try {
            List<CustomerRewards> rewardsListing =
                    rewardsService.process(customerIds, startMonth, endMonth);
            String responseStr = jsonMapper.writeValueAsString(rewardsListing);
            return new ResponseEntity<String>(responseStr, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception processing rewards listing.", e);
        }

        return new ResponseEntity<String>(ERROR.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "/rewards",
            method = POST,
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<String> calculateRewards(@RequestBody CustomerHistory history) {
        Pair<Boolean, Status> valid = rewardsService.isCustomerHistoryValid(history);

        if (!valid.getLeft()) {
            return new ResponseEntity<String>(valid.getValue().getStatus(), HttpStatus.BAD_REQUEST);
        }

        CustomerRewards rewards =
                new CustomerRewards(
                        history.getCustomerId(),
                        new HashMap()); // rewardsService.calculateMonthlyRewards(history));

        try {
            String responseStr = jsonMapper.writeValueAsString(rewards);
            return new ResponseEntity<String>(responseStr, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception writing response string", e);
        }

        return new ResponseEntity<String>(ERROR.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
