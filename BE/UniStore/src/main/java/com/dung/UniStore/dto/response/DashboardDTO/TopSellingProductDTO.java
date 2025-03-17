package com.dung.UniStore.dto.response.DashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopSellingProductDTO {
    private String name;
    private String categoryName;
    private int sold;
    private double revenue;
}