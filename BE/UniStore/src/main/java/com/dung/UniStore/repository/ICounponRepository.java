package com.dung.UniStore.repository;

import com.dung.UniStore.entity.Counpons;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICounponRepository extends JpaRepository<Counpons,Integer> {
    Counpons findByUserId(Long userId);
}
