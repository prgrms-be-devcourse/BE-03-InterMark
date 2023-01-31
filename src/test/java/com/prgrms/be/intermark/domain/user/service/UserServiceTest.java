package com.prgrms.be.intermark.domain.user.service;

import com.prgrms.be.intermark.auth.CustomUserPrincipal;
import com.prgrms.be.intermark.auth.TokenProvider;
import com.prgrms.be.intermark.common.dto.page.PageResponseDTO;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.dto.UserIdAndRoleDTO;
import com.prgrms.be.intermark.domain.user.dto.UserInfoResponseDTO;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final TokenProvider tokenProvider = mock(TokenProvider.class);
    private final UserService userService = new UserService(userRepository, tokenProvider);

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
                    () -> assertThat(findUser.nickname()).isEqualTo(user.getNickname()),
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

    @Nested
    @DisplayName("findByIdForFasade")
    class FindByIdForFasade {

        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();

        @Test
        @DisplayName("Success - 유저 조회 시 존재할 경우 상세정보 반환.")
        public void findByIdForFasadeSuccess() {
            // given, when
            when(userRepository.findByIdAndIsDeletedFalse(anyLong()))
                    .thenReturn(Optional.of(user));
            User findUser = userService.findByIdForFacade(anyLong());
            // then
            verify(userRepository).findByIdAndIsDeletedFalse(anyLong());
            MatcherAssert.assertThat(findUser, Matchers.samePropertyValuesAs(user));
        }

        @Test
        @DisplayName("Fail - 유저 존재하지 않는 경우 EntityNotFoundException 발생.")
        public void findByIdForFasadeFail() {
            // then
            assertThatThrownBy(() -> {
                userService.findByIdForFacade(anyLong());
            })
                    .isExactlyInstanceOf(EntityNotFoundException.class)
                    .hasMessage("존재하지 않는 사용자입니다.");
        }
    }

    @Nested
    @DisplayName("findByProviderAndProviderId")
    class FindByProviderAndProviderId {

        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();

        @Test
        @DisplayName("Success - 유저 조회 시 존재할 경우 상세정보 반환.")
        public void findByProviderAndProviderIdSuccess() {
            // given, when
            when(userRepository.findBySocialTypeAndSocialId(user.getSocialType(), user.getSocialId()))
                    .thenReturn(Optional.of(user));
            Optional<User> findUser = userService.findByProviderAndProviderId(user.getSocialType(), user.getSocialId());
            // then
            verify(userRepository).findBySocialTypeAndSocialId(user.getSocialType(), user.getSocialId());
            assertThat(findUser).isPresent();
            MatcherAssert.assertThat(findUser.get(), Matchers.samePropertyValuesAs(user));
        }

        @Test
        @DisplayName("Fail - 유저 존재하지 않는 경우 Optional.empty 반환")
        public void findByProviderAndProviderIdFail() {
            // given, when
            when(userRepository.findBySocialTypeAndSocialId(user.getSocialType(), user.getSocialId()))
                    .thenReturn(Optional.empty());
            Optional<User> findUser = userService.findByProviderAndProviderId(user.getSocialType(), user.getSocialId());
            // then
            verify(userRepository).findBySocialTypeAndSocialId(user.getSocialType(), user.getSocialId());
            assertThat(findUser).isEmpty();
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

    @Nested
    @DisplayName("join")
    class Join {
        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();

        OAuth2User oAuth2User = new CustomUserPrincipal("sub",
                UserRole.ROLE_USER,
                Map.of("sub", user.getSocialId(),
                        "username", user.getNickname(),
                        "email", user.getEmail()));

        @Test
        @DisplayName("Success - 유저가 존재하는 경우 로그인 진행 후 유저 정보 반환")
        public void joinLoginSuccess() {
            // given, when
            when(userRepository.findBySocialTypeAndSocialId(user.getSocialType(), user.getSocialId()))
                    .thenReturn(Optional.of(user));
            UserIdAndRoleDTO response = userService.join(oAuth2User, SocialType.GOOGLE.name().toLowerCase());
            // then
            verify(userRepository).findBySocialTypeAndSocialId(user.getSocialType(), user.getSocialId());
            assertThat(response.userId()).isEqualTo(user.getId());
            assertThat(response.userRole()).isEqualTo(user.getRole());
        }

        @Test
        @DisplayName("Success - 유저가 존재하지 않는 경우 회원가입 진행 후 유저 정보 반환")
        public void joinSignUpSuccess() {
            // given, when
            when(userRepository.findBySocialTypeAndSocialId(user.getSocialType(), user.getSocialId()))
                    .thenReturn(Optional.empty());
            when(userRepository.save(any(User.class)))
                    .thenReturn(user);
            UserIdAndRoleDTO response = userService.join(oAuth2User, SocialType.GOOGLE.name().toLowerCase());
            // then
            verify(userRepository).findBySocialTypeAndSocialId(user.getSocialType(), user.getSocialId());
            verify(userRepository).save(any(User.class));
            assertThat(response.userId()).isEqualTo(user.getId());
            assertThat(response.userRole()).isEqualTo(user.getRole());
        }

        @Test
        @DisplayName("Fail - 존재하지 않는 Social Type인 경우 IllegalArgumentException 반환.")
        public void joinFail() {
            // given, when, then
            assertThatThrownBy(() -> {
                userService.join(oAuth2User, "wrong");
            })
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }
}