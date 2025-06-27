package com.Gritty.Linki.client.chatClient.dto;

import com.Gritty.Linki.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfluencerDto {
    private String influencerId;
    private String userId;
    private User user;
    private String profileImage;
    public String getInfluencerName() {
        return user != null ? user.getUserName() : null;
    }
}
