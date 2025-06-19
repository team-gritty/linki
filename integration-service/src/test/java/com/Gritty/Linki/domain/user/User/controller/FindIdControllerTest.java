package com.Gritty.Linki.domain.user.User.controller;

import com.Gritty.Linki.domain.user.User.dto.FindIdRequestDto;
import com.Gritty.Linki.domain.user.User.dto.FindIdResponseDto;
import com.Gritty.Linki.domain.user.User.dto.VerificationRequestDto;
import com.Gritty.Linki.domain.user.User.service.FindIdService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FindIdController.class)
class FindIdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FindIdService findIdService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_NAME = "테스트유저";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_USER_ID = "testuser123";
    private static final String TEST_VERIFICATION_CODE = "123456";

    @BeforeEach
    void setUp() {
        // 테스트 전 초기화 작업
    }

    @Test
    @DisplayName("인증번호 발송 - 성공")
    void sendVerificationCode_Success() throws Exception {
        // given
        FindIdRequestDto requestDto = new FindIdRequestDto(TEST_NAME, TEST_EMAIL);
        when(findIdService.sendVerificationCode(TEST_NAME, TEST_EMAIL)).thenReturn(true);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-id/send-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("인증번호가 이메일로 발송되었습니다."));
    }

    @Test
    @DisplayName("인증번호 발송 - 사용자 정보 없음")
    void sendVerificationCode_UserNotFound() throws Exception {
        // given
        FindIdRequestDto requestDto = new FindIdRequestDto(TEST_NAME, TEST_EMAIL);
        when(findIdService.sendVerificationCode(TEST_NAME, TEST_EMAIL)).thenReturn(false);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-id/send-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("일치하는 회원 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("인증번호 확인 - 성공")
    void verifyCode_Success() throws Exception {
        // given
        VerificationRequestDto requestDto = new VerificationRequestDto(TEST_NAME, TEST_EMAIL, TEST_VERIFICATION_CODE);
        FindIdResponseDto responseDto = FindIdResponseDto.builder()
                .userId(TEST_USER_ID)
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .build();
        
        when(findIdService.verifyCode(TEST_NAME, TEST_EMAIL, TEST_VERIFICATION_CODE))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-id/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.message").value("인증이 완료되었습니다."));
    }

    @Test
    @DisplayName("인증번호 확인 - 잘못된 인증번호")
    void verifyCode_WrongCode() throws Exception {
        // given
        VerificationRequestDto requestDto = new VerificationRequestDto(TEST_NAME, TEST_EMAIL, "999999");
        when(findIdService.verifyCode(TEST_NAME, TEST_EMAIL, "999999"))
                .thenReturn(null);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-id/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("인증번호가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("인증번호 재발송 - 성공")
    void resendVerificationCode_Success() throws Exception {
        // given
        FindIdRequestDto requestDto = new FindIdRequestDto(TEST_NAME, TEST_EMAIL);
        when(findIdService.resendVerificationCode(TEST_NAME, TEST_EMAIL)).thenReturn(true);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-id/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("인증번호가 재발송되었습니다."));
    }

    @Test
    @DisplayName("인증번호 재발송 - 사용자 정보 없음")
    void resendVerificationCode_UserNotFound() throws Exception {
        // given
        FindIdRequestDto requestDto = new FindIdRequestDto(TEST_NAME, TEST_EMAIL);
        when(findIdService.resendVerificationCode(TEST_NAME, TEST_EMAIL)).thenReturn(false);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-id/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("인증번호 재발송에 실패했습니다."));
    }

    @Test
    @DisplayName("잘못된 요청 형식")
    void invalidRequestFormat() throws Exception {
        // given
        String invalidJson = "{\"invalid\": \"format\"}";

        // when & then
        mockMvc.perform(post("/v1/api/user/find-id/send-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
} 