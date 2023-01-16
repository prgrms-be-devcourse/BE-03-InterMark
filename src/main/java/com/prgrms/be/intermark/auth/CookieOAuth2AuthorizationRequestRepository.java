package com.prgrms.be.intermark.auth;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Base64;
import java.util.Optional;

import static org.springframework.web.util.WebUtils.getCookie;

public class CookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String REDIRECT_URI_PARAMCOOKIE_NAME = "redirect_uri";
    public static final String REFRESH_TOKEN = "refresh_token";
    private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

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
            Cookie cookie = new Cookie(cookieName, value);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(cookieExpireSeconds);
            response.addCookie(cookie);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return loadAuthorizationRequest(request);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return getCookie(request)
                .map(cookie -> {
                    OAuth2AuthorizationRequest oauth2Request = getOAuth2AuthorizationRequest(cookie);
                    clear(cookie, response);
                    return oauth2Request;
                })
                .orElse(null);
    }

    private Optional<Cookie> getCookie(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getCookie(request, cookieName));
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
