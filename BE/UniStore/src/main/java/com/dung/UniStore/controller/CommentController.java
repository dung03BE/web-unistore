package com.dung.UniStore.controller;

import com.dung.UniStore.entity.Comment;
import com.dung.UniStore.repository.ICommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    @Autowired
    private ICommentRepository commentRepository;
    @GetMapping("/{productId}")
    public List<Comment> getCommentsByProductId(@PathVariable Long productId) {
        return commentRepository.findByProductIdOrderByTimestampDesc(productId);
    }
}
