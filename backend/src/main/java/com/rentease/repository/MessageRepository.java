package com.rentease.repository;

import com.rentease.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
           "(m.sender.id = :user2Id AND m.receiver.id = :user1Id) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findChatHistory(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END " +
           "FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<Long> findDistinctConversationPartnerIds(@Param("userId") Long userId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);
}

