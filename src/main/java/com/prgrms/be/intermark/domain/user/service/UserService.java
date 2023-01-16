package com.prgrms.be.intermark.domain.user.service;

import com.prgrms.be.intermark.auth.OAuthAttribute;
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
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public void assignRefreshToken(Long userId,String refreshToken){
        Optional<User> user = userRepository.findById(userId);
        user.map(user1 ->{
            user1.setRefreshToken(refreshToken);
            return user1;
        }).orElseThrow(EntityNotFoundException::new);

    }
}
