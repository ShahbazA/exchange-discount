package com.exchange.discount.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Items {
    private String name;
    private Double price;
    private Integer quantity;
    private Boolean isGrocery;
}