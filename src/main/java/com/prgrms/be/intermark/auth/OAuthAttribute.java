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
    private String name;
    private String email;

    @Builder
    public OAuthAttribute(Map<String, Object> attributes, String socialId, SocialType socialType, String name, String email) {
        this.attributes = attributes;
        this.socialId = socialId;
        this.name = name;
        this.socialType = socialType;
        this.email = email;
    }

    public static OAuthAttribute of(SocialType socialType, String socialId, Map<String, Object> attributes) {
        return ofGoogle(socialId, attributes);
    }

    private static OAuthAttribute ofGoogle(String socialId, Map<String, Object> attributes) {
        return OAuthAttribute.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .socialId(socialId)
                .socialType(SocialType.GOOGLE)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .userName(name)
                .email(email)
                .password(PASSWORD)
                .role(UserRole.GUEST)
                .socialId(socialId)
                .socialType(socialType)
                .build();
    }
}
