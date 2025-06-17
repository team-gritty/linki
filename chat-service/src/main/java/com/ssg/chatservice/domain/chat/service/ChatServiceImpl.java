package com.ssg.chatservice.domain.chat.service;

import com.ssg.chatservice.client.PartnerApiClient;
import com.ssg.chatservice.client.PartnerInfoResponse;
import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;
import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.domain.chat.enums.ErrorCode;
import com.ssg.chatservice.domain.chat.enums.NegoStatus;
import com.ssg.chatservice.domain.chat.repository.ChatRepository;
import com.ssg.chatservice.entity.Chat;
import com.ssg.chatservice.exception.ChatException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService{
    private final ChatRepository chatRepository;
    private final PartnerApiClient partnerApiClient;
    private final ModelMapper modelMapper;

    //제안서 아이디로 채팅방 조회 및 DTO 반환
    @Override
    public ChatDetailDTO findByProposalId(String proposalId){
        //제안서 아이디로 채팅방 조회
        Chat chat =  chatRepository.findByProposalId(proposalId);
        if(chat == null){
            throw new ChatException(ErrorCode.CHATROOM_NOT_FOUND);
        }
        //feign Client로 partner객체 조회
        PartnerInfoResponse partner = partnerApiClient.getPartnerInfo(chat.getProposalId());
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
    public ChatDetailDTO createRoom(String proposalId) {
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

        //제안서 아이디로 ChatDetailDTO 생성
        return findByProposalId(proposalId);
    }

}
