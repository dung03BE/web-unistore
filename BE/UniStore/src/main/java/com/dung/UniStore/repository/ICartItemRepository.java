package com.dung.UniStore.repository;

import com.dung.UniStore.entity.Cart;
import com.dung.UniStore.entity.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICartItemRepository extends JpaRepository<CartItem,Long> {
//    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = ?1 AND ci.product.id = ?2")
//    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);
    @Query("SELECT c from Cart c where c.user.email=?1 AND c.cartId=?2" )
    Cart findCartByEmailAndCartId(String emailId, Long cartId);
    @Query("SELECT ci FROM CartItem ci " +
            "JOIN FETCH ci.product p " +
            "JOIN FETCH p.colors pc " +
            "JOIN FETCH ci.cart c " +
            "JOIN FETCH c.user u " +
            "WHERE u.id = :userId")
    List<CartItem> findCartItemByUserId(@Param("userId") Long userId);
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.product.id = :productId")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.product.id = :productId AND ci.color = :color")
    CartItem findCartItemByProductIdAndCartIdAndColor(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("color") String color);

    List<CartItem> findByCartCartId(Long cartId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.cartItemId = :cartItemId")
    void deleteByCartItemId(@Param("cartItemId") Long cartItemId);

    List<CartItem> findByCart_CartId(Long cartId);
}
