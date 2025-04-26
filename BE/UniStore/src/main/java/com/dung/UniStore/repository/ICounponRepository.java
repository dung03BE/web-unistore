package com.dung.UniStore.repository;

import com.dung.UniStore.entity.Counpons;
import com.dung.UniStore.entity.Status;
import com.dung.UniStore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ICounponRepository extends JpaRepository<Counpons,Integer> {
    Counpons findByUserId(Long userId);

    boolean existsByUserAndCode(User user, String code);


    @Query("""
    SELECT c FROM Counpons c
    WHERE c.code = :code AND (c.user.id = :userId OR c.user IS NULL)
    ORDER BY c.user.id DESC
    """)
    List<Counpons> findByCodeForUser(@Param("code") String code, @Param("userId") Long userId);
    @Query(value = "SELECT * FROM coupons c " +
            "WHERE c.code = :code " +
            "AND c.status = :status " +
            "AND c.start_date <= :now " +
            "AND c.end_date >= :now " +
            "AND (c.user_id = :userId OR c.user_id IS NULL)",
            nativeQuery = true)
    Optional<Counpons> findValidCouponForUserNative(@Param("code") String code,
                                                    @Param("status") String status,
                                                    @Param("now") LocalDateTime now,
                                                    @Param("userId") Long userId);

    Counpons findByCodeAndUserId(String code, int userId);
}
