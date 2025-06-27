package com.ssg.chatservice.domain.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PostDTO {
    private String title;
    private String content;
    private String author;
    private String template;
    private LocalDateTime createdAt;
    private int writeId;
}
