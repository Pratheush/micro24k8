package com.mylearning.dto;

import lombok.Builder;

@Builder
public record InventoryResponse (String skuCode, boolean isInStock){

}
