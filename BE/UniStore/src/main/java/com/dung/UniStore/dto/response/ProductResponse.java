package com.dung.UniStore.dto.response;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private int id;
    private String name;
    private float price;

    private String description;
    private int  categoryId;
    private Double discount;
    private List<ProductImageResponse> thumbnails;
    private ProductDetailsResponse details;
    private List<ProductColorResponse> colors;
    private String brand;
    private String model;
    private Byte available;
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ProductColorResponse {
        private int id;
        private String color;
    }
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ProductImageResponse {
        private int id;
        private String imageUrl;
    }
    private Integer quantity;
}
