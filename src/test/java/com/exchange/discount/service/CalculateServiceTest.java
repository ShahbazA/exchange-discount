package com.exchange.discount.service;

import com.exchange.discount.model.Bill;
import com.exchange.discount.model.Items;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CalculateServiceTest {

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CalculateService calculateService;

    @Test
    void contextLoads() {
    }

    @Test
    public void isCustomerNonLoyalFivePercentDiscount() {

        Items item1 = Items.builder().name("Eggs").isGrocery(true).price(24.0).quantity(1).build();
        Items item2 = Items.builder().name("Bag").isGrocery(false).price(10.0).quantity(30).build();
        Items item3 = Items.builder().name("Brush").isGrocery(false).price(70.0).quantity(1).build();
        List<Items> itemsList = List.of(item1, item2, item3);

        Bill bill = Bill.builder()
                .items(itemsList)
                .totalAmount(50.0)
                .userType("customer")
                .customerTenure(25)
                .originalCurrency("AED")
                .targetCurrency("PKR")
                .build();

        when(currencyService.currencyConversion("AED", "USD")).thenReturn(3.6725);
        when(currencyService.currencyConversion("USD", "PKR")).thenReturn(279.491578);

        Assertions.assertThat(calculateService.calculate(bill)).isEqualTo(27179.56);
    }

    @Test
    public void isCustomerNonLoyal() {
        //isCustomer and less than 24 months or 2 years

        Bill bill = Bill.builder()
                .items(getItems())
                .totalAmount(50.0)
                .userType("customer")
                .customerTenure(10)
                .originalCurrency("AED")
                .targetCurrency("USD")
                .build();

        when(currencyService.currencyConversion("AED", "USD")).thenReturn(3.6725);
        when(currencyService.currencyConversion("USD", "USD")).thenReturn(1.0);

        Double amountPayable = calculateService.calculate(bill);
        Assertions.assertThat(amountPayable).isEqualTo(13.61);
    }

    @Test
    public void isCustomerIsLoyal() {
        //isCustomer and greater than 24 months or 2 years

        Bill bill = Bill.builder()
                .items(getItems())
                .totalAmount(50.0)
                .userType("customer")
                .customerTenure(25)
                .originalCurrency("AED")
                .targetCurrency("USD")
                .build();

        when(currencyService.currencyConversion("AED", "USD")).thenReturn(3.6725);
        when(currencyService.currencyConversion("USD", "USD")).thenReturn(1.0);

        Double amountPayable = calculateService.calculate(bill);
        Assertions.assertThat(amountPayable).isEqualTo(13.07);
    }

    @Test
    public void isAffiliate() {
        Bill bill = Bill.builder()
                .items(getItems())
                .totalAmount(50.0)
                .userType("affiliate")
                .customerTenure(25)
                .originalCurrency("AED")
                .targetCurrency("USD")
                .build();

        when(currencyService.currencyConversion("AED", "USD")).thenReturn(3.6725);
        when(currencyService.currencyConversion("USD", "USD")).thenReturn(1.0);

        Double amountPayable = calculateService.calculate(bill);
        Assertions.assertThat(amountPayable).isEqualTo(12.53);
    }

    @Test
    public void isEmployee() {

        Bill bill = Bill.builder()
                .items(getItems())
                .totalAmount(50.0)
                .userType("employee")
                .customerTenure(25)
                .originalCurrency("AED")
                .targetCurrency("USD")
                .build();

        when(currencyService.currencyConversion("AED", "USD")).thenReturn(3.6725);
        when(currencyService.currencyConversion("USD", "USD")).thenReturn(1.0);

        Double amountPayable = calculateService.calculate(bill);
        Assertions.assertThat(amountPayable).isEqualTo(10.35);
    }

    @Test
    public void fiveDollarDiscountTest() {
        Double discountAmount = calculateService.fiveDollarDiscount(500.0);
        Assertions.assertThat(discountAmount).isEqualTo(475.0);

        Double discountAmount1 = calculateService.fiveDollarDiscount(99.0);
        Assertions.assertThat(discountAmount1).isEqualTo(99.0);

        Double discountAmount2 = calculateService.fiveDollarDiscount(1.0);
        Assertions.assertThat(discountAmount2).isEqualTo(1.0);
    }

    private List<Items> getItems() {
        Items item1 = Items.builder().name("Eggs").isGrocery(true).price(10.0).quantity(1).build();
        Items item2 = Items.builder().name("Toothpaste").isGrocery(false).price(20.0).quantity(2).build();
        return List.of(item1, item2);
    }

    public void convertStringToBill() throws JsonProcessingException {
        List<Items> items = List.of(Items.builder().name("Eggs").isGrocery(true).price(12.56).quantity(1).build(),
                Items.builder().name("Toothpaste").isGrocery(false).price(20.45).quantity(2).build());
        Bill bill = Bill.builder().items(items).totalAmount(53.46).userType("affiliate").customerTenure(25).originalCurrency("AED").targetCurrency("USD").build();

        ObjectMapper objectMapper = new ObjectMapper();
        String billStr = objectMapper.writeValueAsString(bill);

//        calculateService.calculate(bill);
    }
}
