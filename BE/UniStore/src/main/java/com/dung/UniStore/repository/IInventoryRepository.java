package com.dung.UniStore.repository;

import com.dung.UniStore.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IInventoryRepository extends JpaRepository<Inventory,Integer> {
    List<Inventory> findByProductId(Integer productId);

    <T> Optional<T> findByProductIdAndProductColorId(int id, int id1);
}
