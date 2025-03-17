package com.dung.UniStore.controller;

import com.dung.UniStore.dto.response.DashboardDTO.DashboardOverviewDTO;
import com.dung.UniStore.dto.response.DashboardDTO.MonthlyRevenueDTO;
import com.dung.UniStore.dto.response.DashboardDTO.TopProductTypeDTO;
import com.dung.UniStore.dto.response.DashboardDTO.TopSellingProductDTO;
import com.dung.UniStore.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    // 1. API tổng quan (gộp)
    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewDTO> getDashboardOverview() {
        return ResponseEntity.ok(dashboardService.getDashboardOverview());
    }

    // 2. Doanh thu theo từng tháng
    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyRevenueDTO>> getMonthlyRevenue() {
        return ResponseEntity.ok(dashboardService.getMonthlyRevenue());
    }

    // 3. Top 5 loại sản phẩm bán chạy nhất
    @GetMapping("/top-product-types")
    public ResponseEntity<List<TopProductTypeDTO>> getTopProductTypes() {
        return ResponseEntity.ok(dashboardService.getTopProductTypes());
    }

    // 4. Top 5 sản phẩm bán chạy nhất
    @GetMapping("/top-selling-products")
    public ResponseEntity<List<TopSellingProductDTO>> getTopSellingProducts() {
        return ResponseEntity.ok(dashboardService.getTopSellingProducts());
    }
}
