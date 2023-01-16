package com.prgrms.be.intermark.auth.constant;

public final class JwtConstants {
    public static final long ACCESS_TOKEN_EXP = 1800000;
    public static final long REFRESH_TOKEN_EXP = 604800000;
    public static final int REFRESH_TOKEN_COOKIE_MAX_AGE = (int) REFRESH_TOKEN_EXP / 60;
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String AUTHORITIES_KEY = "role";
}
