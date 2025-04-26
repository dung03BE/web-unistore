package com.dung.UniStore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;  // Liên kết với sản phẩm

    @ManyToOne
    @JoinColumn(name = "productcolor_id", referencedColumnName = "id")
    private ProductColor productColor;  // Liên kết với màu sắc sản phẩm

    private int quantity;  // Số lượng theo màu sắc
}
