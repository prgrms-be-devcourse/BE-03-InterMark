package com.prgrms.be.intermark.auth.constant;

public final class JwtConstants {
    public static final long ACCESS_TOKEN_EXP = 1800000;
    public static final long REFRESH_TOKEN_EXP = 604800000;
    public static final int REFRESH_TOKEN_COOKIE_MAX_AGE = (int) REFRESH_TOKEN_EXP / 60;
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
}
