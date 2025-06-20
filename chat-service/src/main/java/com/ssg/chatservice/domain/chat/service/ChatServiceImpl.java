package com.ssg.chatservice.domain.chat.service;

import com.ssg.chatservice.client.*;
import com.ssg.chatservice.domain.chat.dto.ChatDTO;
import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;
import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.domain.chat.enums.ErrorCode;
import com.ssg.chatservice.domain.chat.enums.NegoStatus;
import com.ssg.chatservice.domain.chat.repository.ChatRepository;
import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.domain.message.service.MessageService;
import com.ssg.chatservice.entity.Chat;
import com.ssg.chatservice.entity.Message;
import com.ssg.chatservice.exception.ChatException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final ChatApiClient chatApiClient;

    private final ModelMapper modelMapper;

    //제안서아이디로  채팅방 조회
    public Chat findByProposalId(String proposalId){
        return chatRepository.findByProposalId(proposalId);
    }

    //제안서 아이디로 ChatDetailDTO 반환
    @Override
    public ChatDetailDTO getChatDtoByProposalId(String token,String proposalId){
        //제안서 아이디로 채팅방 조회 (Optional 예외처리)
        Chat chat = findByProposalId(proposalId);
        //feign client : partner Info 조회
        PartnerInfoResponse partner = chatApiClient.getPartnerInfo(token, chat.getProposalId());
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
    public ChatDTO createRoom(String proposalId) {
        //DB에서 제안서 아이디를 기준으로 채팅방 조회
        Chat chat = findByProposalId(proposalId);
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

        return modelMapper.map(chat,ChatDTO.class);
    }



    //광고주의 채팅 목록 조회 (캠페인 아이디로 채팅방 조회)
    @Override
    public List<ChatDTO> campaignToChatList (String token, String campaignId){
        List<ChatInfoResponse> chatInfos = campaignToChatInfo(token, campaignId);
        List<Chat> chats = chatInfoGetChat(chatInfos);
        //마지막 메세지 조회 (데이트 타입 때문에 DTO 매핑)
        Map<String, ChatMessageDTO> lastMessages = messageService.lastMessage(chats);
        return chatDTOs(chats,chatInfos,lastMessages);
    }

    //로그인 유저의 채팅 목록 조회 (유저 아이디로 채팅방 조회)
    @Override
    public List<ChatDTO> userToChatList (String token){
        List<ChatInfoResponse> chatInfos = userChatinfo(token);
        List<Chat> chats = chatInfoGetChat(chatInfos);
        //마지막 메세지 조회 (데이트 타입 때문에 DTO 매핑)
        Map<String, ChatMessageDTO> lastMessages = messageService.lastMessage(chats);
        return chatDTOs(chats,chatInfos,lastMessages);
    }


    // feign client : 캠페인 아이디로 채팅 정보 조회
    @Override
    public List<ChatInfoResponse> campaignToChatInfo(String token, String campaignId) {
        return chatApiClient.getChatInfo(token, campaignId);
    }

    //채팅 정보리스트에서 proposalId 추출하여 채팅방 조회
    @Override
    public List<Chat> chatInfoGetChat(List<ChatInfoResponse> campaignToChatInfo){
        List<String> proposalIds = campaignToChatInfo.stream()
                .map(ChatInfoResponse::getProposalId)
                .collect(Collectors.toList());
        return chatRepository.findByProposalIdIn(proposalIds);
    }


    //chatDto List 빌더
    @Override
    public List<ChatDTO> chatDTOs (List<Chat> chats ,
                                   List<ChatInfoResponse> chatInfos,
                                   Map<String, ChatMessageDTO>  lastMessages){
        List<ChatDTO> advertiserChatList = new ArrayList<>();

        for(int i = 0; i< chats.size(); i++){
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

    @Override
    //제안서에 해당하는 채팅방 상태를 활성으로 변경
    public Chat activateRoomByProposal(String proposalId){
        Chat chat = findByProposalId(proposalId);
        chat.setChatStatus(ChatStatus.ACTIVE);
        return chatRepository.save(chat);
    }


    @Override
    //채팅아이디로 채팅방 조회
    public ChatDetailDTO getChatDtoByChatId(String token,String chatId){
        Chat chat = chatRepository.findById(chatId).orElseThrow(()->new ChatException(ErrorCode.CHATROOM_NOT_FOUND));
        return getChatDtoByProposalId(token,chat.getProposalId());
    }


    @Override
    //제안서에 해당하는 채팅방 소프트삭제
    public Chat softDeleteChat(String proposalId){
        Chat chat = findByProposalId(proposalId);
        chat.setChatStatus(ChatStatus.DELETE);
        return chatRepository.save(chat);
    }


    @Override
    //유저 아이디로 채팅정보 조회
    public List<ChatInfoResponse> userChatinfo(String token){
       return chatApiClient.getUserChatInfo(token);
    }



}
