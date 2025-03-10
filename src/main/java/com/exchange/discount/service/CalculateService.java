package com.exchange.discount.service;

import com.exchange.discount.model.Bill;
import com.exchange.discount.model.Items;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CalculateService {

    @Autowired
    private CurrencyService currencyService;

    public Double calculate(Bill bill) {
        log.info("Bill: {}", bill);

        Double totalAmountPayable = discountToApply(bill);
        double usdRate = currencyService.currencyConversion(bill.getOriginalCurrency(), "USD");

        double payableAmountInUsd = (totalAmountPayable/usdRate);

        payableAmountInUsd = fiveDollarDiscount(payableAmountInUsd);

        double usdRateOriginal = currencyService.currencyConversion("USD", bill.getTargetCurrency());

        double payableAmountOriginal = payableAmountInUsd * usdRateOriginal;

        return roundOff(payableAmountOriginal);
    }

    public Double fiveDollarDiscount(Double payableAmountInUsd){
        int discountMultiplier = (int) (payableAmountInUsd / 100); // Number of times 100 fits into the value
        double discount = discountMultiplier * 5;

        return payableAmountInUsd - discount;
    }

    private static Double roundOff(Double value){
        return Math.round(value * 100.0) / 100.0;
    }

    private Double discountToApply(Bill bill) {
        Double totalAmountPayable;

        if (bill.getUserType().equals("customer") && bill.getCustomerTenure() > 24) {
            totalAmountPayable = discount(bill, 0.95);
        } else if (bill.getUserType().equals("affiliate")) {
            totalAmountPayable = discount(bill, 0.9);
        } else if (bill.getUserType().equals("employee")) {
            totalAmountPayable = discount(bill, 0.7);
        } else {
            totalAmountPayable = bill.getItems()
                    .stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
        }
        return totalAmountPayable;
    }

    private Double discount(Bill bill, Double discountToApply) {
        double nonDiscountedSum = bill.getItems()
                .stream()
                .filter(Items::getIsGrocery)
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

         double discountedSum = bill.getItems()
                .stream()
                .filter(items -> !items.getIsGrocery())
                .mapToDouble(item -> (item.getPrice() * discountToApply) * item.getQuantity())
                .sum();

        return nonDiscountedSum + discountedSum;
    }
}