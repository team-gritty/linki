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
    private String chatId;
    private String messageContent;

    //mongoDB의 date
    @Field("messageDate")
    private Instant messageDate;

    private boolean messageRead;
    private String messageSenderId;
    private String messageType;
    //영속성 저장 시 지정된 아이디가 없으면 생성하여 저장, 있으면 입력값으로 저장
    @PrePersist
    public void prePersist() {
        if (this.messageId == null) {
            this.messageId = IdGenerator.chatId();
        }
    }

}

