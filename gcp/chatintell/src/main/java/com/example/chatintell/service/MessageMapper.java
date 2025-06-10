package com.example.chatintell.service;

import com.example.chatintell.entity.MessageResponse;
import com.example.chatintell.entity.Messages;
import com.example.chatintell.file.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper {
    public MessageResponse toMessageResponse(Messages message) {
        return MessageResponse.builder()
                .id(message.getMessageId())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .type(message.getType())
                .state(message.getState())
                .createdAt(message.getCreatedDate())
                .media(FileUtils.readFileFromLocation(message.getMediaFilePath()))
                .build();
    }
}
