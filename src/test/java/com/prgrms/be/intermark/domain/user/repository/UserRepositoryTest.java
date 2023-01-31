package com.prgrms.be.intermark.domain.user.repository;

import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("findByIdAndIsDeletedFalse")
    class FindByIdAndIsDeltedFalse {

        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();

        @Test
        @DisplayName("Success - 유저 조회 성공 시 세부정보 반환.")
        public void findByIdAndIsDeletedFalseSuccess() {
            // given
            userRepository.save(user);
            // when
            Optional<User> findUser = userRepository.findByIdAndIsDeletedFalse(user.getId());
            // then
            assertThat(findUser).isPresent();
            assertThat(findUser.get().getId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("Fail - 유저가 삭제 상태인 경우 조회 불가능.")
        public void findByIdAndIsDeletedFail() {
            // given
            user.deleteUser();
            // when
            userRepository.save(user);
            Optional<User> findUser = userRepository.findByIdAndIsDeletedFalse(user.getId());
            // then
            assertThat(findUser).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIdAndRefreshToken")
    class FindByIdAndRefreshToken {

        String refreshToken = "refreshToken";
        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();

        @Test
        @DisplayName("Success - 유저 조회 성공 시 세부정보 반환.")
        public void findByIdAndIsDeletedFalseSuccess() {
            // given
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
            // when
            Optional<User> findUser = userRepository.findByIdAndRefreshToken(user.getId(), refreshToken);
            // then
            assertThat(findUser).isPresent();
            MatcherAssert.assertThat(findUser.get(), Matchers.samePropertyValuesAs(user));
        }

        @Test
        @DisplayName("Success - 유저가 삭제 상태인 경우에도 조회 가능")
        public void findByIdAndIsDeletedTrueSuccess() {
            // given
            user.deleteUser();
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
            // when
            Optional<User> findUser = userRepository.findByIdAndRefreshToken(user.getId(), refreshToken);
            // then
            assertThat(findUser).isPresent();
            MatcherAssert.assertThat(findUser.get(), Matchers.samePropertyValuesAs(user));
        }

        @Test
        @DisplayName("Fail - userId나 refresh toekn이 다른 경우 Optional.empty 반환.")
        public void findByIdAndIsDeletedFail() {
            // given
            user.deleteUser();
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
            // when
            Optional<User> findUser1 = userRepository.findByIdAndRefreshToken(user.getId(), "wrong");
            Optional<User> findUser2 = userRepository.findByIdAndRefreshToken(2L, refreshToken);
            // then
            assertThat(findUser1).isEmpty();
            assertThat(findUser2).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySocialTypeAndSocialId")
    class FindBySocialTypeAndSocialId {

        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();

        @Test
        @DisplayName("Success - 유저 조회 성공 시 세부정보 반환.")
        public void findByIdAndIsDeletedFalseSuccess() {
            // given
            userRepository.save(user);
            // when
            Optional<User> findUser = userRepository.findBySocialTypeAndSocialId(user.getSocialType(), user.getSocialId());
            // then
            assertThat(findUser).isPresent();
            MatcherAssert.assertThat(findUser.get(), Matchers.samePropertyValuesAs(user));
        }

        @Test
        @DisplayName("Success - 유저가 삭제 상태인 경우에도 조회 가능")
        public void findByIdAndIsDeletedTrueSuccess() {
            // given
            user.deleteUser();
            userRepository.save(user);
            // when
            Optional<User> findUser = userRepository.findBySocialTypeAndSocialId(user.getSocialType(), user.getSocialId());
            // then
            assertThat(findUser).isPresent();
            MatcherAssert.assertThat(findUser.get(), Matchers.samePropertyValuesAs(user));
        }

        @Test
        @DisplayName("Fail - Social ID가 다른 경우 Optional.empty 반환.")
        public void findByIdAndIsDeletedFail() {
            // given
            userRepository.save(user);
            // when
            Optional<User> findUser = userRepository.findBySocialTypeAndSocialId(SocialType.GOOGLE, "wrong");
            // then
            assertThat(findUser).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByIsDeletedFalse")
    class CountByIsDeletedFalse {

        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();

        @Test
        @DisplayName("Success - 삭제되지 않은 유저 존재 시 유저 수 반환")
        public void countByIsDeletedFalseSuccess() {
            // given
            userRepository.save(user);
            // when
            long userCount = userRepository.countByIsDeletedFalse();
            // then
            assertThat(userCount).isEqualTo(1);
        }

        @Test
        @DisplayName("Success - 삭제된 유저만 존재 시 0 반환.")
        public void countByIsDeletedFalseDeletedUserSuccess() {
            // given
            user.deleteUser();
            userRepository.save(user);
            // when
            long userCount = userRepository.countByIsDeletedFalse();
            // then
            assertThat(userCount).isEqualTo(0);
        }
    }

    @Test
    @DisplayName("Success - 모든 유저 조회 시 유저 페이징 반환 - findByIsDeletedFalse")
    public void findByIsDeletedFalseSuccess() {
        // given
        userRepository.save(new User(SocialType.GOOGLE, "1", "user1", UserRole.ROLE_USER, "example1@gmail.com"));
        userRepository.save(new User(SocialType.GOOGLE, "2", "user2", UserRole.ROLE_USER, "example2@gmail.com"));
        userRepository.save(new User(SocialType.GOOGLE, "3", "user3", UserRole.ROLE_USER, "example3@gmail.com"));
        userRepository.save(new User(SocialType.GOOGLE, "4", "user4", UserRole.ROLE_USER, "example4@gmail.com"));
        userRepository.save(new User(SocialType.GOOGLE, "5", "user5", UserRole.ROLE_USER, "example5@gmail.com"));
        userRepository.save(new User(SocialType.GOOGLE, "6", "user6", UserRole.ROLE_USER, "example6@gmail.com"));
        userRepository.save(new User(SocialType.GOOGLE, "7", "user7", UserRole.ROLE_USER, "example7@gmail.com"));
        // when
        Page<User> userPage = userRepository.findByIsDeletedFalse(PageRequest.of(0, 5));
        // then
        assertThat(userPage.getTotalPages()).isEqualTo(2);
        assertThat(userPage.getTotalElements()).isEqualTo(7);
        assertThat(userPage.isFirst()).isTrue();
        assertThat(userPage.hasNext()).isTrue();
    }
}