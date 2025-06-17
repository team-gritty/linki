package com.ssg.chatservice.domain.chat.service;

import com.ssg.chatservice.client.PartnerApiClient;
import com.ssg.chatservice.client.PartnerInfoResponse;
import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;
import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.domain.chat.repository.ChatRepository;
import com.ssg.chatservice.entity.Chat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ChatServiceImplTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRepository chatRepository;

    @MockBean // 외부 API 클라이언트는 실제 호출하지 않고 가짜 객체로 대체
    private PartnerApiClient partnerApiClient;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    @DisplayName("createRoom() - 채팅방 생성 성공 및 응답 DTO 검증")
    void createChatRoomTest() {
        // given - 테스트에 사용할 제안서 ID 정의
        String proposalId = "test1977";

        // given - PartnerApiClient가 반환할 가짜 파트너 정보 정의
        PartnerInfoResponse fakePartner = new PartnerInfoResponse(
                "partnerId",
                "partnerName",
                "test1977",
                "profile.png",
                "채널명",
                "PENDING");


        // given - 제안서 ID로 partner API 호출 시 위 데이터를 반환하도록 mock 세팅
        given(partnerApiClient.getPartnerInfo(proposalId)).willReturn(fakePartner);

        // when - 채팅방 생성 로직 실행
        ChatDetailDTO result = chatService.createRoom(proposalId);

        // then - 반환된 DTO가 null이 아니고, 제안서 ID가 일치하는지 검증
        assertThat(result).isNotNull();
        assertThat(result.getProposalId()).isEqualTo(proposalId);

        // then - 파트너 정보가 제대로 포함되었는지 확인
        assertThat(result.getPartnerId()).isEqualTo("partnerId");
        assertThat(result.getPartnerName()).isEqualTo("partnerName");

        // then - DB에 채팅방이 실제로 저장되었는지 확인
        Chat saved = chatRepository.findByProposalId(proposalId);
        assertThat(saved).isNotNull(); // DB에 저장 여부
        assertThat(saved.getProposalId()).isEqualTo(proposalId); // 제안서 ID 일치
    }
}