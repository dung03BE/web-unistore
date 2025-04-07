package com.dung.UniStore.repository;

import com.dung.UniStore.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductIdOrderByTimestampDesc(Long productId);
}
