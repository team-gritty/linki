package com.Gritty.Linki.domain.user.mypage.dto;

import lombok.Data;

import java.util.List;

@Data
public class YoutubeApiResponse {
    private List<YoutubeChannelInfo> items;
}
