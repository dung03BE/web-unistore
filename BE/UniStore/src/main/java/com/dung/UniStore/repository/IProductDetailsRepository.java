package com.dung.UniStore.repository;


import com.dung.UniStore.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IProductDetailsRepository extends JpaRepository<ProductDetails,Integer> {

    ProductDetails findByProductId(int id);
}
