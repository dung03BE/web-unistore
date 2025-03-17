package com.dung.UniStore.dto.response.DashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardOverviewDTO {
    private Double totalRevenue;
    private Integer totalOrders;
    private Integer newCustomers;
    private Integer totalEmployees;
}