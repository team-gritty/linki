package com.ssg.chatservice.domain.chat.service;

import com.ssg.chatservice.client.ChatInfoResponse;
import com.ssg.chatservice.domain.chat.dto.ChatDTO;
import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;
import com.ssg.chatservice.domain.chat.dto.respone.ChatDetailResponeDTO;
import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.entity.Chat;

import java.util.List;
import java.util.Map;

public interface ChatService {


    //제안서아이디로  채팅방 조회
    Chat findByProposalId(String proposalId);
    //제안서 아이디로 ChatDetailDTO 반환
    ChatDetailDTO getChatDtoByProposalId(String token,String proposalId);
    //제안서 아이디로 채팅방 생성
    ChatDTO createRoom(String proposalId);
    //캠페인 아이디로 채팅 정보 조회
    List<ChatInfoResponse> chatInfoResponses(String token, String campaignId);
    //채팅 정보로 채팅방 조회
    List<Chat> chatInfoGetChat(List<ChatInfoResponse> chatInfoResponses);
    //chatDTOList 빌더
    List<ChatDTO> chatDTOs (List<Chat> chats ,List<ChatInfoResponse> chatInfos,Map<String, ChatMessageDTO> lastMessages);
    //캠페인 아이디로 채팅목록 조회
    List<ChatDTO> campaignToChatList(String token, String campaignId);
    //제안서에 해당하는 채팅방 상태를 활성으로 변경
    Chat activateRoomByProposal(String proposalId);
    //채팅아이디로 채팅방 조회
    ChatDetailDTO getChatDtoByChatId(String token,String chatId);
    //제안서에 해당하는 채팅방 소프트삭제
    Chat softDeleteChat(String proposalId);
}
