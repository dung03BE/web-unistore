package com.dung.UniStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalInventoryResponse {
    private Integer productId;
    private String productName;
    private int totalQuantity;
}