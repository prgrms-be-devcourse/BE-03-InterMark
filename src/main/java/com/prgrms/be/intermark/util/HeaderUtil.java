package com.prgrms.be.intermark.util;

import javax.servlet.http.HttpServletRequest;

import static com.prgrms.be.intermark.auth.constant.JwtConstants.HEADER_AUTHORIZATION;
import static com.prgrms.be.intermark.auth.constant.JwtConstants.TOKEN_PREFIX;

public class HeaderUtil {

    public static String getAccessToken(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_AUTHORIZATION);

        if (headerValue == null) {
            return null;
        }

        if (headerValue.startsWith(TOKEN_PREFIX)) {
            return headerValue.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
