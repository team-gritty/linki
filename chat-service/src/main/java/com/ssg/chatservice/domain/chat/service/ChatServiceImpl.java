package com.ssg.chatservice.domain.chat.service;

import com.ssg.chatservice.client.ChatApiClient;
import com.ssg.chatservice.client.ChatInfoResponse;
import com.ssg.chatservice.client.PartnerApiClient;
import com.ssg.chatservice.client.PartnerInfoResponse;
import com.ssg.chatservice.domain.chat.dto.ChatDTO;
import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;
import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.domain.chat.enums.ErrorCode;
import com.ssg.chatservice.domain.chat.enums.NegoStatus;
import com.ssg.chatservice.domain.chat.repository.ChatRepository;
import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.domain.message.service.MessageService;
import com.ssg.chatservice.entity.Chat;
import com.ssg.chatservice.exception.ChatException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService{
    //repo
    private final ChatRepository chatRepository;
    private final MessageService messageService;
    //feign client
    private final PartnerApiClient partnerApiClient;
    private final ChatApiClient chatApiClient;

    private final ModelMapper modelMapper;

    //제안서 아이디로 채팅방 조회 및 DTO 반환
    @Override
    public ChatDetailDTO findByProposalId(String token,String proposalId){
        //제안서 아이디로 채팅방 조회
        Chat chat =  chatRepository.findByProposalId(proposalId);
        if(chat == null){
            throw new ChatException(ErrorCode.CHATROOM_NOT_FOUND);
        }
       PartnerInfoResponse partner = partnerApiClient.getPartnerInfo(token, chat.getProposalId());

        if(partner == null){
            throw new ChatException(ErrorCode.PARTNER_API_FAILED);
        }

        return ChatDetailDTO.builder()
                .chatId(chat.getChatId())
                .chatStatus(chat.getChatStatus())
                .proposalId(chat.getProposalId())
                .partnerId(partner.getPartnerId())
                .partnerName(partner.getPartnerName())
                .negoStatus(NegoStatus.valueOf(partner.getNegoStatus()))
                .profileImage(partner.getProfileImage())
                .channelName(partner.getChannelName())
                .build();
    }

    //채팅방 생성
    @Override
    @Transactional
    public String createRoom(String proposalId) {
        //DB에서 제안서 아이디를 기준으로 채팅방 조회
        Chat chat = chatRepository.findByProposalId(proposalId);
        //이미 존재하는 채팅방이면 예외처리
        if(chat != null){
            throw new ChatException(ErrorCode.CHATROOM_ALREADY_EXIST);
        }
        // 비활성 상태의 채팅방 생성하여 DB에 저장
        chat = Chat.builder()
                .chatDate(Instant.now())
                .chatStatus(ChatStatus.PENDING)
                .proposalId(proposalId)
                .build();
        chatRepository.save(chat);

        return chat.getChatId();
    }




    //광고주의 채팅 목록 조회
    @Override
    public List<ChatDTO> AdvertiserChatList(String token, String campaignId) {
        List<ChatDTO> advertiserChatList = new ArrayList<>();

        // 1. 채팅 정보 조회
        List<ChatInfoResponse> chatInfos = chatApiClient.getChatInfo(token, campaignId);
        List<String> proposalIds = chatInfos.stream()
            .map(ChatInfoResponse::getProposalId)
            .collect(Collectors.toList());

        // 2. 채팅방 조회 및 맵핑
        List<Chat> chats = chatRepository.findByProposalIdIn(proposalIds);
        // 3. 마지막 메시지 조회
        Map<String, ChatMessageDTO> lastMessages = messageService.lastMessage(chats);

        // 4. ChatDTO 리스트 생성
        for(int i = 0; i< chatInfos.size(); i++){
            ChatDTO chatdto = ChatDTO.builder()
                    .chatId((chats.get(i).getChatId()))
                    .opponentId(chatInfos.get(i).getOpponentId())
                    .opponentName(chatInfos.get(i).getOpponentName())
                    .lastMessage(lastMessages.get(chats.get(i).getChatId()).getContent())
                    .lastMessageTime(lastMessages.get(chats.get(i).getChatId()).getMessageDate())
                    .isNew(lastMessages.get(chats.get(i).getChatId()).isMessageRead())
                    .proposalId(chats.get(i).getProposalId())
                    .build();
            advertiserChatList.add(chatdto);
        }
        return advertiserChatList;
    }


}
