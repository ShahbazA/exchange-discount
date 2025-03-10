package com.exchange.discount.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Bill {
    private List<Items> items;
    private Double totalAmount;
    private String userType;
    private Integer customerTenure; //number of months
    private String originalCurrency;
    private String targetCurrency;
}
