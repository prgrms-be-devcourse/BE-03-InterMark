package com.prgrms.be.intermark.domain.util;

import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProvider {

    public static User createUser() {
        return User.builder()
                .social(SocialType.GOOGLE)
                .socialId("intermark")
                .nickname("인터마크 관리자")
                .role(UserRole.ROLE_ADMIN)
                .email("intermark@gmail.com")
                .build();
    }
}