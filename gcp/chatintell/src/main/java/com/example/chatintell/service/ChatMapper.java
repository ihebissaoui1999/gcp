package com.example.chatintell.service;

import com.example.chatintell.dto.ChatResponse;
import com.example.chatintell.entity.Chat;
import org.springframework.stereotype.Service;

@Service
public class ChatMapper {
public ChatResponse toChatResponse(Chat chat , String senderId ) {
    return ChatResponse.builder()
            .id(chat.getChatid())
            .name(chat.getChatName(senderId))
            .unreadCount(chat.getUnreadMessages(senderId))
            .lastMessage(chat.getLastMessage())
            .lastMessageTime(chat.getLastMessageTime())
            .isRecipientOnline(chat.getRecipient().isUserOnline())
            .senderId(chat.getSender().getUserid())
            .receiverId(chat.getRecipient().getUserid())
            .build();
}
}
