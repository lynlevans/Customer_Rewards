package com.prep.customer.purchasing.controllers;

import static com.prep.customer.purchasing.domain.enums.Status.ERROR;
import static com.prep.customer.purchasing.domain.enums.Status.MONTH_ID_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prep.customer.purchasing.domain.CustomerRewards;
import com.prep.customer.purchasing.services.RewardsService;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RewardsController {

    @Autowired RewardsService rewardsService;

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private final Logger logger = LoggerFactory.getLogger(RewardsController.class);

    @PostConstruct
    public void init() {
        jsonMapper.registerModule(new JavaTimeModule());
    }

    @RequestMapping(value = "/rewards", method = GET, produces = "application/json")
    public ResponseEntity<String> calculateRewards(
            @RequestParam(required = false) List<Long> customerIds,
            @RequestParam(required = false) Integer startMonth,
            @RequestParam(required = false) Integer endMonth) {

        if (!RewardsService.areMonthsValid(startMonth, endMonth)) {
            return new ResponseEntity(MONTH_ID_ERROR.getStatus(), HttpStatus.BAD_REQUEST);
        }

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
}
