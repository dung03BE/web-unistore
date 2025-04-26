package com.dung.UniStore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "coupons")
public class Counpons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType;

    @Column(name = "discount_value")
    private BigDecimal discountValue;
    @Column(name="start_date")
    private LocalDateTime startDate;
    @Column(name="end_date")
    private LocalDateTime endDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    public  enum DiscountType {
        percentage,fixed_amount
    }
    @Column(name = "usage_limit")
    private Integer usageLimit; // tổng số lượt cho phép
    @Column(name = "used_count")
    private Integer usedCount = 0; // mặc định là 0

    @Column(name = "user_limit")
    private Integer userLimit = 1; // mỗi user dùng tối đa mấy lần


}
