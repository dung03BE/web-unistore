package com.dung.UniStore.repository;

import com.dung.UniStore.entity.CouponUserUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUserUsageRepository extends JpaRepository<CouponUserUsage,Integer> {
    boolean existsByCouponIdAndUserId(Long id, int id1);
}
