package com.prgrms.be.intermark.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.be.intermark.auth.TokenProvider;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.dto.UserInfoResponseDTO;
import com.prgrms.be.intermark.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@WithMockUser
class UserControllerTest {

    private static final User user = User.builder()
            .social(SocialType.GOOGLE)
            .socialId("1")
            .role(UserRole.ROLE_USER)
            .nickname("이수영")
            .email("example1@gmail.com")
            .build();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Success - 유저 조회 성공 시 상태코드 200, 유저 세부정보 반환. - findUser")
    void findUserSuccess() throws Exception {
        // given
        Long userId = 1L;
        UserInfoResponseDTO userInfoDTO = UserInfoResponseDTO.from(user);
        when(userService.findById(anyLong())).thenReturn(userInfoDTO);
        // when
        mockMvc.perform(get("/api/v1/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(userInfoDTO.username()))
                .andExpect(jsonPath("email").value(userInfoDTO.email()))
                .andDo(print());
        // then
        verify(userService).findById(anyLong());
    }

    @Test
    @DisplayName("Fail - 유저 조회 실패 시 상태코드 404 반환. - findUser")
    void findUserFail() throws Exception {
        // given
        Long userId = 1L;
        when(userService.findById(anyLong())).thenThrow(new EntityNotFoundException("존재하지 않는 사용자입니다."));
        // when
        mockMvc.perform(get("/api/v1/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").value("존재하지 않는 사용자입니다."))
                .andDo(print());
        // then
        verify(userService).findById(anyLong());
    }
}