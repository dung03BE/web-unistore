package com.dung.UniStore.dto.response;

import com.dung.UniStore.entity.CartItem;
import com.dung.UniStore.entity.Product;
import com.dung.UniStore.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private Long cartId;

    private Long UserId;
    private List<ProductResponse> products= new ArrayList<>();

    private Double totalPrice=0.0;
}
