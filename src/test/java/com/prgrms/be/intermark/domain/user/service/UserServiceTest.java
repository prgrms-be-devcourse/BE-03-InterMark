package com.prgrms.be.intermark.domain.user.service;

import com.prgrms.be.intermark.auth.TokenProvider;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.dto.UserInfoResponseDTO;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private static final User user = User.builder()
            .social(SocialType.GOOGLE)
            .socialId("1")
            .role(UserRole.ROLE_USER)
            .nickname("이수영")
            .email("example1@gmail.com")
            .build();

    private final UserRepository userRepository = mock(UserRepository.class);
    private final TokenProvider tokenProvider = mock(TokenProvider.class);
    private final UserService userService = new UserService(userRepository, tokenProvider);

    @Test
    @DisplayName("조회할 유저가 존재하면 유저 이름과 이메일 정보만 DTO로 반환한다.")
    public void findByIdAndIsDeletedFalseTest() {
        // given, when
        when(userRepository.findByIdAndIsDeletedFalse(anyLong()))
                .thenReturn(Optional.of(user));
        UserInfoResponseDTO findUser = userService.findById(anyLong());
        // then
        verify(userRepository).findByIdAndIsDeletedFalse(anyLong());
        assertAll(
                () -> assertThat(findUser.username()).isEqualTo(user.getNickname()),
                () -> assertThat(findUser.email()).isEqualTo(user.getEmail())
        );
    }

    @Test
    @DisplayName("조회할 유저가 존재하지 않으면 Exception이 발생한다.")
    public void findByWrongIdAndIsDeletedFalseTest() {
        // given, when
        when(userRepository.findByIdAndIsDeletedFalse(anyLong()))
                .thenReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> {
            userService.findById(anyLong());
        })
                .isExactlyInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("회원탈퇴하지 않은 모든 유저정보를 DTO로 변환하여 조회할 수 있다.")
    public void findAllUser() {
        // given
        List<User> users = List.of(User.builder().social(SocialType.GOOGLE).socialId("1")
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
                        .build());
        PageRequest request = PageRequest.of(0, 5);
        Page<User> userPage = new PageImpl<>(users, request, users.size());
        // when
        when(userRepository.findByIsDeletedFalse(request))
                .thenReturn(userPage);
        PageResponseDTO<User, UserInfoResponseDTO> response = userService.findAllUser(request);
        // then
        assertThat(response.getNowPage()).isEqualTo(1);
        assertThat(response.getNowPageNumbers()).isEqualTo(List.of(1, 2));
        assertThat(response.isNext()).isFalse();
        assertThat(response.isPrev()).isFalse();
    }
}