package com.dung.UniStore.repository;


import com.dung.UniStore.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role,Integer> {
    Optional<Object> findByName(String user);
}
