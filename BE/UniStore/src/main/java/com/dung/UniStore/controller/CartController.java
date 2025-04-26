package com.dung.UniStore.controller;

import com.dung.UniStore.dto.response.CartItemResponse;
import com.dung.UniStore.dto.response.CartResponse;

import com.dung.UniStore.entity.Cart;
import com.dung.UniStore.entity.CartItem;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.exception.ErrorCode;
import com.dung.UniStore.repository.ICartRepository;
import com.dung.UniStore.service.ICartService;
import com.dung.UniStore.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {
    private final ICartService cartService;
    private final AuthUtil authUtil;
    private final ICartRepository cartRepository;

    @PostAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER','ROLE_EMPLOYEE')")
    @PostMapping("/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartResponse> addProToCart(
            @PathVariable Long productId,
            @PathVariable Integer quantity,
            @RequestParam String color // Thêm tham số color từ query parameter
    ) throws Exception {
        CartResponse cartResponse = cartService.addProToCart(productId, quantity, color); // Truyền color vào service
        return new ResponseEntity<>(cartResponse, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<CartResponse>> getAllCarts() throws ApiException {
        List<CartResponse> carts = cartService.getAllCarts();
        return new ResponseEntity<List<CartResponse>>(carts, HttpStatus.FOUND);
    }

    @GetMapping("/users/cart")
    public ResponseEntity<CartResponse> getCartById() throws Exception {
        String emailId = authUtil.loggedInEmail();
        Long cartId;
        try {
            Cart cart = cartRepository.findCartByEmail(emailId);
            cartId = cart.getCartId();
        }
        catch (Exception e)
        {
            throw  new ApiException("Cart null");
        }


        CartResponse cartResponse = cartService.getCart(emailId, cartId);
        return new ResponseEntity<CartResponse>(cartResponse, HttpStatus.OK);

    }

    @PutMapping("/products/{productId}/quantity")
    public ResponseEntity<CartResponse> updateCartProduct(@PathVariable Long productId, @RequestParam int quantityChange, @RequestParam String color) throws ApiException {


        CartResponse cartResponse = cartService.updateProductQuantityInCart(productId, quantityChange, color);

        // Kiểm tra nếu giỏ hàng đã được cập nhật thành công
        if (cartResponse == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Nếu không tìm thấy giỏ hàng
        }

        return new ResponseEntity<>(cartResponse, HttpStatus.OK);

    }

    @GetMapping("/cart")
    public ResponseEntity<List<CartItemResponse>> getCartByUserId() {

        Long userID = authUtil.loggedInUserId();
        List<CartItemResponse> cartItems = cartService.getCartItemsByUserId(userID);
        return ResponseEntity.ok(cartItems);
    }
    @DeleteMapping("")
    public ResponseEntity<CartResponse> deleteCartByUserId() throws ApiException {
        Long userID = authUtil.loggedInUserId();
        CartResponse cartResponse = cartService.deleteCartByUserId(userID);
        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }
    @DeleteMapping("cartItemId/{cartItemId}")
    public ResponseEntity<String> deleteCartItemByUserId(@PathVariable Long cartItemId) throws ApiException {
        Long userID = authUtil.loggedInUserId();
        cartService.deleteCartItemByUserId(userID, cartItemId);
        return ResponseEntity.ok("Cart item deleted successfully.");
    }

}
