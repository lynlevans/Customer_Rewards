package com.prep.customer.purchasing.controllers;

import com.prep.customer.purchasing.domain.CustomerHistory;
import com.prep.customer.purchasing.domain.StatusEnum;
import com.prep.customer.purchasing.services.impl.PurchaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;

    @RequestMapping(method = GET)
    public String init() {
        return "I am here";
    }

    @RequestMapping(value="/bonus/calculate", method = POST)
    public ResponseEntity<String> calculateBonus(@RequestBody CustomerHistory history) {
        Pair<Boolean, StatusEnum> valid = purchaseService.isCustomerHistoryValid(history);
        if (!valid.getLeft()) {
            return new ResponseEntity<String>(valid.getRight().getStatus(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>("Here", HttpStatus.OK);

    }
}
