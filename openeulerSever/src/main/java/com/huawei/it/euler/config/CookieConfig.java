package com.huawei.it.euler.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.huawei.it.euler.util.EncryptUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CookieConfig {
    @Value("${oauth.cookie.path}")
    private String cookiePath;

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private Cache<String, Object> caffeineCache;

    public void writeCookie(HttpServletResponse response, String uuid) {
        String enUuid = encryptUtils.aesEncrypt(uuid);
        addCookie(response, "uuid", enUuid);
        addCookie(response, "openeuler", "in");
        caffeineCache.put(enUuid, uuid);
    }

    public void addCookie(HttpServletResponse response, String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath(cookiePath);
        response.addCookie(cookie);
    }

    public void cleanCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
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
            if (Objects.equals(cookie.getName(), "uuid")) {
                // 删除缓存
                caffeineCache.invalidate(cookie.getValue());
            }
        }
    }
}
