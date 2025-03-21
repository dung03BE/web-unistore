package com.dung.UniStore.repository;


import com.dung.UniStore.entity.Recycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IRecycleRepository extends JpaRepository<Recycle,Integer> {

    @Query(value = "SELECT * FROM recycle_requests r WHERE r.user_id = :userId", nativeQuery = true)
    List<Recycle> findAllByUserId(@Param("userId") Long userId);


}
