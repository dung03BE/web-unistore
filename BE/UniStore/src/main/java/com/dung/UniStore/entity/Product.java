package com.dung.UniStore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="products")
@Getter
@Setter
@NoArgsConstructor

//listen exchance from PRODUCTlISTENER

public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="name", length = 350)
    private String name;
    @Column(name="price",nullable = false)
    private float price;
//    @Column(length = 350)
//    private String thumbnail ;
    private String description;
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("product-image")  //Cho phía "cha" trong mối quan hệ (sẽ được serialize)
    private List<ProductImage> images; // Đổi tên thành 'images' cho rõ ràng hơn
    @ManyToOne
    @JoinColumn(name="category_id" ,referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "product",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.EAGER)
    @JsonIgnore
    private List<CartItem> products = new ArrayList<>();
    @Column(name="discount")
    private Double discount;
    @Column(name= "speacial_price")
    private Double specialPrice;
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductColor> colors;
    private String brand;
    private String model;
    @Column(columnDefinition = "TINYINT")
    private Byte available;

}
