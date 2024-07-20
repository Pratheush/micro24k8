package com.mylearning.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent1 {
    private String orderNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}
