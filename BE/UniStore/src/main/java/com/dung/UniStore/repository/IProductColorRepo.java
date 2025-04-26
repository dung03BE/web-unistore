package com.dung.UniStore.repository;

import com.dung.UniStore.dto.response.ProductColorResponse;
import com.dung.UniStore.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IProductColorRepo extends JpaRepository<ProductColor,Integer> {
    List<ProductColor> findByProduct_Id(int id);

    void deleteByProduct_Id(int id);

    <T> Optional<T> findByProductIdAndColor(int id, String color);
}
