package com.dung.UniStore.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
public class CouponRequest {
    private String code;
    private String discountType; // "percentage" hoặc "fixed"
    private BigDecimal discountValue;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private int userLimit;   // Mỗi user dùng tối đa bao nhiêu lần
    private int usageLimit;  // Tổng lượt dùng toàn hệ thống

    private Long userId;
}
