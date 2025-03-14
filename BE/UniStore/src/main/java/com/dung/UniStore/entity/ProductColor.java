package com.dung.UniStore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "productcolor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String color; // Lưu trực tiếp tên màu

    @ManyToOne
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    private Product product;
}