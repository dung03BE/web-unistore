package com.dung.UniStore.repository;

import com.dung.UniStore.entity.TotalInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TotalInventoryRepository extends JpaRepository<TotalInventory, Integer> {
    Optional<TotalInventory> findByProductId(Integer productId);
}
