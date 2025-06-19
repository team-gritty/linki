package com.ssg.chatservice.domain.chat.service;

import com.ssg.chatservice.domain.chat.dto.ChatDTO;
import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;

import java.util.List;

public interface ChatService {

    //제안서 아이디로 채팅방 조회
    ChatDetailDTO findByProposalId(String token,String proposalId);
    //제안서 아이디로 채팅방 생성
    String createRoom(String proposalId);
    //캠페인 아이디로 채팅 목록 조회
    List<ChatDTO> AdvertiserChatList(String token, String campaignId );

    //
}
