package com.prgrms.be.intermark.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.be.intermark.auth.TokenProvider;
import com.prgrms.be.intermark.common.dto.page.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.common.service.page.PageService;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.dto.UserInfoResponseDTO;
import com.prgrms.be.intermark.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@WithMockUser
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private UserService userService;

    @MockBean
    private PageService pageService;

    @Nested
    @DisplayName("findUser")
    class FindUser {

        Long userId = 1L;
        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();

        @Test
        @DisplayName("Success - 유저 조회 성공 시 상태코드 200, 유저 세부정보 반환.")
        void findUserSuccess() throws Exception {
            // given
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
        @DisplayName("Fail - 유저 조회 실패 시 상태코드 404 반환.")
        void findUserFail() throws Exception {
            // given
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

    @Test
    @DisplayName("Success - 유효한 page, size 값인 경우 이에 맞는 유저 페이지 조회 - findUsers")
    public void findUsersValidSuccess() throws Exception {
        // given
        int page = 0, size = 3;
        PageRequest request = PageRequest.of(page, size);
        Page<User> users = new PageImpl<>(List.of(User.builder().social(SocialType.GOOGLE).socialId("1")
                        .role(UserRole.ROLE_USER)
                        .nickname("이수영").email("example1@gmail.com")
                        .build(),
                User.builder().social(SocialType.GOOGLE).socialId("2")
                        .role(UserRole.ROLE_USER)
                        .nickname("이서영").email("example2@gmail.com")
                        .build(),
                User.builder().social(SocialType.GOOGLE).socialId("3")
                        .role(UserRole.ROLE_USER)
                        .nickname("이소영").email("example3@gmail.com")
                        .build(),
                User.builder().social(SocialType.GOOGLE).socialId("4")
                        .role(UserRole.ROLE_USER)
                        .nickname("이세영").email("example4@gmail.com")
                        .build(),
                User.builder().social(SocialType.GOOGLE).socialId("5")
                        .role(UserRole.ROLE_USER)
                        .nickname("이주영").email("example5@gmail.com")
                        .build(),
                User.builder().social(SocialType.GOOGLE).socialId("6")
                        .role(UserRole.ROLE_USER)
                        .nickname("이자영").email("example6@gmail.com")
                        .build()), request, 6);
        PageResponseDTO<User, UserInfoResponseDTO> responseDTOPageResponseDTO = new PageResponseDTO<>(users, UserInfoResponseDTO::from, PageListIndexSize.ADMIN_PERFORMANCE_LIST_INDEX_SIZE);
        // when
        when(pageService.getPageRequest(any(PageRequest.class), anyInt()))
                .thenReturn(request);
        when(userService.findAllUser(any(PageRequest.class)))
                .thenReturn(responseDTOPageResponseDTO);
        mockMvc.perform(get("/api/v1/users")
                        .queryParam("page", Integer.toString(page))
                        .queryParam("size", Integer.toString(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andExpect(jsonPath("data[0].username").value(responseDTOPageResponseDTO.getData().get(0).username()))
                .andExpect(jsonPath("data[0].email").value(responseDTOPageResponseDTO.getData().get(0).email()))
                .andDo(print());
        // then
        verify(pageService).getPageRequest(any(PageRequest.class), anyInt());
        verify(userService).findAllUser(any(PageRequest.class));
    }
}