package com.prgrms.be.intermark.domain.user.repository;

import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @AfterEach
    void reset() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("유저 id와 isDeleted 상태를 이용하여 유저를 조회할 수 있다.")
    public void findByIdAndIsDeletedFalseTest() {
        // given
        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();
        userRepository.save(user);
        // when
        Optional<User> findUser = userRepository.findByIdAndIsDeletedFalse(user.getId());
        // then
        assertThat(findUser).isPresent();
        assertThat(findUser.get().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("모든 유저 조회 시 page와 size에 따른 페이징된 유저의 정보를 조회할 수 있다. (isDeleted 상태가 true인 경우는 제외됨.)")
    public void findByIsDeletedFalseTest() {
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
        List<User> content = userPage.getContent();
        assertThat(userPage.getTotalPages()).isEqualTo(2);
        assertThat(userPage.getTotalElements()).isEqualTo(7);
        assertThat(userPage.isFirst()).isTrue();
        assertThat(userPage.hasNext()).isTrue();
    }

    @Test
    @DisplayName("isDeleted 상태가 true인 경우 유저가 존재해도 조회되지 않는다.")
    public void findByWrongIdTest() {
        // given
        User user = User.builder()
                .social(SocialType.GOOGLE)
                .socialId("1")
                .role(UserRole.ROLE_USER)
                .nickname("이수영")
                .email("example1@gmail.com")
                .build();
        user.setIsDeleted(true);
        // when
        userRepository.save(user);
        Optional<User> findUser = userRepository.findByIdAndIsDeletedFalse(user.getId());
        // then
        assertThat(findUser).isEmpty();
    }
}