package com.prgrms.be.intermark.auth;

import com.prgrms.be.intermark.util.CookieUtil;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

import static com.prgrms.be.intermark.auth.constant.JwtConstants.OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME;

public class CookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {


    private final String cookieName;

    private final int cookieExpireSeconds;

    public CookieOAuth2AuthorizationRequestRepository() {
        this(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, 180);
    }

    public CookieOAuth2AuthorizationRequestRepository(String cookieName, int cookieExpireSeconds) {
        this.cookieName = cookieName;
        this.cookieExpireSeconds = cookieExpireSeconds;
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return getCookie(request)
                .map(this::getOAuth2AuthorizationRequest)
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            getCookie(request).ifPresent(cookie -> clear(cookie, response));
        } else {
            String value = Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(authorizationRequest));

            CookieUtil.addCookie(cookieName, value, cookieExpireSeconds, response);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return loadAuthorizationRequest(request);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return
                getCookie(request)
                        .map(cookie -> {
                            OAuth2AuthorizationRequest oauth2Request = getOAuth2AuthorizationRequest(cookie);
                            clear(cookie, response);
                            return oauth2Request;
                        })
                        .orElse(null);
    }

    private Optional<Cookie> getCookie(HttpServletRequest request) {
        return CookieUtil.getCookieByName(cookieName, request);
    }

    private void clear(Cookie cookie, HttpServletResponse response) {
        cookie.setValue("");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private OAuth2AuthorizationRequest getOAuth2AuthorizationRequest(Cookie cookie) {
        return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())
        );
    }
}
