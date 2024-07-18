package com.huawei.it.euler.util;

import ch.qos.logback.core.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;


public class RequestUtils {

    public static String getShortUri(HttpServletRequest req) {
        String shortUri = (String) (req.getAttribute("JalorRequestURI"));
        if (shortUri != null) {
            return shortUri;
        } else {
            String uri = req.getRequestURI();
            String cxt = req.getContextPath();
            shortUri = (!StringUtil.isNullOrEmpty(uri) && uri.length() != cxt.length()) ? uri.substring(cxt.length()) : "";
        }
        req.setAttribute("JalorRequestURI", shortUri);
        return shortUri;
    }
}
