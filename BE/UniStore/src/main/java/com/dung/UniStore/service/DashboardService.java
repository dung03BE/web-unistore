package com.dung.UniStore.service;

import com.dung.UniStore.dto.response.DashboardDTO.DashboardOverviewDTO;
import com.dung.UniStore.dto.response.DashboardDTO.MonthlyRevenueDTO;
import com.dung.UniStore.dto.response.DashboardDTO.TopProductTypeDTO;
import com.dung.UniStore.dto.response.DashboardDTO.TopSellingProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    // 1. API tổng quan (GỘP)
    public DashboardOverviewDTO getDashboardOverview() {
        String revenueQuery = "SELECT SUM(total_money) FROM orders " +
                "WHERE MONTH(order_date) = MONTH(CURDATE()) AND YEAR(order_date) = YEAR(CURDATE())";

        String ordersQuery = "SELECT COUNT(*) FROM orders " +
                "WHERE MONTH(order_date) = MONTH(CURDATE()) AND YEAR(order_date) = YEAR(CURDATE())";

        String customersQuery = "SELECT COUNT(*) FROM users " +
                "WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE())";

        String employeesQuery = "SELECT COUNT(*) FROM users WHERE role_Id=3";

        Double totalRevenue = jdbcTemplate.queryForObject(revenueQuery, Double.class);
        Integer totalOrders = jdbcTemplate.queryForObject(ordersQuery, Integer.class);
        Integer newCustomers = jdbcTemplate.queryForObject(customersQuery, Integer.class);
        Integer totalEmployees = jdbcTemplate.queryForObject(employeesQuery, Integer.class);

        return new DashboardOverviewDTO(totalRevenue, totalOrders, newCustomers, totalEmployees);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    // 2. Doanh thu theo từng tháng
    public List<MonthlyRevenueDTO> getMonthlyRevenue() {
        String sql = "SELECT MONTH(order_date) AS month, SUM(total_money) AS revenue " +
                "FROM orders WHERE YEAR(order_date) = YEAR(CURDATE()) " +
                "GROUP BY MONTH(order_date) ORDER BY month";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new MonthlyRevenueDTO(rs.getInt("month"), rs.getDouble("revenue"))
        );
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    // 3. Top 5 loại sản phẩm bán chạy nhất
    public List<TopProductTypeDTO> getTopProductTypes() {
        String sql = "SELECT p.name AS type, SUM(oi.quantity) AS value " +
                "FROM order_details oi JOIN products p ON oi.product_id = p.id " +
                "GROUP BY oi.product_id ORDER BY value DESC LIMIT 5";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new TopProductTypeDTO(rs.getString("type"), rs.getInt("value"))
        );
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    // 4. Top 5 sản phẩm bán chạy nhất
    public List<TopSellingProductDTO> getTopSellingProducts() {
        String sql = "SELECT p.name AS product_name, c.name  AS category_name, SUM(oi.quantity) AS sold, " +
                "SUM(oi.quantity * oi.price) AS revenue FROM order_details oi " +
                "JOIN products p ON oi.product_id = p.id JOIN categories c ON c.id = p.category_id" +
                " GROUP BY oi.product_id " +
                "ORDER BY revenue DESC LIMIT 5";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new TopSellingProductDTO(
                        rs.getString("product_name"),   // Lấy tên sản phẩm
                        rs.getString("category_name"), // Lấy tên danh mục
                        rs.getInt("sold"),
                        rs.getDouble("revenue")
                )
        );
    }
}