package com.rentease.dto;

import com.rentease.entity.Message;
import java.time.LocalDateTime;

public class ChatMessageDto {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private Long propertyId;
    private String propertyTitle;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;

    public ChatMessageDto() {
    }

    public static ChatMessageDto fromEntity(Message message) {
        if (message == null) return null;
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(message.getId());
        if (message.getSender() != null) {
            dto.setSenderId(message.getSender().getId());
            dto.setSenderName(message.getSender().getName());
        }
        if (message.getReceiver() != null) {
            dto.setReceiverId(message.getReceiver().getId());
            dto.setReceiverName(message.getReceiver().getName());
        }
        if (message.getProperty() != null) {
            dto.setPropertyId(message.getProperty().getId());
            dto.setPropertyTitle(message.getProperty().getTitle());
        }
        dto.setContent(message.getContent());
        dto.setRead(message.isRead());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
