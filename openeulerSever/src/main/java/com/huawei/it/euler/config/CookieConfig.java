package com.huawei.it.euler.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieConfig {
    @Value("${oauth.cookie.path}")
    private String cookiePath;

    public void addCookie(HttpServletResponse response, String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath(cookiePath);
        response.addCookie(cookie);
    }

    public void cleanCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie == null) {
                continue;
            }
            cookie = new Cookie(cookie.getName(), cookie.getValue());
            cookie.setPath(cookiePath);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    public void writeSessionInCookie(HttpServletResponse response, String sessionId) {
        addCookie(response, "sessionId", sessionId);
        addCookie(response, "openeuler", "in");
    }

    public void writeXsrfInCookie(HttpServletResponse response,String xsrfKey, String xsrfToken) {
        Cookie cookie = new Cookie(xsrfKey, xsrfToken);
        cookie.setSecure(true);
        cookie.setHttpOnly(false);
        cookie.setPath(cookiePath);
        response.addCookie(cookie);
    }

    public String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "";
        }
        for (Cookie cookie : cookies) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return "";
    }
}
