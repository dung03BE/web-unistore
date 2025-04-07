package com.dung.UniStore.repository;


import com.dung.UniStore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IOrderRepository extends JpaRepository<Order,Integer> , JpaSpecificationExecutor<Order> {

    Order findByUserId(int userId);

    List<Order> findAllByUserIdOrderByOrderDateDesc(int userId);

}
