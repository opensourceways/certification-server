package com.huawei.it.euler.ddd.service;

import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.config.CookieConfig;
import com.huawei.it.euler.ddd.domain.account.*;
import com.huawei.it.euler.ddd.infrastructure.oidc.OidcAuthService;
import com.huawei.it.euler.ddd.infrastructure.oidc.OidcCookie;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.util.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Slf4j
@Service
public class AccountService {
    @Autowired
    private OidcAuthService oidcAuthService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private XsrfService xsrfService;

    @Autowired
    private LogUtils logUtils;

    @Autowired
    private CookieConfig cookieConfig;

    public String toLogin() {
        return oidcAuthService.getLoginUrl();
    }

    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        UserInfo userInfo = oidcAuthService.getUserInfoByCode(code);
        userInfoService.saveUser(userInfo);
        String sessionId = sessionService.create();
        try {
            sessionService.save(sessionId, userInfo.getUuid());
            logUtils.insertAuditLog(request, userInfo.getUuid(), "login", "login in", "user login in");
        } catch (Exception e) {
            log.error("save session failed!");
            log.error(e.getMessage());
        }
        cookieConfig.writeSessionInCookie(response, sessionId);
        String token = xsrfService.refreshToken(userInfo.getUuid());
        cookieConfig.writeXsrfInCookie(response, xsrfService.getResponseHeaderKey(), token);
        response.addHeader(xsrfService.getResponseHeaderKey(), token);
        response.sendRedirect(oidcAuthService.redirectToIndex());
    }

    public boolean isLogin(HttpServletRequest request, HttpServletResponse response) {
        boolean isLoginLocal = false;
        try {
            String sessionId = getSessionId(request);
            isLoginLocal = sessionService.isAuth(sessionId);
        } catch (Exception ignored) {
        }
        if (isLoginLocal) {
            return true;
        }

        try {
            OidcCookie oidcCookie = getOidcCookie(request);
            JSONObject loginObj = oidcAuthService.isLogin(oidcCookie);
            String uuid = loginObj.getString("userId");

            UserInfo userInfo = oidcAuthService.getUserInfo(uuid);
            userInfoService.saveUser(userInfo);

            String sessionId = sessionService.create();
            if (loginObj.containsKey("tokenExpireInterval")){
                int tokenExpiresIn = loginObj.getInteger("tokenExpireInterval");
                sessionService.save(sessionId, uuid, tokenExpiresIn);
            } else {
                sessionService.save(sessionId, uuid);
            }
            logUtils.insertAuditLog(request, uuid, "login", "login in", "user login in");
            cookieConfig.writeSessionInCookie(response, sessionId);

            String token = xsrfService.refreshToken(uuid);
            cookieConfig.writeXsrfInCookie(response, xsrfService.getResponseHeaderKey(), token);
            response.addHeader(xsrfService.getResponseHeaderKey(), token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void refreshLogin(HttpServletRequest request) {
        try {
            String sessionId = getSessionId(request);
            boolean needRefresh = sessionService.isNeedRefresh(sessionId);
            if (!needRefresh) {
                return;
            }
            OidcCookie oidcCookie = getOidcCookie(request);
            JSONObject loginObj = oidcAuthService.isLogin(oidcCookie);
            String uuid = loginObj.getString("userId");
            if (loginObj.containsKey("tokenExpireInterval")){
                int tokenExpiresIn = loginObj.getInteger("tokenExpireInterval");
                sessionService.save(sessionId, uuid, tokenExpiresIn);
            } else {
                sessionService.save(sessionId, uuid);
            }
        } catch (Exception e) {
            log.error("refresh login failed!");
            log.error(e.getMessage());
        }
    }

    public void setAuthentication(HttpServletRequest request) {
        try {
            String loginUuid = getLoginUuid(request);
            List<GrantedAuthority> userAuthorities = roleService.getUserAuthorities(loginUuid);
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginUuid, null, userAuthorities);
            SecurityContextHolder.getContext().setAuthentication(token);
        } catch (NoLoginException e) {
            log.error("set authentication failed!");
            log.error(e.getMessage());
        }
    }

    public UserInfo getLoginUser(HttpServletRequest request) throws NoLoginException {
        String sessionId = null;
        try {
            sessionId = getSessionId(request);
        } catch (NoLoginException e) {
            throw new NoLoginException();
        }
        String uuid;
        try {
            uuid = sessionService.getUuid(sessionId);
        } catch (Exception e) {
            throw new NoLoginException();
        }
        return userInfoService.getUser(uuid);
    }

    public String getLoginUuid(HttpServletRequest request) throws NoLoginException {
        String sessionId = null;
        try {
            sessionId = getSessionId(request);
        } catch (NoLoginException e) {
            throw new NoLoginException();
        }
        try {
            return sessionService.getUuid(sessionId);
        } catch (Exception e) {
            throw new NoLoginException();
        }
    }

    private String getSessionId(HttpServletRequest request) throws NoLoginException {
        String sessionId = cookieConfig.getCookie(request,"sessionId");
        if (StringUtils.isEmpty(sessionId)){
            throw new NoLoginException();
        }
        return sessionId;
    }

    private OidcCookie getOidcCookie(HttpServletRequest request) throws NoLoginException {
        String uT = cookieConfig.getCookie(request,"_U_T_");
        String yG = cookieConfig.getCookie(request,"_Y_G_");
        if (StringUtils.isEmpty(uT) || StringUtils.isEmpty(yG)){
            throw new NoLoginException();
        }
        OidcCookie oidcCookie = new OidcCookie();
        oidcCookie.set_U_T_(uT);
        oidcCookie.set_Y_G_(yG);
        return oidcCookie;
    }

    public UserInfo getUserInfo(String uuid){
        UserInfo userInfo = userInfoService.getUser(uuid);
        if (userInfo == null){
            userInfo = oidcAuthService.getUserInfo(uuid);
            userInfoService.saveUser(userInfo);
        }
        return userInfo;
    }

    public List<UserInfo> getUserInfoList(List<String> uuidList){
        return uuidList.stream().map(this::getUserInfo).toList();
    }

    public List<UserInfo> getUserInfoList(int roleId){
        List<String> uuidList = roleService.getUuidListByRoleId(roleId);
        return uuidList.stream().map(this::getUserInfo).toList();
    }

    public boolean isPartner(String uuid){
        UserInfo userInfo = getUserInfo(uuid);
        return roleService.isPartner(userInfo.getRoleList());
    }

    public String toLogout(){
        return oidcAuthService.getLogoutUrl();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String sessionId = getSessionId(request);
            String uuid = sessionService.clear(sessionId);
            cookieConfig.cleanCookie(request,response);
            logUtils.insertAuditLog(request, uuid, "login", "login out", "user login out");
        } catch (NoLoginException e) {
            log.error("logout failed!");
            log.error(e.getMessage());
        }
    }
    public void logoutForCenter(HttpServletRequest request, HttpServletResponse response) {
        String authorization = request.getHeader("Authorization");
        String uuid = oidcAuthService.verifyJwt(authorization);
        sessionService.clearByUuid(uuid);
        cookieConfig.cleanCookie(request,response);
        logUtils.insertAuditLog(request, uuid, "login", "login out", "user login out by center");
    }

}