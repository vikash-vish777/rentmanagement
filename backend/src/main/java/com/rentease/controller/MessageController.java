package com.rentease.controller;

import com.rentease.dto.ApiResponse;
import com.rentease.dto.ChatMessageDto;
import com.rentease.dto.ConversationSummaryDto;
import com.rentease.entity.User;
import com.rentease.service.AuthService;
import com.rentease.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private AuthService authService;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationSummaryDto>> getConversations(Authentication authentication) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        return ResponseEntity.ok(messageService.getConversations(user.getId()));
    }

    @GetMapping("/chat/{partnerId}")
    public ResponseEntity<List<ChatMessageDto>> getChatHistory(
            @PathVariable Long partnerId,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        return ResponseEntity.ok(messageService.getChatHistory(user.getId(), partnerId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> sendMessage(
            @RequestBody Map<String, Object> payload,
            Authentication authentication
    ) {
        User user = authService.getCurrentUserEntity(authentication.getName());
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());
        Long propertyId = payload.get("propertyId") != null ? Long.valueOf(payload.get("propertyId").toString()) : null;
        String content = (String) payload.get("content");

        ChatMessageDto message = messageService.sendMessage(user.getId(), receiverId, propertyId, content);
        return ResponseEntity.ok(new ApiResponse(true, "Message sent", message));
    }
}
