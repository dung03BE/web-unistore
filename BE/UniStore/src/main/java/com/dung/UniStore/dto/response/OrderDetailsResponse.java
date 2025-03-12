package com.dung.UniStore.dto.response;



import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsResponse {
    private int id;
    private int productId;
    private int orderId;
    private int quantity;
    private BigDecimal price;
    private String color;
    List<ProductResponse> productResponses;
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ProductResponse {
        private String name;
        private String color;
    }
}
