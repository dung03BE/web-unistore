package com.dung.UniStore.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "productdetails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @JsonBackReference("product-details")
    private Product product;
    private String screen_size;
    private String resolution;
    private String processor;
    private String ram;
    private String storage;
    private String battery;
    private String camera;
    private String os;
    private String weight;
    private String dimensions;
    private String sim;
    private String network;
}