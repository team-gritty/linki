package com.ssg.chatservice.domain.chat.service;

import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;
public interface ChatService {

    //제안서 아이디로 채팅방 조회 및 DTO 반환
    ChatDetailDTO findByProposalId(String token,String proposalId);
    String createRoom(String proposalId);

}
