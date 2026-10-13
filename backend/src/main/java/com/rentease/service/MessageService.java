package com.rentease.service;

import com.rentease.dto.ChatMessageDto;
import com.rentease.dto.ConversationSummaryDto;
import com.rentease.entity.Message;
import com.rentease.entity.Property;
import com.rentease.entity.User;
import com.rentease.exception.ResourceNotFoundException;
import com.rentease.repository.MessageRepository;
import com.rentease.repository.PropertyRepository;
import com.rentease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatMessageDto sendMessage(Long senderId, Long receiverId, Long propertyId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found with id: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with id: " + receiverId));

        Property property = null;
        if (propertyId != null) {
            property = propertyRepository.findById(propertyId).orElse(null);
        }

        Message message = new Message(sender, receiver, property, content);
        message.setCreatedAt(LocalDateTime.now());
        Message saved = messageRepository.save(message);

        ChatMessageDto dto = ChatMessageDto.fromEntity(saved);

        // Real-time broadcast via WebSocket STOMP
        try {
            messagingTemplate.convertAndSend("/topic/messages/" + receiverId, dto);
            messagingTemplate.convertAndSend("/topic/messages/" + senderId, dto);
        } catch (Exception ignored) {
            // Log/ignore if WebSocket client is not connected
        }

        return dto;
    }

    @Transactional
    public List<ChatMessageDto> getChatHistory(Long currentUserId, Long partnerId) {
        List<Message> messages = messageRepository.findChatHistory(currentUserId, partnerId);

        // Mark incoming messages as read
        for (Message msg : messages) {
            if (msg.getReceiver().getId().equals(currentUserId) && !msg.isRead()) {
                msg.setRead(true);
            }
        }
        messageRepository.saveAll(messages);

        return messages.stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ConversationSummaryDto> getConversations(Long currentUserId) {
        List<Long> partnerIds = messageRepository.findDistinctConversationPartnerIds(currentUserId);
        List<ConversationSummaryDto> result = new ArrayList<>();

        for (Long partnerId : partnerIds) {
            userRepository.findById(partnerId).ifPresent(partner -> {
                List<Message> history = messageRepository.findChatHistory(currentUserId, partnerId);
                if (!history.isEmpty()) {
                    Message lastMsg = history.get(history.size() - 1);
                    long unread = history.stream()
                            .filter(m -> m.getReceiver().getId().equals(currentUserId) && !m.isRead())
                            .count();

                    result.add(new ConversationSummaryDto(
                            partner.getId(),
                            partner.getName(),
                            partner.getEmail(),
                            lastMsg.getContent(),
                            lastMsg.getCreatedAt(),
                            unread
                    ));
                }
            });
        }

        result.sort((a, b) -> {
            if (a.getLastMessageTime() == null || b.getLastMessageTime() == null) return 0;
            return b.getLastMessageTime().compareTo(a.getLastMessageTime());
        });

        return result;
    }
}
