package com.dung.UniStore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Liên kết với đơn hàng

    @Column(name = "transaction_id", unique = true)
    private String transactionId; // Mã giao dịch từ VNPay/Momo/PayPal

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod; // Phương thức thanh toán (VNPay, Momo, COD)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status; // Trạng thái thanh toán

    @Column(name = "amount", nullable = false)
    private BigDecimal amount; // Số tiền thanh toán

    @Column(name = "payment_time")
    private LocalDateTime paymentTime; // Thời gian thanh toán
}
