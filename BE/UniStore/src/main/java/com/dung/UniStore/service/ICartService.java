package com.dung.UniStore.service;

import com.dung.UniStore.dto.response.CartItemResponse;
import com.dung.UniStore.dto.response.CartResponse;
import com.dung.UniStore.entity.CartItem;
import com.dung.UniStore.exception.ApiException;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ICartService {

    List<CartResponse> getAllCarts() throws ApiException;

    CartResponse getCart(String emailId, Long cartId) throws ApiException;

    @Transactional
    CartResponse updateProductQuantityInCart(Long productId, int quantityChange,String color) throws ApiException;
    List<CartItemResponse> getCartItemsByUserId(Long userId);
    CartResponse addProToCart(Long productId, Integer quantity, String color) throws Exception;

    CartResponse deleteCartByUserId(Long userId) throws ApiException;

    void deleteCartItemByUserId(Long userID, Long cartItemId) throws ApiException;
}
