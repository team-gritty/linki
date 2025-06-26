package com.ssg.chatservice.domain.chat.service;

import com.ssg.chatservice.client.*;
import com.ssg.chatservice.domain.chat.dto.ChatDTO;
import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;
import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.domain.chat.enums.ErrorCode;
import com.ssg.chatservice.domain.chat.enums.NegoStatus;
import com.ssg.chatservice.domain.chat.repository.ChatRepository;
import com.ssg.chatservice.domain.kafka.enums.EventType;
import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.domain.message.service.MessageService;
import com.ssg.chatservice.entity.Chat;
import com.ssg.chatservice.exception.ChatException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        return chatRepository.findByProposalId(proposalId)
                .orElseThrow(()->new ChatException(ErrorCode.CHATROOM_NOT_FOUND));
    }

    //제안서 아이디로 ChatDetailDTO 반환
    //채팅방 입장
    @Override
    public ChatDetailDTO getChatDtoByProposalId(String token,String proposalId){
        //제안서 아이디로 채팅방 조회
        Chat chat = findByProposalId(proposalId);
        //feign client : partner Info 조회
        PartnerInfoResponse partner = chatApiClient.getPartnerInfo(token, chat.getProposalId());
        if(partner == null){
            throw new ChatException(ErrorCode.PARTNER_API_FAILED);
        }

        ChatDetailDTO chatDetailDTO = ChatDetailDTO.builder()
                .chatId(chat.getChatId())
                .chatStatus(chat.getChatStatus())
                .proposalId(chat.getProposalId())
                .partnerId(partner.getPartnerId())
                .partnerName(partner.getPartnerName())
                .negoStatus(NegoStatus.valueOf(partner.getNegoStatus()))
                .profileImage(partner.getProfileImage())
                .build();

        if (chatDetailDTO.getChatStatus() != ChatStatus.DELETE) return chatDetailDTO;
        else return chatDetailDTO;
    }

    //채팅방 생성
    @Override
    @Transactional
    public ChatDTO createRoom(String proposalId) {
        //DB에서 제안서 아이디를 기준으로 채팅방 조회
        Optional<Chat> existingChat = chatRepository.findByProposalId(proposalId);
        //이미 존재하는 채팅방이면 예외처리
        if(existingChat.isPresent()){
            throw new ChatException(ErrorCode.CHATROOM_ALREADY_EXIST);
        }
        // 비활성 상태의 채팅방 생성하여 DB에 저장
        Chat chat = Chat.builder()
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
        //삭제된 채팅방 필터링
        List<Chat> chats = chatInfoGetChat(chatInfos).stream().filter(chat ->
                chat.getChatStatus() != ChatStatus.DELETE).collect(Collectors.toList());
        //마지막 메세지 조회 (데이트 타입 때문에 DTO 매핑)
        Map<String, ChatMessageDTO> lastMessages = messageService.lastMessage(chats);
        return chatDTOs(chats,chatInfos,lastMessages);
    }

    //로그인 유저의 채팅 목록 조회 (유저 아이디로 채팅방 조회)
    @Override
    public List<ChatDTO> userToChatList (String token, String userId){
        List<ChatInfoResponse> chatInfos = userChatinfo(token);
        List<Chat> chats = chatInfoGetChat(chatInfos).stream()
                .filter(chat->chat.getChatStatus() != ChatStatus.DELETE).collect(Collectors.toList());
        //마지막 메세지 및 읽지 않은 메시지 여부 조회 (사용자별)
        Map<String, ChatMessageDTO> lastMessages = messageService.lastMessageWithUnreadStatus(chats, userId);
        
        List<ChatDTO> finalChatDtos = chatDTOs(chats,chatInfos,lastMessages);
        log.info("Final ChatDTOs to be sent to frontend: {}", finalChatDtos);
        return finalChatDtos;
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
        // chatInfos를 proposalId를 키로 하는 Map으로 변환하여 검색 효율을 높임
        Map<String, ChatInfoResponse> chatInfoMap = chatInfos.stream()
                .collect(Collectors.toMap(ChatInfoResponse::getProposalId, chatInfo -> chatInfo, (existing, replacement) -> existing));

        return chats.stream().map(chat -> {
            ChatInfoResponse chatInfo = chatInfoMap.get(chat.getProposalId());
            ChatMessageDTO lastMessage = lastMessages.get(chat.getChatId());

            if (chatInfo == null || lastMessage == null) {
                // 매칭되는 정보가 없는 경우, 로그를 남기고 리스트에서 제외하거나 기본값으로 처리
                // 여기서는 null을 반환하여 filter로 걸러냄
                return null;
            }

            return ChatDTO.builder()
                    .chatId(chat.getChatId())
                    .opponentId(chatInfo.getOpponentId())
                    .opponentName(chatInfo.getOpponentName())
                    .lastMessage(lastMessage.getContent())
                    .lastMessageTime(lastMessage.getMessageDate())
                    .isNew(!lastMessage.isMessageRead())  // 읽지 않은 메시지가 있으면 새 메시지
                    .proposalId(chat.getProposalId())
                    .campaignId(chatInfo.getCampaignId()) // Map에서 가져온 chatInfo 객체 사용
                    .build();
        }).filter(Objects::nonNull) // null이 아닌 객체만 필터링
          .collect(Collectors.toList());
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
    //제안서에 해당하는 채팅방 비활성
    public Chat inactiveChat(String proposalId){
        Chat chat = findByProposalId(proposalId);
        chat.setChatStatus(ChatStatus.INACTIVE);
        return chatRepository.save(chat);
    }


    @Override
    //유저 아이디로 채팅정보 조회
    public List<ChatInfoResponse> userChatinfo(String token){
       return chatApiClient.getUserChatInfo(token);
    }

    @Override
    //이벤트 수신 후 계약상태 변경
    public void updateNegoStatus(String proposalId, EventType eventType) {
        ChatDetailDTO chat = modelMapper.map(findByProposalId(proposalId), ChatDetailDTO.class);
        NegoStatus newStatus = eventType.getNegoStatus();
        if (newStatus == null) {
            throw new ChatException(ErrorCode.KAFKA_NEGO_STATUS_NULL);
        }
        chat.setNegoStatus(newStatus);
    }



}
