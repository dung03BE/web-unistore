package com.dung.UniStore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "total_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;  // Liên kết với sản phẩm

    private int quantity;  // Số lượng tổng của sản phẩm (không phân biệt màu)
}