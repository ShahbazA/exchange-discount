package com.exchange.discount.controller;

import com.exchange.discount.model.Bill;
import com.exchange.discount.service.CalculateService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Log4j2
public class ExchangeDiscountController {

    @Autowired
    private CalculateService calculateService;

    @PostMapping("/calculate")
    public Double calculate(@RequestBody Bill bill){
        log.info("Bill received: {}", bill.toString());
        return calculateService.calculate(bill);
    }
}