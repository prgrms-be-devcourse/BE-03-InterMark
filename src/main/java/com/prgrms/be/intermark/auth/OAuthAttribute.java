package com.prgrms.be.intermark.auth;

import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;


@Getter
public class OAuthAttribute {

    private static final String PASSWORD = "NO_PASSWORD";

    private Map<String, Object> attributes;
    private String socialId;
    private SocialType socialType;
    private String nickname;
    private String email;
    private UserRole userRole;

    @Builder
    public OAuthAttribute(Map<String, Object> attributes, String socialId, SocialType socialType, String nickname, String email, UserRole userRole) {
        this.attributes = attributes;
        this.socialId = socialId;
        this.nickname = nickname;
        this.socialType = socialType;
        this.email = email;
        this.userRole = userRole;
    }

    public static OAuthAttribute of(SocialType socialType, String socialId, UserRole userRole, Map<String, Object> attributes) {
        return ofGoogle(socialId, userRole, attributes);
    }

    private static OAuthAttribute ofGoogle(String socialId, UserRole userRole, Map<String, Object> attributes) {
        return OAuthAttribute.builder()
                .nickname((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .socialId(socialId)
                .socialType(SocialType.GOOGLE)
                .userRole(userRole)
                .build();
    }

    //최초로 회원가입 되는 곳에서 사용
    public User toEntity() {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .role(this.userRole)
                .socialId(socialId)
                .social(socialType)
                .build();
    }
}
