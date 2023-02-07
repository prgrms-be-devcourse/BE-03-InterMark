package com.prgrms.be.intermark.auth;

import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        SocialType socialType = SocialType.valueOf(userRequest.getClientRegistration()
                .getRegistrationId()
                .toUpperCase());
        String socialId = oAuth2User.getName();
        Optional<User> userAlreadyExist = userRepository.findBySocialTypeAndSocialIdAndIsDeletedFalse(socialType, socialId);
        if(userAlreadyExist.isPresent()) {
            UserRole role = userAlreadyExist.get().getRole();
            return new CustomUserPrincipal("sub", role, oAuth2User.getAttributes());
        }
        //OAuth2user를  return 해줘야 한다.
        return new CustomUserPrincipal("sub", UserRole.ROLE_USER, oAuth2User.getAttributes());
    }
}
