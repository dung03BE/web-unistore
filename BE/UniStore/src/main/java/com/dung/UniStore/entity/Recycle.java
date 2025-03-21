package com.dung.UniStore.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="recycle_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Recycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "device_type")
    private String deviceType;
    @Column(name = "device_condition")
    private String deviceCondition;
    @Column(name = "pickup_method")
    private String pickupMethod;
    private String status ;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;
    private String imageUrl;
    @Column(name = "coupon_generated")
    private Boolean couponGenerated = false;
}
