package com.prgrms.be.intermark.auth;

import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    public OAuth2AuthenticationSuccessHandler(UserService userService, TokenProvider tokenProvider) {
        this.userService= userService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication instanceof OAuth2AuthenticationToken){

            // db에 유저정보 저장
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User principal = oauth2Token.getPrincipal();
            String registrationId = oauth2Token.getAuthorizedClientRegistrationId();
            log.info("{}",principal.getAttributes());
            Long userId = userService.join(principal, registrationId);

            String aceessToken = tokenProvider.createAceessToken(userId);
            log.info(aceessToken);

            String refreshToken = tokenProvider.createRefreshToken(userId);

            userService.assignRefreshToken(userId,refreshToken);

        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
