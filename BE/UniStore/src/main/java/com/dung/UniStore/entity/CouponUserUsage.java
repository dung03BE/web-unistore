package com.dung.UniStore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_user_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUserUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}
