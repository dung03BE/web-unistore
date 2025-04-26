package com.dung.UniStore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GeneratedColumn;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;
    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<CartItem> cartItems= new ArrayList<>();

    private Double totalPrice=0.0;
}
