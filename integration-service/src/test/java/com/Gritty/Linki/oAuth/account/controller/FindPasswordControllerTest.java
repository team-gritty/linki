package com.Gritty.Linki.oAuth.account.controller;

import com.Gritty.Linki.domain.oAuth.account.service.FindPasswordService;
import com.Gritty.Linki.domain.oAuth.dto.FindPasswordRequestDto;
import com.Gritty.Linki.domain.oAuth.dto.FindPasswordVerificationRequestDto;
import com.Gritty.Linki.domain.oAuth.dto.FindPasswordChangeRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FindPasswordController.class)
class FindPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FindPasswordService findPasswordService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String userName = "테스트유저";
    private final String userLoginId = "testuser";
    private final String userEmail = "test@example.com";
    private final String verificationCode = "123456";
    private final String newPassword = "newPassword123";

    @BeforeEach
    void setUp() {
        // 기본 설정
    }

    @Test
    @DisplayName("인증번호 발송 성공 테스트")
    void sendVerificationCode_Success() throws Exception {
        // given
        FindPasswordRequestDto requestDto = new FindPasswordRequestDto(userName, userLoginId, userEmail);
        when(findPasswordService.sendVerificationCode(userName, userLoginId, userEmail)).thenReturn(true);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-password/send-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("인증번호가 이메일로 발송되었습니다."));
    }

    @Test
    @DisplayName("인증번호 발송 실패 테스트")
    void sendVerificationCode_Failure() throws Exception {
        // given
        FindPasswordRequestDto requestDto = new FindPasswordRequestDto(userName, userLoginId, userEmail);
        when(findPasswordService.sendVerificationCode(userName, userLoginId, userEmail)).thenReturn(false);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-password/send-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("일치하는 회원 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("인증번호 확인 성공 테스트")
    void verifyCode_Success() throws Exception {
        // given
        FindPasswordVerificationRequestDto requestDto = new FindPasswordVerificationRequestDto(userName, userLoginId, userEmail, verificationCode);
        when(findPasswordService.verifyCode(userName, userLoginId, userEmail, verificationCode)).thenReturn(true);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-password/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("인증이 완료되었습니다."));
    }

    @Test
    @DisplayName("인증번호 확인 실패 테스트")
    void verifyCode_Failure() throws Exception {
        // given
        FindPasswordVerificationRequestDto requestDto = new FindPasswordVerificationRequestDto(userName, userLoginId, userEmail, verificationCode);
        when(findPasswordService.verifyCode(userName, userLoginId, userEmail, verificationCode)).thenReturn(false);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-password/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("인증번호가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("인증번호 재발송 성공 테스트")
    void resendVerificationCode_Success() throws Exception {
        // given
        FindPasswordRequestDto requestDto = new FindPasswordRequestDto(userName, userLoginId, userEmail);
        when(findPasswordService.resendVerificationCode(userName, userLoginId, userEmail)).thenReturn(true);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-password/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("인증번호가 재발송되었습니다."));
    }

    @Test
    @DisplayName("인증번호 재발송 실패 테스트")
    void resendVerificationCode_Failure() throws Exception {
        // given
        FindPasswordRequestDto requestDto = new FindPasswordRequestDto(userName, userLoginId, userEmail);
        when(findPasswordService.resendVerificationCode(userName, userLoginId, userEmail)).thenReturn(false);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-password/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("인증번호 재발송에 실패했습니다."));
    }

    @Test
    @DisplayName("비밀번호 변경 성공 테스트")
    void changePassword_Success() throws Exception {
        // given
        FindPasswordChangeRequestDto requestDto = new FindPasswordChangeRequestDto(userName, userLoginId, userEmail, verificationCode, newPassword);
        when(findPasswordService.changePassword(userName, userLoginId, userEmail, verificationCode, newPassword)).thenReturn(true);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-password/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("비밀번호가 성공적으로 변경되었습니다."));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 테스트")
    void changePassword_Failure() throws Exception {
        // given
        FindPasswordChangeRequestDto requestDto = new FindPasswordChangeRequestDto(userName, userLoginId, userEmail, verificationCode, newPassword);
        when(findPasswordService.changePassword(userName, userLoginId, userEmail, verificationCode, newPassword)).thenReturn(false);

        // when & then
        mockMvc.perform(post("/v1/api/user/find-password/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("비밀번호 변경에 실패했습니다."));
    }

    @Test
    @DisplayName("사용자 정보 확인 성공 테스트")
    void checkUser_Success() throws Exception {
        // given
        when(findPasswordService.checkUser(userName, userLoginId, userEmail)).thenReturn(true);

        // when & then
        mockMvc.perform(get("/v1/api/user/find-password/check-user")
                        .param("userName", userName)
                        .param("userLoginId", userLoginId)
                        .param("userEmail", userEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용자를 찾았습니다."));
    }

    @Test
    @DisplayName("사용자 정보 확인 실패 테스트")
    void checkUser_Failure() throws Exception {
        // given
        when(findPasswordService.checkUser(userName, userLoginId, userEmail)).thenReturn(false);

        // when & then
        mockMvc.perform(get("/v1/api/user/find-password/check-user")
                        .param("userName", userName)
                        .param("userLoginId", userLoginId)
                        .param("userEmail", userEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("일치하는 사용자 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("잘못된 요청 형식 테스트")
    void invalidRequest_Test() throws Exception {
        // given
        String invalidJson = "{\"invalid\": \"data\"}";

        // when & then
        mockMvc.perform(post("/v1/api/user/find-password/send-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
} 