package com.rentease.dto;

import java.time.LocalDateTime;

public class ConversationSummaryDto {
    private Long partnerId;
    private String partnerName;
    private String partnerEmail;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private long unreadCount;

    public ConversationSummaryDto() {
    }

    public ConversationSummaryDto(Long partnerId, String partnerName, String partnerEmail, String lastMessage, LocalDateTime lastMessageTime, long unreadCount) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.partnerEmail = partnerEmail;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
