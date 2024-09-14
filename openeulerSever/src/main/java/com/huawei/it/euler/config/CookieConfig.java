package com.huawei.it.euler.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.alibaba.fastjson.JSONArray;
import com.huawei.it.euler.util.EncryptUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public void writeCookieList(HttpServletResponse response,JSONArray cookieList){
        for (Object cookieObj : cookieList) {
            String cookieStr = cookieObj.toString();
            Cookie cookie = createCookie(cookieStr);
            response.addCookie(cookie);
        }
    }

    public Cookie createCookie(String str){
        String[] cookieParamArr = str.split(";");
        String nameValueStr = cookieParamArr[0];
        String name = nameValueStr.split("=")[0].trim();
        String value = nameValueStr.split("=")[1].trim();
        Cookie cookie = new Cookie(name,value);
        for (int i = 1; i < cookieParamArr.length; i++) {
            String[] split = cookieParamArr[i].split("=");
            String attrName = split[0].toLowerCase().trim();
            String attrValue = split.length > 1 ? split[1].trim() : "";
            switch (attrName) {
                case "path" -> cookie.setPath(attrValue);
                case "secure" -> cookie.setSecure(true);
                case "httponly" -> cookie.setHttpOnly(true);
                case "max-age" -> cookie.setMaxAge(Integer.parseInt(attrValue));
                case "domain" -> cookie.setDomain(attrValue);
            }
        }
        return cookie;
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
