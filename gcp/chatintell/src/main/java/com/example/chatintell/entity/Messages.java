package com.example.chatintell.entity;

import com.example.chatintell.base.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "messages")
@NamedQuery(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID,
        query = "SELECT m FROM Messages m WHERE m.chat.id = :chatId ORDER BY m.createdDate"
)
@NamedQuery(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT,
        query = "UPDATE Messages SET state = :newState WHERE chat.id = :chatId"
)
public class Messages extends BaseAuditingEntity {
    @Id
    @SequenceGenerator(name = "mes_seq",sequenceName = "mes_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String messageId;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    private MessageState state;
    @Enumerated(EnumType.STRING)
    private MessageType type;
    @Column(nullable = false)
    private String senderId;
    @Column(nullable = false)
    private String receiverId;

    private String mediaFilePath;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

}
