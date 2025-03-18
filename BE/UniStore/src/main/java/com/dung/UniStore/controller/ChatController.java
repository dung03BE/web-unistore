package com.dung.UniStore.controller;

import com.dung.UniStore.entity.ChatMessage;
import com.dung.UniStore.entity.Comment;
import com.dung.UniStore.repository.ICommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {
    @Autowired
    private ICommentRepository commentRepository;
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessage.MessageType.CHAT) {
            Comment comment = new Comment();
            comment.setProductId(chatMessage.getProductId()); // Thay bằng productId thực tế
            comment.setUsername(chatMessage.getSender());
            comment.setContent(chatMessage.getContent());
            comment.setTimestamp(LocalDateTime.now());
            commentRepository.save(comment);
        }
        return chatMessage;
    }
    @MessageMapping("/chat.join")
    @SendTo("/topic/public")
    public ChatMessage join(@Payload ChatMessage chatMessage) {
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        return chatMessage;
    }
}