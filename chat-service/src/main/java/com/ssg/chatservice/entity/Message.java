package com.ssg.chatservice.entity;

import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.ssg.chatservice.util.IdGenerator;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "message")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Message{

    @Id
    private String messageId;

    public static String messageId() {
        return IdGenerator.messageId();
    }

    @Field("chat_id")
    private String chatId;
    @Field("message_content")
    private String messageContent;
    //mongoDB의 date
    @Field("message_date")
    private Instant messageDate;
    @Field("message_read")
    private boolean messageRead;
    @Field("message_sender_id")
    private String messageSenderId;
    @Field("message_type")
    private String messageType;

    public void initializeIdIfNull() {
        if (this.messageId == null) {
            this.messageId = IdGenerator.messageId();
        }
    }

}

