package com.prgrms.be.intermark.domain.user.service;

import com.prgrms.be.intermark.auth.TokenProvider;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.dto.UserInfoResponseDTO;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.prgrms.be.intermark.auth.constant.JwtConstants.THREE_DAYS_MSEC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final TokenProvider tokenProvider = mock(TokenProvider.class);
    private final UserService userService = new UserService(userRepository, tokenProvider);
    private final User mockUser = mock(User.class);

    @Nested
    @DisplayName("userId, role, refreshToken을 입력받아 새로운 role로 갱신된 refreshToken을 반환")
    class ChangeRefreshToken {
        @Test
        @DisplayName("Success - userId,role, refreshToken을 입력받아 새로운 RefreshToken 을 반환.")
        public void changeRefreshTokenSuccess() {
            Long userId = 1L;
            UserRole userRole = UserRole.ROLE_USER;
            String refreshToken = "";
            String newRefreshToken = "123";
            when(userRepository.findByIdAndRefreshToken(userId, refreshToken)).thenReturn(Optional.of(mockUser));
            when(tokenProvider.getExpiration(any())).thenReturn(THREE_DAYS_MSEC - 10L);
            when(tokenProvider.createRefreshToken(userId, userRole)).thenReturn(newRefreshToken);

            assertThat(userService.changeRefreshToken(userId, userRole, refreshToken)).isEqualTo(Optional.of(newRefreshToken));

            verify(tokenProvider).getExpiration(any());
            verify(userRepository).findByIdAndRefreshToken(userId, refreshToken);
            verify(mockUser).setRefreshToken(newRefreshToken);
            verify(mockUser).getRefreshToken();


        }

        @Test
        @DisplayName("Fail - userId혹은 refreshToken이 잘못됐을 경우 일 경우 IllegalArgumentException을 반환한다.")
        public void changeRefreshTokenCannotFindTargetUserFail() {
            Long userId = 1L;
            UserRole userRole = UserRole.ROLE_USER;
            String refreshToken = "";

            when(userRepository.findByIdAndRefreshToken(any(), any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.changeRefreshToken(userId, userRole, refreshToken)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Success - 만약 찾은 user의 refreshToken이 3일보다 긴 유효기간을 가진다면, Optional.empty()를 반환")
        public void changeRefreshTokenExpirationTooLongSuccess() {
            Long userId = 1L;
            UserRole userRole = UserRole.ROLE_USER;
            String refreshToken = "";

            when(userRepository.findByIdAndRefreshToken(userId, refreshToken)).thenReturn(Optional.of(mockUser));
            when(tokenProvider.getExpiration(any())).thenReturn(THREE_DAYS_MSEC + 10L);

            assertThat(userService.changeRefreshToken(userId, userRole, refreshToken)).isEqualTo(Optional.empty());
            verify(tokenProvider, never()).createRefreshToken(userId, userRole);


        }
    }

    @Nested
    @DisplayName("refreshToken을 받아서 해당하는 User에게 refreshToken을 셋팅 해준다.")
    class AssignRefreshToken {
        @Test
        @DisplayName("Success - refreshToken을 받아서 해당하는 user에게 셋팅해준다")
        public void assignRefreshTokenSucess() {
            String refreshToken = "";
            String userIdString = "1";
            when(tokenProvider.getUserIdFromRefreshToken(any())).thenReturn(userIdString);
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

            userService.assignRefreshToken(refreshToken);

            verify(mockUser).setRefreshToken("");
        }

        @Test
        @DisplayName("Fail - refreshToken을 받는데 refreshToken이 잘못됐다(일치하는 ID 없음).")
        public void assignRefreshTokenInvalidRefreshTokenFail() {
            String refreshToken = "";
            when(tokenProvider.getUserIdFromRefreshToken(any())).thenReturn("1");

            when(userRepository.findById(any())).thenReturn(Optional.empty());
            assertThatThrownBy(() -> userService.assignRefreshToken(refreshToken)).isInstanceOf(EntityNotFoundException.class);
        }
    }


    @Nested
    class delete {
        @Test
        @DisplayName("Success - uerID를 받아서 user를 삭제한다.")
        public void deleteSuccess() {
            Long userId = 1L;
            when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(mockUser));

            userService.delete(userId);

            verify(userRepository).findByIdAndIsDeletedFalse(userId);
            verify(mockUser).deleteUser();
        }

        @Test
        @DisplayName("Fail - userId로 user를 찾을 수 없습니다. EntityNotFoundException 을 던진다")
        public void deleteWrongUserIdFail() {
            Long userId = 1L;
            when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.delete(userId)).isInstanceOf(EntityNotFoundException.class);

            verify(userRepository, only()).findByIdAndIsDeletedFalse(userId);
            verify(mockUser, never()).deleteUser();

        }
    }

    @Nested
    class updateRole {
        @Test
        @DisplayName("Success - userId로 user를 찾아 권한을 업데이트 한다.")
        public void updateRoleSuccess() {

            Long userId = 1L;
            UserRole userRole = UserRole.ROLE_ADMIN;
            when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(mockUser));

            userService.updateRole(userId, userRole);

            verify(userRepository).findByIdAndIsDeletedFalse(userId);
            verify(mockUser).setRole(userRole);

        }

        @Test
        @DisplayName("Fail - userId 로 user를 찾지 못해 EntityNotFoundException 을 던진다.")
        public void updateRoleFail() {
            Long userId = 1L;
            UserRole userRole = UserRole.ROLE_ADMIN;
            when(userRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.delete(userId)).isInstanceOf(EntityNotFoundException.class);

            verify(userRepository, only()).findByIdAndIsDeletedFalse(userId);
            verify(mockUser, never()).setRole(any());

        }
    }

    @Nested
    @DisplayName("findByIdAndIsDeletedFalse")
    class FindByIdAndIsDeletedFalse {

        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();

        @Test
        @DisplayName("Success - 유저 조회 시 존재할 경우 상세정보 반환.")
        public void findByIdAndIsDeletedFalseSuccess() {
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
        @DisplayName("Fail - 유저 존재하지 않는 경우 EntityNotFoundException 발생.")
        public void findByWrongIdAndIsDeletedFalseFail() {
            // then
            assertThatThrownBy(() -> {
                userService.findById(anyLong());
            })
                    .isExactlyInstanceOf(EntityNotFoundException.class)
                    .hasMessage("존재하지 않는 사용자입니다.");
        }
    }

    @Test
    @DisplayName("Success - 모든 유저 페이지 조회 - findAllUser")
    public void findAllUserSuccess() {
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
        verify(userRepository).findByIsDeletedFalse(request);
        assertThat(response.getNowPage()).isEqualTo(1);
        assertThat(response.getNowPageNumbers()).isEqualTo(List.of(1, 2));
        assertThat(response.isNext()).isFalse();
        assertThat(response.isPrev()).isFalse();
    }

    @Test
    @DisplayName("Success - 삭제되지 않은모든유저의 갯수 조회.")
    public void countAllUserSuccess() {
        userService.countAllUser();
        verify(userRepository, times(1)).countByIsDeletedFalse();
    }

}