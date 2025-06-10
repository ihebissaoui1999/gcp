package com.example.chatintell.repository;

import com.example.chatintell.entity.MessageConstants;
import com.example.chatintell.entity.MessageState;
import com.example.chatintell.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessagesRepository extends JpaRepository<Messages, String> {
    @Query(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID)
    List<Messages> findMessagesByChatId(@Param("chatId") String chatId);

    @Query(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT)
    @Modifying
    void setMessagesToSeenByChatId(@Param("chatId") String chatId, @Param("newState") MessageState state);
}
