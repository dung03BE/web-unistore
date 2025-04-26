package com.dung.UniStore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryCreationRequest {
    private Integer id;
    private Integer productId;
    private Integer productColorId;
    private int quantity;
}
