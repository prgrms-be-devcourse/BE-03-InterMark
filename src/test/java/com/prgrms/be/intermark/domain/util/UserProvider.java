package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.user.Social;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProvider {

    public static User createUser() {
        return User.builder()
                .birth(LocalDate.of(1997, 10, 10))
                .social(Social.GOOGLE)
                .socialId("intermark")
                .refreshToken("abcdefg")
                .nickname("인터마크 관리자")
                .role(UserRole.ADMIN)
                .isDeleted(false)
                .email("intermark@gmail.com")
                .build();
    }
}
