/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.oidc;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Data
@Slf4j
@Component
public class OidcClient {
    @Value("${eulerlogin.clientId}")
    private String clientId;

    @Value("${eulerlogin.clientSecret}")
    private String clientSecret;

    @Value("${eulerlogin.authCodeUrl}")
    private String authCodeUrl;

    @Value("${eulerlogin.accessTokenUrl}")
    private String accessTokenUrl;

    @Value("${eulerlogin.userInfoUrl}")
    private String userInfoUrl;

    @Value("${eulerlogin.redirectUrl}")
    private String redirectUrl;

    @Value("${eulerlogin.frontCallbackUrl}")
    private String frontCallbackUrl;

    @Value("${eulerlogin.logoutUrl}")
    private String logoutUrl;

    @Value("${eulerlogin.frontUrl}")
    private String frontUrl;

    @Value("${eulerlogin.refreshTokenUrl}")
    private String refreshTokenUrl;

    @Value("${eulerlogin.managerTokenUrl}")
    private String managerTokenUrl;

    @Value("${eulerlogin.managerUserInfoUrl}")
    private String managerUserInfoUrl;

    @Value("${eulerlogin.logoutForCenter}")
    private String logoutForCenter;

    @Autowired
    private RestTemplate restTemplate;

    public String getLoginUrl() {
        return String.format("%s?response_type=code&scope=openid profile email phone address offline_access&client_id=%s&redirect_uri=%s", authCodeUrl, clientId, redirectUrl);
    }

    public String getLogoutUrl() {
        return String.format("%s?client_id=%s&redirect_uri=%s", logoutUrl, clientId, frontUrl);
    }

    public String getFrontRedirectUrl(){
        return frontCallbackUrl;
    }

    public String getFrontIndexUrl(){
        return frontUrl;
    }

    public String getAccessToken(String code){
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUrl);
        formData.add("logout_uri", logoutForCenter);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(formData, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(accessTokenUrl, httpEntity, String.class);
        return JSONObject.parseObject(responseEntity.getBody()).getString("access_token");
    }

    public OidcResponse getUserInfoByAccessToken(String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        // 如果需要，还可以设置其他头，比如 Content-Type
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);
        return JSONObject.parseObject(responseEntity.getBody(),OidcResponse.class);
    }

    public String getManagerToken(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("grant_type", "token");
        jsonObject.put("app_id", clientId);
        jsonObject.put("app_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(jsonObject, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(managerTokenUrl, httpEntity, String.class);
        return responseEntity.getBody();
    }

    public OidcResponse getUserInfoByManagerToken(String uuid, String managerToken) {
        String queryUrl = managerUserInfoUrl + "?userId=" + uuid;
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", managerToken);
        // 如果需要，还可以设置其他头，比如 Content-Type
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(queryUrl, HttpMethod.GET, httpEntity, String.class);
        return JSONObject.parseObject(responseEntity.getBody(),OidcResponse.class);
    }

    public OidcResponse refreshSession(OidcCookie oidcCookie){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("token", oidcCookie.get_U_T_());
        headers.add(HttpHeaders.COOKIE, "_Y_G_=" + oidcCookie.get_Y_G_());
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(refreshTokenUrl, HttpMethod.GET, httpEntity, String.class);
        log.info("refresh session api " + refreshTokenUrl);
        log.info("refresh session param " + JSONObject.toJSONString(oidcCookie));
        log.info(JSONObject.toJSONString(responseEntity));
        OidcResponse oidcResponse = JSONObject.parseObject(responseEntity.getBody(), OidcResponse.class);
        if (oidcResponse != null){
            List<String> cookieList = responseEntity.getHeaders().get("Set-Cookie");
            oidcResponse.setCookieList(cookieList);
        }
        return oidcResponse;
    }

    /**
     * parse the data for api called by euler user center
     * @param jwtStr jwt data
     * @return required data
     */
    public String verifyJwt(String jwtStr){
        try {
            Claims claims = Jwts.parser().setSigningKey(clientSecret).parseClaimsJws(jwtStr).getBody();
            for (Map.Entry<String, Object> stringObjectEntry : claims.entrySet()) {
                if ("sub".equals(stringObjectEntry.getKey())){
                    return (String) stringObjectEntry.getValue();
                }
            }
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
