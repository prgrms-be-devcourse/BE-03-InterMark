package com.prgrms.be.intermark.util;

import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class CookieUils {


    public static void addCookie(String cookieName,String cookieValue,int maxAge, HttpServletResponse response){
        Cookie cookie = new Cookie(cookieName,cookieValue);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    public static Optional<Cookie> getCookieByName(String name,HttpServletRequest request){
        return ofNullable(WebUtils.getCookie(request,name));
    }

    public static void deleteCookieByName(String name, HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if(name.equals(cookie.getName())){
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
}

