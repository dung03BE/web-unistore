package com.dung.UniStore.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer productColorId;
    private String color;
    private int quantity;
}