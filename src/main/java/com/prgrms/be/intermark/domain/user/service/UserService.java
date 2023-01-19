package com.prgrms.be.intermark.domain.user.service;

import com.prgrms.be.intermark.auth.OAuthAttribute;
import com.prgrms.be.intermark.auth.TokenProvider;
import com.prgrms.be.intermark.domain.user.SocialType;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.UserRole;
import com.prgrms.be.intermark.domain.user.dto.UserIdAndRoleDTO;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
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
    public UserIdAndRoleDTO join(OAuth2User oauth2User, String social) {
        SocialType socialType = SocialType.valueOf(social.toUpperCase());
        String socialId = oauth2User.getName();
        GrantedAuthority[] grantedAuthorities = oauth2User.getAuthorities().toArray(new GrantedAuthority[0]);
        UserRole authority = UserRole.valueOf(grantedAuthorities[0].getAuthority());
        log.info("oauth2User에서 꺼내온 Authority : {}",authority);
        OAuthAttribute authAttribute = OAuthAttribute.of(socialType, socialId, authority, oauth2User.getAttributes());
        User foundedUser = findByProviderAndProviderId(socialType, socialId)
                .map(user -> {
                    //user가 전에 로그인 한 적 있음
                    log.info("Already exists: {} for (social: {}, socialId: {})", user, social, socialId);
                    user.setNickname(authAttribute.getNickname());
                    return user;
                })
                .orElseGet(() -> {
                    //처음 로그인 하면
                    log.info("첫 로그인 감지. 자동 회원가입을 진행합니다.");
                    return userRepository.save(authAttribute.toEntity());
                });
        return new UserIdAndRoleDTO(foundedUser.getId(), foundedUser.getRole());
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
