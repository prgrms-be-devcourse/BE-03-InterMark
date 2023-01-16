package com.prgrms.be.intermark.domain.user.service;

import com.prgrms.be.intermark.auth.OAuthAttribute;
import com.prgrms.be.intermark.auth.TokenProvider;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    
    public UserService(UserRepository userRepository, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByProviderAndProviderId(SocialType social, String socialId) {
        return userRepository.findBySocialTypeAndSocialId(social, socialId);
    }

    @Transactional
    public Long join(OAuth2User oauth2User, String authorizedClientRegistrationId) {
        SocialType socialType = SocialType.valueOf(authorizedClientRegistrationId.toUpperCase());
        String socialId = oauth2User.getName();
        OAuthAttribute authAttribute = OAuthAttribute.of(socialType, socialId, oauth2User.getAttributes());
        return findByProviderAndProviderId(socialType, socialId)
                .map(user -> {
                    log.warn("Already exists: {} for (provider: {}, providerId: {})", user, authorizedClientRegistrationId, socialId);
                    user.setUserName(authAttribute.getName());
                    return user;
                })
                .orElseGet(() -> {
                    return userRepository.save(authAttribute.toEntity());
                }).getId();
    }
    @Transactional
    public void assignRefreshToken(String refreshToken){
        // TODO: 의존성 주입 받는 것 (빈 주입) vs 인자로 넣어주는 것 vs 안에서 만들어주는 것
        String userIdFromRefreshToken = tokenProvider.getUserIdFromRefreshToken(refreshToken);
        Optional<User> user = userRepository.findById(Long.parseLong(userIdFromRefreshToken));
        user.map(user1 ->{
            user1.setRefreshToken(refreshToken);

            return user1;
        }).orElseThrow(EntityNotFoundException::new);
    }
}
