package com.prgrms.be.intermark.auth;

import com.prgrms.be.intermark.domain.user.dto.UserIdAndRoleDTO;
import com.prgrms.be.intermark.domain.user.service.UserService;
import com.prgrms.be.intermark.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.prgrms.be.intermark.auth.constant.JwtConstants.REFRESH_TOKEN_COOKIE_MAX_AGE;
import static com.prgrms.be.intermark.auth.constant.JwtConstants.REFRESH_TOKEN_COOKIE_NAME;

@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    public OAuth2AuthenticationSuccessHandler(UserService userService, TokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {

            // db에 유저정보 저장
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User principal = oauth2Token.getPrincipal();
            String social = oauth2Token.getAuthorizedClientRegistrationId();
            log.info("{}", principal.getAttributes());
            UserIdAndRoleDTO userIdAndRoleDTO = userService.join(principal, social);

            String aceessToken = tokenProvider.createAceessToken(userIdAndRoleDTO.userId(), userIdAndRoleDTO.userRole());
            log.info(aceessToken);

            String refreshToken = tokenProvider.createRefreshToken(userIdAndRoleDTO.userId(), userIdAndRoleDTO.userRole());

            userService.assignRefreshToken(refreshToken);

            CookieUtil.deleteCookieByName(REFRESH_TOKEN_COOKIE_NAME, request, response);
            CookieUtil.addCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken, REFRESH_TOKEN_COOKIE_MAX_AGE, response);

            String loginSuccessJson = generateLoginSuccessJson(aceessToken);
            response.setContentType("application/json;charset=UTF-8");
            response.setContentLength(loginSuccessJson.getBytes(StandardCharsets.UTF_8).length);
            response.getWriter().write(loginSuccessJson);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private String generateLoginSuccessJson(String accessToken) {
        return "{\"token\":\"" + accessToken + "\"}";
    }
}
