package com.prgrms.be.intermark.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.be.intermark.auth.TokenProvider;
import com.prgrms.be.intermark.common.dto.page.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.common.service.page.PageService;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.dto.UpdateUserAuthorityRequestDTO;
import com.prgrms.be.intermark.domain.user.dto.UserInfoResponseDTO;
import com.prgrms.be.intermark.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(UserController.class)
@WithMockUser
@AutoConfigureRestDocs
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
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
        PageResponseDTO<User, UserInfoResponseDTO> responseDTOPageResponseDTO = new PageResponseDTO<>(users, UserInfoResponseDTO::from, PageListIndexSize.USER_LIST_INDEX_SIZE);
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

    @Nested
    class DeleteUser {
        static Stream<Arguments> userIdRoleProvider() {
            return Stream.of(
                    arguments(1L, UserRole.ROLE_USER),
                    arguments(1L, UserRole.ROLE_SELLER),
                    arguments(1L, UserRole.ROLE_ADMIN)
            );
        }

        static Stream<Arguments> userIdRoleProviderExceptAdmin() {
            return Stream.of(
                    arguments(1L, UserRole.ROLE_USER),
                    arguments(1L, UserRole.ROLE_SELLER)
            );
        }

        static Stream<Arguments> adminUserIdRoleProvider() {
            return Stream.of(
                    arguments(1L, UserRole.ROLE_ADMIN)
            );
        }

        @ParameterizedTest
        @MethodSource("userIdRoleProvider")
        @DisplayName("Success -유저가 자신의 계정을 삭제 시킨다. 상태코드 204 반환")
        public void deleteUser_DeleteSelf_Success(Long userId, UserRole userRole) throws Exception {
            //given
            String aceessToken = tokenProvider.createAceessToken(userId, userRole);
            Authentication authenticationByToken = tokenProvider.getAuthentication(aceessToken);
            doNothing().when(userService).delete(anyLong());

            //when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/users/{userId}", userId)
                            .header("Authorization", "Bearer " + "{access 토큰}").with(csrf()).with(authentication(authenticationByToken))
            ).andDo(print());
            //then
            resultActions.andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "deleteUserBySelfSucess",
                                    pathParameters(parameterWithName("userId").description("유저 ID")),
                                    requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access 토큰}")
                                    )
                            )
                    );
            verify(userService, only()).delete(userId);

        }


        @ParameterizedTest
        @MethodSource("userIdRoleProviderExceptAdmin")
        @DisplayName("Fail - 관리자가 아닌 유저가 다른유저를 delete 한다. 상태코드 401 반환")
        public void deleteUser_DiffrentUserId_Fail(Long userId, UserRole role) throws Exception {
            //given
            Long targetUserId = userId + 1;
            String aceessToken = tokenProvider.createAceessToken(userId, role);
            Authentication authenticationByToken = tokenProvider.getAuthentication(aceessToken);
            doNothing().when(userService).delete(anyLong());

            //when
            ResultActions resultActions = mockMvc.perform(delete("/api/v1/users/{targetId}", targetUserId).
                    with(csrf()).with(authentication(authenticationByToken))
                    .header("Authorization", "Bearer " + aceessToken)).andDo(print());
            //then
            resultActions
                    .andExpect(status().isUnauthorized())
                    .andDo(
                            document("fail-delete-different-user",
                                    pathParameters(
                                            parameterWithName("targetId").description("유저 ID")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access 토큰}")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("http상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("에러의 메시지"),
                                            fieldWithPath("errors").type(Arrays.asList(JsonFieldType.ARRAY,JsonFieldType.NULL)).description("errors를 나타낸다."),
                                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("언제 발생했는지를 나타낸다.")
                                    )
                            )
                    );
            verify(userService, never()).delete(targetUserId);

        }

        @ParameterizedTest
        @MethodSource("adminUserIdRoleProvider")
        @DisplayName("Success -  관리자가 다른 유저의 계정을 삭제시킨다. 상태코드 204")
        public void deleteUser_AdminDeleteDiffrentUser_Success(Long userId, UserRole role) throws Exception {
            //given
            Long targetUserId = userId + 1;
            String aceessToken = tokenProvider.createAceessToken(userId, role);
            Authentication authenticationByToken = tokenProvider.getAuthentication(aceessToken);
            doNothing().when(userService).delete(anyLong());

            //when
            ResultActions resultActions = mockMvc.perform(delete("/api/v1/users/{targetId}", targetUserId)
                    .with(csrf()).with(authentication(authenticationByToken)).header("Authorization", "Bearer " + aceessToken)).andDo(print());
            //then
            resultActions.andExpect(status().isNoContent()).
                    andDo(
                            document("sucess-delete-by-admin",
                                    pathParameters(
                                            parameterWithName("targetId").description("유저 ID")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access 토큰}")
                                    )
                            ));
            verify(userService, never()).delete(userId);

        }
        @ParameterizedTest
        @MethodSource("adminUserIdRoleProvider")
        @DisplayName("Fail - 관리자가 삭제되거나 존재하지 않는 유저의 계정을 삭제시킨다. 상태코드 404")
        public void deleteUser_AdminDeleteAlreadyDeletedOrNotFoundUser_fail(Long userId,UserRole role) throws Exception{
            //given
            Long deletedUserId = userId + 1;
            Long cannotFindUserId = userId + 10;
            String aceessToken = tokenProvider.createAceessToken(userId, role);
            Authentication authenticationByToken = tokenProvider.getAuthentication(aceessToken);
            doThrow(new EntityNotFoundException()).when(userService).delete(deletedUserId);
            doThrow(new EntityNotFoundException()).when(userService).delete(cannotFindUserId);
            //when
            ResultActions deletedUserResultActions = mockMvc.perform(delete("/api/v1/users/{targetId}", deletedUserId)
                    .with(csrf()).with(authentication(authenticationByToken)).header("Authorization", "Bearer " + aceessToken)).andDo(print());
            ResultActions cannotFindUserResultActions = mockMvc.perform(delete("/api/v1/users/{targetId}", cannotFindUserId)
                    .with(csrf()).with(authentication(authenticationByToken)).header("Authorization", "Bearer " + aceessToken)).andDo(print());
            //then
            deletedUserResultActions.andExpect(status().isNotFound())
                    .andDo(
                            document("fail-admin-delete-not-found-user",
                                    pathParameters(
                                            parameterWithName("targetId").description("유저 Id")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access 토큰}")
                                    ),responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("http 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.NULL).description("에러의 메시지"),
                                            fieldWithPath("errors").type(JsonFieldType.NULL).description("errors를 나타낸다."),
                                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("언제 발생했는지를 나타낸다.")
                                    )
                            )
                    );
            cannotFindUserResultActions.andExpect(status().isNotFound());

            verify(userService, times(1)).delete(deletedUserId);
            verify(userService, times(1)).delete(cannotFindUserId);
        }


    }
    @Nested
    class UpdateUserAuthority{
        static Stream<Arguments> allUserAndWrongRequestBodyProvider(){
            return Stream.of(
                    arguments(1L,UserRole.ROLE_USER,2L,"{\"택도없는거시기\":123}"),
                    arguments(1L,UserRole.ROLE_USER,2L,"{\"authority\":\"USER\"}"),
                    arguments(1L,UserRole.ROLE_USER,2L,"{\"authority\":\"ROLE_FINDER\"}"),
                    arguments(1L,UserRole.ROLE_ADMIN,2L,"{\"택도없는거시기\":123}"),
                    arguments(1L,UserRole.ROLE_ADMIN,2L,"{\"authority\":\"ROLE_FINDER\"}"),
                    arguments(1L,UserRole.ROLE_ADMIN,2L,"{\"authority\":\"USER\"}")
            );
        }
        static Stream<Arguments> notAdminAndTargetProvider(){
            return Stream.of(
                    arguments(1L,UserRole.ROLE_USER,2L,UserRole.ROLE_USER),
                    arguments(1L,UserRole.ROLE_USER,2L,UserRole.ROLE_SELLER),
                    arguments(1L,UserRole.ROLE_USER,2L,UserRole.ROLE_ADMIN),
                    arguments(1L,UserRole.ROLE_SELLER,2L,UserRole.ROLE_USER),
                    arguments(1L,UserRole.ROLE_SELLER,2L,UserRole.ROLE_SELLER),
                    arguments(1L,UserRole.ROLE_SELLER,2L,UserRole.ROLE_ADMIN)
            );
        }


        static Stream<Arguments> adminAndTargetProvider(){
            return Stream.of(
                    arguments(1L,UserRole.ROLE_ADMIN,2L,UserRole.ROLE_USER),
                    arguments(1L,UserRole.ROLE_ADMIN,2L,UserRole.ROLE_SELLER),
                    arguments(1L,UserRole.ROLE_ADMIN,2L,UserRole.ROLE_ADMIN)
            );
        }
        @ParameterizedTest
        @MethodSource("adminAndTargetProvider")
        @DisplayName("Success - 관리자가 특정 유저의 권한을 바꾼다. 200")
        public void updateRole_adminUpdateRole_success(Long userId,UserRole userRole,Long targetUserId,UserRole targetUserRole) throws Exception{
            //given
            String accessToken = tokenProvider.createAceessToken(userId, userRole);
            UpdateUserAuthorityRequestDTO updateUserAuthorityRequestDTO = UpdateUserAuthorityRequestDTO.builder().authority(targetUserRole).build();
            Authentication authenticationByToken = tokenProvider.getAuthentication(accessToken);
            doNothing().when(userService).updateRole(targetUserId,targetUserRole);
            //when
            ResultActions resultActions = mockMvc
                    .perform(patch("/api/v1/users/{targetUserId}/authority", targetUserId)
                            .with(csrf())
                            .with(authentication(authenticationByToken))
                            .header("Authorization", "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateUserAuthorityRequestDTO))
                    ).andDo(print());
            //then
            resultActions.andExpect(status().isOk())
                    .andDo(
                            document(
                                    "success-change-authority-by-admin",
                                    pathParameters(
                                            parameterWithName("targetUserId").description("유저 Id")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access 토큰}")
                                    ),requestFields(
                                            fieldWithPath("authority").type(JsonFieldType.STRING).description("ROLE_USER | ROLE_SELLER | ROLE_ADMIN")
                                    )
                            )
                    );
            verify(userService,times(1)).updateRole(targetUserId,targetUserRole);
        }
        @ParameterizedTest
        @MethodSource("adminAndTargetProvider")
        @DisplayName("Fail - 관리자가 삭제되거나 존재하지 안는 유저의 권한을 바꾼다. 404")
        public void updateRole_adminUpdateRoleToDeletedUser_fail(Long userId,UserRole userRole,Long targetUserId,UserRole targetUserRole) throws Exception{
            //given
            String accessToken = tokenProvider.createAceessToken(userId, userRole);
            UpdateUserAuthorityRequestDTO updateUserAuthorityRequestDTO = UpdateUserAuthorityRequestDTO.builder().authority(targetUserRole).build();
            Authentication authenticationByToken = tokenProvider.getAuthentication(accessToken);
            doThrow(EntityNotFoundException.class).when(userService).updateRole(targetUserId,targetUserRole);
            //when
            ResultActions resultActions = mockMvc
                    .perform(patch("/api/v1/users/{targetUserId}/authority", targetUserId)
                            .with(csrf())
                            .with(authentication(authenticationByToken))
                            .header("Authorization", "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateUserAuthorityRequestDTO))
                    ).andDo(print());
            //then
            resultActions.andExpect(status().isNotFound())
                    .andDo(
                            document(
                                    "fail-change-authority-not-found-user-by-admin",
                                    pathParameters(
                                            parameterWithName("targetUserId").description("유저 Id")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access 토큰}")
                                    ),requestFields(
                                            fieldWithPath("authority").type(JsonFieldType.STRING).description("ROLE_USER | ROLE_SELLER | ROLE_ADMIN")
                                    ),responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("http 상태 코드"),
                                            fieldWithPath("message").type(Arrays.asList(JsonFieldType.STRING,JsonFieldType.NULL)).description("에러의 메시지"),
                                            fieldWithPath("errors").type(Arrays.asList(JsonFieldType.ARRAY,JsonFieldType.NULL)).description("errors를 나타낸다."),
                                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("언제 발생했는지를 나타낸다.")
                                    )
                            )
                    );
            verify(userService,times(1)).updateRole(targetUserId,targetUserRole);
        }
        @ParameterizedTest
        @MethodSource("notAdminAndTargetProvider")
        @DisplayName("Fail - 관리자가 아닌 사람이 특정 유저의 권한을 바꾼다.상태코드 401")
        public void updateRole_userUpdateRoleWhoIsNotAdmin_fail(
                Long userId,
                UserRole userRole,
                Long targetUserId,
                UserRole targetUserRole
        ) throws Exception{
            //given
            String accessToken = tokenProvider.createAceessToken(userId, userRole);
            UpdateUserAuthorityRequestDTO updateUserAuthorityRequestDTO = UpdateUserAuthorityRequestDTO.builder().authority(targetUserRole).build();
            Authentication authenticationByToken = tokenProvider.getAuthentication(accessToken);
            doNothing().when(userService).updateRole(targetUserId,targetUserRole);

            //when
            ResultActions resultActions = mockMvc
                    .perform(patch("/api/v1/users/{targetUserId}/authority", targetUserId)
                            .with(csrf())
                            .with(authentication(authenticationByToken))
                            .header("Authorization", "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateUserAuthorityRequestDTO))
                    ).andDo(print());
            //then
            resultActions.andExpect(status().isUnauthorized())
                    .andDo(
                            document(
                                    "fail-change-authority-by-unauthorized-user",
                                    pathParameters(
                                            parameterWithName("targetUserId").description("유저 Id")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access 토큰}")
                                    ),requestFields(
                                            fieldWithPath("authority").type(JsonFieldType.STRING).description("ROLE_USER | ROLE_SELLER | ROLE_ADMIN")
                                    ),responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("http 상태 코드"),
                                            fieldWithPath("message").type(Arrays.asList(JsonFieldType.STRING,JsonFieldType.NULL)).description("에러의 메시지"),
                                            fieldWithPath("errors").type(Arrays.asList(JsonFieldType.ARRAY,JsonFieldType.NULL)).description("errors를 나타낸다."),
                                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("언제 발생했는지를 나타낸다.")
                                    )
                            )
                    );;
            verify(userService,never()).updateRole(targetUserId,targetUserRole);
        }
        @ParameterizedTest
        @MethodSource("allUserAndWrongRequestBodyProvider")
        @DisplayName("Fail - 어떤 사람의 권한을 바꾼다. 요청의 RequestBody가 잘못됐다. 상태코드 400")
        public void updateRole_anyoneUpdateRoleWithWrongRequestBody_fail(
                Long userId,
                UserRole userRole,
                Long targetId,
                String requestBody) throws Exception{
            //given
            String accessToken = tokenProvider.createAceessToken(userId, userRole);
            Authentication authenticationByToken = tokenProvider.getAuthentication(accessToken);
            doNothing().when(userService).updateRole(anyLong(),any());
            //when
            ResultActions resultActions = mockMvc
                    .perform(patch("/api/v1/users/{targetId}/authority",targetId)
                            .with(csrf()).with(authentication(authenticationByToken))
                            .header("Authorization", "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody)
                    ).andDo(print());
            //then
            resultActions.andExpect(status().isBadRequest())
                    .andDo(
                            document(
                                    "fail-wrong-request-body",
                                    pathParameters(
                                            parameterWithName("targetId").description("유저 Id")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access 토큰}")
                                    ),responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("http 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("에러의 메시지"),
                                            subsectionWithPath("errors").type(Arrays.asList(JsonFieldType.ARRAY,JsonFieldType.NULL)).description("errors를 나타낸다."),
                                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("언제 발생했는지를 나타낸다.")
                                    )
                            )
                    );;
            verify(userService,never()).updateRole(anyLong(),any());
        }
    }
}