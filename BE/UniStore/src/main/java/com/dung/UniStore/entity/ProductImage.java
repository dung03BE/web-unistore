package com.dung.UniStore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {
    public static  final int Maximum_Images_Per_Product=5;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name="product_id", referencedColumnName = "id")
    @JsonBackReference("product-image")
    private Product product;

    @Column(name="image_url")
    private String imageUrl;
    public ProductImage(Product product, String imageUrl) {
        this.product = product;
        this.imageUrl = imageUrl;
    }
}

