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
        String originalCurrency = bill.getOriginalCurrency();
        String targetCurrency = bill.getTargetCurrency();

        Double totalAmountPayable = discountToApply(bill);
        log.info("Total amount after applying discount: {} {}", totalAmountPayable, originalCurrency);

        double usdRate = currencyService.currencyConversion(bill.getOriginalCurrency(), "USD");
        log.info("USD rate: {}", usdRate);

        double payableAmountInUsd = totalAmountPayable * usdRate;
        log.info("Total amount converted to USD: {}", payableAmountInUsd);

        payableAmountInUsd = fiveDollarDiscount(payableAmountInUsd);
        log.info("Total amount after $5 discount: {}", payableAmountInUsd);

        double usdRateOriginal = currencyService.currencyConversion("USD", targetCurrency);
        log.info("USD to {} rate: {}", targetCurrency , usdRateOriginal);

        double payableAmountOriginal = payableAmountInUsd * usdRateOriginal;
        log.info("Payable amount after conversion to {} : {}", targetCurrency, payableAmountOriginal);

        double finalPayableAmount = roundOff(payableAmountOriginal);
        log.info("Final payable amount after all discounts: {}", finalPayableAmount);

        return finalPayableAmount;
    }

    public Double fiveDollarDiscount(Double payableAmountInUsd){
        int discountMultiplier = (int) (payableAmountInUsd / 100);
        double discount = discountMultiplier * 5;
        if(discount > 0.0){
            log.info("Bill is greater than $100, applying discount for ${}", discount);
        }else{
            log.info("Bill is less than $100, discount not applied");
        }
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