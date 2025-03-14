package com.dung.UniStore.repository;

import com.dung.UniStore.entity.Cart;
import com.dung.UniStore.entity.CartItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICartRepository extends JpaRepository<Cart,Long> {
    @Query("SELECT c from Cart c where c.user.email=?1")
    Cart findCartByEmail(String email);
    @Query("SELECT c from Cart c where c.user.email=?1 AND c.cartId=?2" )
    Cart findCartByEmailAndCartId(String emailId, Long cartId);

    Optional<Object> findByUserId(int userId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.cartId = :cartId")
    void deleteByCartId(Long cartId);

    Cart findCartByUserId(Long loggedInUserId);
}
