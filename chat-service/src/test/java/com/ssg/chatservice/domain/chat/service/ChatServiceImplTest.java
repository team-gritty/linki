package com.ssg.chatservice.domain.chat.service;

import com.ssg.chatservice.client.ChatInfoResponse;
import com.ssg.chatservice.domain.chat.dto.ChatDTO;
import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;
import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.domain.chat.repository.ChatRepository;
import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.domain.message.service.MessageService;
import com.ssg.chatservice.entity.Chat;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@Slf4j
@SpringBootTest
class ChatServiceImplTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageService messageService;

    @Test
    @DisplayName("제안서 아이디로 채팅방 조회")
    void findByProposalId(){
        String proposalId = "PRP-0000000000000000";
        Chat chat = chatService.findByProposalId(proposalId);
        assertThat(chat).isNotNull();
        assertThat(chat.getProposalId()).isEqualTo(proposalId);
    }


    @Test
    @DisplayName("제안서 아이디로 ChatDetailDTO 조회")
    void getChatDtoByProposalId() {
        // 테스트에 사용할 제안서 ID 정의
        String proposalId = "PRP-0000000000000000";

        // 토큰 전달
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJVU1ItMDAwMDAwMDAwMDAwMDAwMCIsInVzZXJSb2xlIjoiUk9MRV9JTkZMVUVOQ0VSIiwiaWF0IjoxNzUwMzg3NjQ2LCJleHAiOjE3NTA0MjM2NDZ9.zI8qCp_QrrKF7cYIBAdMuHPi1ht5uyS_pqWgEwdqf88";

        //파트너 객체 호출
        ChatDetailDTO chatDetailDTO = chatService.getChatDtoByProposalId(token, proposalId);
        //반환된 DTO가 null이 아니고, 제안서 ID가 일치하는지 검증
        assertThat(chatDetailDTO).isNotNull();
        assertThat(chatDetailDTO.getProposalId()).isEqualTo(proposalId);
    }

    //채팅방이 없는 제안서 아이디로 test 시 성공
    @Test
    @DisplayName("채팅방 생성")
    void createRoom(){
        ChatDTO chat = chatService.createRoom("PRP-000000000000000");

        assertThat(chat).isNotNull();
        assertThat(chat.getProposalId()).isEqualTo("PRP-000000000000000");
    }

    @Test
    @DisplayName("광고주의 채팅 목록 조회 (캠페인 아이디로 채팅방 조회)")
    void campaignToChatList(){
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJVU1ItMDAwMDAwMDAwMDAwMDAwMiIsInVzZXJSb2xlIjoiUk9MRV9BRFZFUlRJU0VSIiwiaWF0IjoxNzUwNDA3MTgzLCJleHAiOjE3NTA0NDMxODN9.jjojs_8C3b36deWJgFFijvtCAru-1JpodxuWxvqi204";
        String campaignId = "CMP-0000000000000000";

        List<ChatDTO> chatDTOs = chatService.campaignToChatList(token,campaignId);

        chatDTOs.forEach(chatDTO -> log.info("chatDTO={}",chatDTO.toString()));


    }

    @Test
    @DisplayName("로그인 유저의 채팅 목록 조회 (유저 아이디로 채팅방 조회)")
    void userToChatList(){
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJVU1ItMDAwMDAwMDAwMDAwMDAwMiIsInVzZXJSb2xlIjoiUk9MRV9BRFZFUlRJU0VSIiwiaWF0IjoxNzUwNDA3MTgzLCJleHAiOjE3NTA0NDMxODN9.jjojs_8C3b36deWJgFFijvtCAru-1JpodxuWxvqi204";
        List<ChatDTO> chatDTOs = chatService.userToChatList(token);
        chatDTOs.forEach(chatDTO -> log.info("chatDTO={}",chatDTO.toString()));
    }


    @Test
    @DisplayName("캠페인 아이디로 ChatInfoResponse 조회")
    void chatInfoResponses(){
        // 테스트에 사용할 캠페인 ID 정의
        String campaignId = "CMP-0000000000000000";

        // 토큰 전달
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJVU1ItMDAwMDAwMDAwMDAwMDAwMCIsInVzZXJSb2xlIjoiUk9MRV9JTkZMVUVOQ0VSIiwiaWF0IjoxNzUwMzg3NjQ2LCJleHAiOjE3NTA0MjM2NDZ9.zI8qCp_QrrKF7cYIBAdMuHPi1ht5uyS_pqWgEwdqf88";

        //파트너 객체 호출
        List<ChatInfoResponse> chatInfoResponses = chatService.campaignToChatInfo(token, campaignId);
        //반환된 DTO가 null이 아니고, 제안서 ID가 일치하는지 검증
        assertThat(chatInfoResponses).isNotNull();
        log.info(chatInfoResponses.toString());

    }

    @Test
    @DisplayName("채팅 정보리스트에서 proposalId 추출하여 채팅방 조회")
    void chatInfoGetChat(){
        List<ChatInfoResponse> chatInfoResponses = List.of(
                new ChatInfoResponse("test1", "test1", "PRP-0000000000000000"),
                new ChatInfoResponse("test2", "test2", "PRP-0000000000000001")
        );

        List<Chat> chats = chatService.chatInfoGetChat(chatInfoResponses);
        List<String> proposalIds = chats.stream().map(Chat::getProposalId).collect(Collectors.toList());
        proposalIds.forEach(poposalId -> log.info("proposalId={}",poposalId));
    }

    @Test
    @DisplayName("chatDto List 빌더")
    void chatDTOs(){
        List<Chat> chats = List.of(
                new Chat("CHT-0000000000000000", Instant.now(), ChatStatus.PENDING,"PRP-000000000000000"),
                new Chat("CHT-0000000000000001", Instant.now(), ChatStatus.PENDING,"PRP-0000000000000001")
        );
        List<ChatInfoResponse> chatInfoResponses = List.of(
                new ChatInfoResponse("test1", "test1", "PRP-0000000000000000"),
                new ChatInfoResponse("test2", "test2", "PRP-0000000000000001")
        );

        Map<String, ChatMessageDTO> lastMessages = Map.of(
                "CHT-0000000000000000",new ChatMessageDTO("MSG-test1","CHT-0000000000000000","user-test","CHT-0000000000000000의 마지막메시지","TEXT",LocalDateTime.now(),false),
                "CHT-0000000000000001",new ChatMessageDTO("MSG-test2","CHT-0000000000000001","user-test","CHT-0000000000000001의 마지막메시지","TEXT",LocalDateTime.now(),false)
        );

        List<ChatDTO> chatDTOs = chatService.chatDTOs(chats,chatInfoResponses,lastMessages);
        chatDTOs.forEach(chatDTO -> log.info("chatDTO={}",chatDTO.toString()));


    }

    @Test
    @DisplayName("제안서에 해당하는 채팅방 상태를 활성으로 변경")
    void activateRoomByProposal(){
        Chat beforeChat = chatService.findByProposalId("PRP-0000000000000000");
        log.info(String.valueOf(beforeChat.getChatStatus()));

        chatService.activateRoomByProposal("PRP-0000000000000000");
        Chat AfterChat = chatService.findByProposalId("PRP-0000000000000000");
        log.info(String.valueOf(AfterChat.getChatStatus()));
        assertThat(AfterChat.getChatStatus()).isEqualTo(ChatStatus.ACTIVE);
    }

    @Test
    @DisplayName("채팅아이디로 채팅방 조회")
    void getChatDtoByChatId(){
        // 토큰 전달
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJVU1ItMDAwMDAwMDAwMDAwMDAwMCIsInVzZXJSb2xlIjoiUk9MRV9JTkZMVUVOQ0VSIiwiaWF0IjoxNzUwMzg3NjQ2LCJleHAiOjE3NTA0MjM2NDZ9.zI8qCp_QrrKF7cYIBAdMuHPi1ht5uyS_pqWgEwdqf88";
        String chatId = "CHT-0000000000000000";
        ChatDetailDTO chat = chatService.getChatDtoByChatId(token,chatId);

        log.info("ChatDetailDTO = {}",chat.toString());

        assertThat(chat).isNotNull();
        assertThat(chat.getChatId()).isEqualTo(chatId);
    }

    @Test
    @DisplayName("제안서에 해당하는 채팅방 소프트삭제")
    void softDeleteChat(){
        Chat beforeChat = chatService.findByProposalId("PRP-0000000000000000");
        log.info(String.valueOf(beforeChat.getChatStatus()));

        chatService.activateRoomByProposal("PRP-0000000000000000");
        Chat AfterChat = chatService.softDeleteChat("PRP-0000000000000000");
        log.info(String.valueOf(AfterChat.getChatStatus()));
        assertThat(AfterChat.getChatStatus()).isEqualTo(ChatStatus.DELETE);
    }

}