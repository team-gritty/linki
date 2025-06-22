package com.Gritty.Linki.client.chatClient.dto;


import com.Gritty.Linki.entity.Campaign;
import com.Gritty.Linki.entity.RedirectLinks;
import com.Gritty.Linki.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertiserDto {

    private String advertiserId;
    private String businessNumber;
    private String companyName;
    private String userId;
    private User user;
    private List<Campaign> campaigns = new ArrayList<>();
    private List<RedirectLinks> redirectLinks = new ArrayList<>();



}
