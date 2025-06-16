package com.ssg.chatservice.entity;

import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.util.IdGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Chat {

    @Id
    private String chatId;

    @Column(nullable = false)
    private Instant chatDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatStatus chatStatus;

    @Column(nullable = false)
    private String proposalId;

    //영속성 저장 시 지정된 아이디가 없으면 생성하여 저장, 있으면 입력값으로 저장
    @PrePersist
    public void prePersist() {
        if (this.chatId == null) {
            this.chatId = IdGenerator.chatId();
        }
    }

}
