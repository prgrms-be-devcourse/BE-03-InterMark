package com.prgrms.be.intermark.auth;

import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        SocialType socialType = SocialType.valueOf(userRequest.getClientRegistration()
                .getRegistrationId()
                .toUpperCase());
        String socialId = oAuth2User.getName();

        OAuthAttribute authAttribute = OAuthAttribute.of(socialType, socialId, oAuth2User.getAttributes());
        User user = saveOrUpdate(authAttribute);
        log.info("user_name = {}", user.getUserName());
        return new CustomUserPrincipal(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(OAuthAttribute authAttribute) {
        User user = userRepository.findByEmail(authAttribute.getEmail())
                .map(entity -> entity.setUserName(authAttribute.getName()))
                .orElse(authAttribute.toEntity());

        return userRepository.save(user);
    }
}
