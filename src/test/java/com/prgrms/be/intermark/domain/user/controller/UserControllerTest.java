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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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

    @Test
    @DisplayName("Success - ????????? page, size ?????? ?????? ?????? ?????? ?????? ????????? ?????? - findUsers")
    public void findUsersValidSuccess() throws Exception {
        // given
        int page = 0, size = 3;
        PageRequest request = PageRequest.of(page, size);
        Page<User> users = new PageImpl<>(List.of(User.builder().social(SocialType.GOOGLE).socialId("1")
                        .role(UserRole.ROLE_USER)
                        .nickname("?????????").email("example1@gmail.com")
                        .build(),
                User.builder().social(SocialType.GOOGLE).socialId("2")
                        .role(UserRole.ROLE_USER)
                        .nickname("?????????").email("example2@gmail.com")
                        .build(),
                User.builder().social(SocialType.GOOGLE).socialId("3")
                        .role(UserRole.ROLE_USER)
                        .nickname("?????????").email("example3@gmail.com")
                        .build(),
                User.builder().social(SocialType.GOOGLE).socialId("4")
                        .role(UserRole.ROLE_USER)
                        .nickname("?????????").email("example4@gmail.com")
                        .build(),
                User.builder().social(SocialType.GOOGLE).socialId("5")
                        .role(UserRole.ROLE_USER)
                        .nickname("?????????").email("example5@gmail.com")
                        .build(),
                User.builder().social(SocialType.GOOGLE).socialId("6")
                        .role(UserRole.ROLE_USER)
                        .nickname("?????????").email("example6@gmail.com")
                        .build()), request, 6);
        PageResponseDTO<User, UserInfoResponseDTO> responseDTOPageResponseDTO = new PageResponseDTO<>(users, UserInfoResponseDTO::from, PageListIndexSize.USER_LIST_INDEX_SIZE);
        // when
        when(pageService.getPageRequest(any(PageRequest.class), anyInt()))
                .thenReturn(request);
        when(userService.findAllUser(any(PageRequest.class)))
                .thenReturn(responseDTOPageResponseDTO);
        ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/users")
                .queryParam("page", Integer.toString(page))
                .queryParam("size", Integer.toString(size)));
        // then
        verify(pageService).getPageRequest(any(PageRequest.class), anyInt());
        verify(userService).findAllUser(any(PageRequest.class));
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andExpect(jsonPath("data[0].nickname").value(responseDTOPageResponseDTO.getData().get(0).nickname()))
                .andExpect(jsonPath("data[0].email").value(responseDTOPageResponseDTO.getData().get(0).email()))
                .andDo(print())
                .andDo(document("find-users-page-success",
                        requestParameters(
                                parameterWithName("page").description("?????? ????????? ????????? ??????"),
                                parameterWithName("size").description("??? ???????????? ????????? ????????? ???")
                        ),
                        responseFields(
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("?????? ???????????? ?????? ??????"),
                                fieldWithPath("data.[].nickname").type(JsonFieldType.STRING).description("??? ????????? ?????????"),
                                fieldWithPath("data.[].email").type(JsonFieldType.STRING).description("??? ????????? ?????????"),
                                fieldWithPath("nowPageNumbers").type(JsonFieldType.ARRAY).description("????????? ?????? ?????? ????????? ?????? ????????? ??????"),
                                fieldWithPath("nowPage").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("next").type(JsonFieldType.BOOLEAN).description("?????? ????????? ?????? ??????"),
                                fieldWithPath("prev").type(JsonFieldType.BOOLEAN).description("?????? ????????? ?????? ??????")

                        )));
    }

    @Nested
    @DisplayName("findUser")
    class FindUser {

        Long userId = 1L;
        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("?????????")
                .email("example1@gmail.com")
                .build();

        @Test
        @DisplayName("Success - ?????? ?????? ?????? ??? ???????????? 200, ?????? ???????????? ??????.")
        void findUserSuccess() throws Exception {
            // given
            UserInfoResponseDTO userInfoDTO = UserInfoResponseDTO.from(user);
            when(userService.findById(anyLong())).thenReturn(userInfoDTO);
            // when
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/users/{userId}", userId)
                    .content(objectMapper.writeValueAsString(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
            // then
            verify(userService).findById(anyLong());
            actions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("nickname").value(userInfoDTO.nickname()))
                    .andExpect(jsonPath("email").value(userInfoDTO.email()))
                    .andDo(print())
                    .andDo(document("find-user-success",
                            pathParameters(
                                    parameterWithName("userId").description("?????? id")
                            ),
                            responseFields(
                                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("?????? ?????????"),
                                    fieldWithPath("email").type(JsonFieldType.STRING).description("?????? ?????????")
                            )));
        }

        @Test
        @DisplayName("Fail - ?????? ?????? ?????? ??? ???????????? 404 ??????.")
        void findUserFail() throws Exception {
            // given
            when(userService.findById(anyLong())).thenThrow(new EntityNotFoundException("???????????? ?????? ??????????????????."));
            // when
            ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/users/{userId}", userId)
                    .content(objectMapper.writeValueAsString(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
            // then
            verify(userService).findById(anyLong());
            actions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("message").value("???????????? ?????? ??????????????????."))
                    .andDo(print())
                    .andDo(document("find-user-fail",
                            pathParameters(
                                    parameterWithName("userId").description("?????? id")
                            ),
                            responseFields(
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP ????????????"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                    fieldWithPath("errors").type(JsonFieldType.ARRAY).optional().description("????????? ?????? ?????????"),
                                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????? ?????? ??????")
                            )));
        }
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
        @DisplayName("Success -????????? ????????? ????????? ?????? ?????????. ???????????? 204 ??????")
        public void deleteUser_DeleteSelf_Success(Long userId, UserRole userRole) throws Exception {
            //given
            String aceessToken = tokenProvider.createAceessToken(userId, userRole);
            Authentication authenticationByToken = tokenProvider.getAuthentication(aceessToken);
            doNothing().when(userService).delete(anyLong());

            //when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/users/{userId}", userId)
                            .header("Authorization", "Bearer " + "{access ??????}").with(csrf()).with(authentication(authenticationByToken))
            ).andDo(print());
            //then
            resultActions.andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "deleteUserBySelfSucess",
                                    pathParameters(parameterWithName("userId").description("?????? ID")),
                                    requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access ??????}")
                                    )
                            )
                    );
            verify(userService, only()).delete(userId);

        }


        @ParameterizedTest
        @MethodSource("userIdRoleProviderExceptAdmin")
        @DisplayName("Fail - ???????????? ?????? ????????? ??????????????? delete ??????. ???????????? 401 ??????")
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
                                            parameterWithName("targetId").description("?????? ID")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access ??????}")
                                    ),
                                    responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("http?????? ??????"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????????"),
                                            fieldWithPath("errors").type(Arrays.asList(JsonFieldType.ARRAY,JsonFieldType.NULL)).description("errors??? ????????????."),
                                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????? ?????????????????? ????????????.")
                                    )
                            )
                    );
            verify(userService, never()).delete(targetUserId);

        }

        @ParameterizedTest
        @MethodSource("adminUserIdRoleProvider")
        @DisplayName("Success -  ???????????? ?????? ????????? ????????? ???????????????. ???????????? 204")
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
                                            parameterWithName("targetId").description("?????? ID")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access ??????}")
                                    )
                            ));
            verify(userService, never()).delete(userId);

        }
        @ParameterizedTest
        @MethodSource("adminUserIdRoleProvider")
        @DisplayName("Fail - ???????????? ??????????????? ???????????? ?????? ????????? ????????? ???????????????. ???????????? 404")
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
                                            parameterWithName("targetId").description("?????? Id")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access ??????}")
                                    ),responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("http ?????? ??????"),
                                            fieldWithPath("message").type(JsonFieldType.NULL).description("????????? ?????????"),
                                            fieldWithPath("errors").type(JsonFieldType.NULL).description("errors??? ????????????."),
                                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????? ?????????????????? ????????????.")
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
                    arguments(1L,UserRole.ROLE_USER,2L,"{\"?????????????????????\":123}"),
                    arguments(1L,UserRole.ROLE_USER,2L,"{\"authority\":\"USER\"}"),
                    arguments(1L,UserRole.ROLE_USER,2L,"{\"authority\":\"ROLE_FINDER\"}"),
                    arguments(1L,UserRole.ROLE_ADMIN,2L,"{\"?????????????????????\":123}"),
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
        @DisplayName("Success - ???????????? ?????? ????????? ????????? ?????????. 200")
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
                                            parameterWithName("targetUserId").description("?????? Id")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access ??????}")
                                    ),requestFields(
                                            fieldWithPath("authority").type(JsonFieldType.STRING).description("ROLE_USER | ROLE_SELLER | ROLE_ADMIN")
                                    )
                            )
                    );
            verify(userService,times(1)).updateRole(targetUserId,targetUserRole);
        }
        @ParameterizedTest
        @MethodSource("adminAndTargetProvider")
        @DisplayName("Fail - ???????????? ??????????????? ???????????? ?????? ????????? ????????? ?????????. 404")
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
                                            parameterWithName("targetUserId").description("?????? Id")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access ??????}")
                                    ),requestFields(
                                            fieldWithPath("authority").type(JsonFieldType.STRING).description("ROLE_USER | ROLE_SELLER | ROLE_ADMIN")
                                    ),responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("http ?????? ??????"),
                                            fieldWithPath("message").type(Arrays.asList(JsonFieldType.STRING,JsonFieldType.NULL)).description("????????? ?????????"),
                                            fieldWithPath("errors").type(Arrays.asList(JsonFieldType.ARRAY,JsonFieldType.NULL)).description("errors??? ????????????."),
                                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????? ?????????????????? ????????????.")
                                    )
                            )
                    );
            verify(userService,times(1)).updateRole(targetUserId,targetUserRole);
        }
        @ParameterizedTest
        @MethodSource("notAdminAndTargetProvider")
        @DisplayName("Fail - ???????????? ?????? ????????? ?????? ????????? ????????? ?????????.???????????? 401")
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
                                            parameterWithName("targetUserId").description("?????? Id")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access ??????}")
                                    ),requestFields(
                                            fieldWithPath("authority").type(JsonFieldType.STRING).description("ROLE_USER | ROLE_SELLER | ROLE_ADMIN")
                                    ),responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("http ?????? ??????"),
                                            fieldWithPath("message").type(Arrays.asList(JsonFieldType.STRING,JsonFieldType.NULL)).description("????????? ?????????"),
                                            fieldWithPath("errors").type(Arrays.asList(JsonFieldType.ARRAY,JsonFieldType.NULL)).description("errors??? ????????????."),
                                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????? ?????????????????? ????????????.")
                                    )
                            )
                    );;
            verify(userService,never()).updateRole(targetUserId,targetUserRole);
        }
        @ParameterizedTest
        @MethodSource("allUserAndWrongRequestBodyProvider")
        @DisplayName("Fail - ?????? ????????? ????????? ?????????. ????????? RequestBody??? ????????????. ???????????? 400")
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
                                            parameterWithName("targetId").description("?????? Id")
                                    ),requestHeaders(
                                            headerWithName("Authorization").description("Bearer + {access ??????}")
                                    ),responseFields(
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("http ?????? ??????"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("????????? ?????????"),
                                            subsectionWithPath("errors").type(Arrays.asList(JsonFieldType.ARRAY,JsonFieldType.NULL)).description("errors??? ????????????."),
                                            fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????? ?????????????????? ????????????.")
                                    )
                            )
                    );;
            verify(userService,never()).updateRole(anyLong(),any());
        }
    }
}