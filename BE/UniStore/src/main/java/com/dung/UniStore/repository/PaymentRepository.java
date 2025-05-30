package com.dung.UniStore.repository;

import com.dung.UniStore.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    Payment findFirstByOrderId(int id);
}
