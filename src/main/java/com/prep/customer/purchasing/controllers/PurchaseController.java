package com.prep.customer.purchasing.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prep.customer.purchasing.domain.CustomerHistory;
import com.prep.customer.purchasing.domain.CustomerRewards;
import com.prep.customer.purchasing.domain.enums.StatusEnum;
import com.prep.customer.purchasing.services.impl.RewardsService;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.prep.customer.purchasing.domain.enums.StatusEnum.ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
public class PurchaseController {

    @Autowired
    RewardsService rewardsService;

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private Logger logger = LoggerFactory.getLogger(PurchaseController.class);

    @RequestMapping(method = GET)
    public String init() {
        return "I am here";
    }

    @RequestMapping(value="/bonus/calculate", method = POST)
    public ResponseEntity<String> calculateBonus(@RequestBody CustomerHistory history) {
        Pair<Boolean, StatusEnum> valid = rewardsService.isCustomerHistoryValid(history);
        if (!valid.getLeft()) {
            return new ResponseEntity<String>(valid.getRight().getStatus(), HttpStatus.BAD_REQUEST);
        }

        CustomerRewards rewards = new CustomerRewards(history.getCustomerId(),
                rewardsService.calculateMonthlyRewards(history));

        try {
            String responseStr = jsonMapper.writeValueAsString(rewards);
            return new ResponseEntity<String>(jsonMapper.writeValueAsString(rewards), HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Exception writing response string", e);
        }
        return new ResponseEntity<String>(ERROR.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
