package com.ssg.subscribeservice.dto;

import com.ssg.subscribeservice.entity.SubscribeEntity;
import com.ssg.subscribeservice.subsenum.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSubscribeDto {
    private String userSubscriptionId;
    private String userId;
    private SubscribeEntity subscribe;
    private LocalDateTime userSubscribeStartedAt;
    private LocalDateTime userSubscribeNextBillingAt;
    private SubscriptionStatus userSubscribeStatus;
    private Boolean userSubscribeBillingRegistered;
}
