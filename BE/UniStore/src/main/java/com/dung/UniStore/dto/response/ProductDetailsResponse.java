package com.dung.UniStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailsResponse {
    private String screen_size;
    private String resolution;
    private String processor;
    private String ram;
    private String storage;
    private String battery;
    private String camera;
    private String os;
    private String weight;
    private String dimensions;
    private String sim;
    private String network;
}