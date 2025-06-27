package com.ssg.chatservice.domain.notification.dto;

import com.ssg.chatservice.domain.kafka.enums.EventType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDto {
    String userName;
    String message;
    EventType eventType;

    public String getMessage(){
        return userName+"님께서 "+eventType.getLabel()+"했습니다.";
    }
}
