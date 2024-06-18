package com.huawei.it.euler.config.usercenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.huawei.it.euler.service.UserService;
import com.huawei.it.euler.util.EncryptUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TokenConfig {

    @Value("${eulerlogin.clientId}")
    private String clientId;

    @Value("${eulerlogin.accessTokenUrl}")
    private String accessTokenUrl;

    @Value("${eulerlogin.refreshTokenUrl}")
    private String refreshTokenUrl;

    @Value("${eulerlogin.userInfoUrl}")
    private String userInfoUrl;

    @Value("${eulerlogin.redirectUrl}")
    private String redirectUrl;

    @Value("${eulerlogin.clientSecret}")
    private String clientSecret;

    @Autowired
    private EncryptUtils encryptUtils;

    @Resource
    private Cache<String, Object> caffeineCache;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * get access token from euler user center by code
     * @param code code
     * @return token data
     */
    public JSONObject getAccessToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(formData, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(accessTokenUrl, httpEntity, String.class);
        String body = responseEntity.getBody();
        return JSONObject.parseObject(body);
    }

    /**
     * get user info from euler user center by access token.
     * @param accessToken access token
     * @return user info data
     */
    public JSONObject getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        // 如果需要，还可以设置其他头，比如 Content-Type
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                userInfoUrl,
                org.springframework.http.HttpMethod.GET,
                entity,
                String.class
        );
        String body = responseEntity.getBody();
        return JSON.parseObject(body);
    }

    /**
     * refresh euler user center cookie when half of expired times passed.
     * @param request request
     */
    public void refreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return;
        }
        String uuid = null;
        String ut_key = null;
        String yg_key = null;
        for (Cookie cookie : cookies) {
            if (cookie == null) {
                continue;
            }
            if (Objects.equals(cookie.getName(), "uuid")) {
                uuid = cookie.getValue();
            }
            if (Objects.equals(cookie.getName(), "_U_T_")) {
                ut_key = cookie.getValue();
            }
            if (Objects.equals(cookie.getName(), "_Y_G_")) {
                yg_key = cookie.getValue();
            }
        }
        if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(ut_key) || StringUtils.isEmpty(ut_key)) {
            log.info("refresh api code error, cannot find cookie");
            return;
        }
        String enUuid = encryptUtils.aesEncrypt(uuid);
        String refreshTimeKey = "tokenRefreshTime-" + enUuid;
        Object refreshTimeValue = caffeineCache.getIfPresent(refreshTimeKey);
        // First refresh token after login, at this time refreshTimeKe is not set, so set it to 0l
        refreshTimeValue = Objects.isNull(refreshTimeValue) ? 0l : refreshTimeValue;
        long tokenRefreshTime = (long) refreshTimeValue;
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > tokenRefreshTime) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("token", ut_key);
            List<String> cookieList = new ArrayList<>();
            cookieList.add("_Y_G_=" + yg_key);
            headers.put(HttpHeaders.COOKIE, cookieList);
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(refreshTokenUrl, JSONObject.class, httpEntity);
            JSONObject refreshData = responseEntity.getBody();
            log.debug("refresh api code :{}", responseEntity.getStatusCode());
            log.debug("refresh api data :{}", refreshData.toJSONString());
            if (HttpStatus.OK.value() == responseEntity.getStatusCode().value()) {
                JSONObject dataJSONObject = refreshData.getJSONObject("data");
                if (dataJSONObject == null) {
                    return;
                }
                int tokenIntervalMin;
                if (dataJSONObject.containsKey("token_expires_in")) {
                    tokenIntervalMin = dataJSONObject.getInteger("token_expires_in");
                } else {
                    // default 30 min
                    tokenIntervalMin = 30 * 60;
                }
                // half of tokenIntervalMin times passed then refresh the token
                long newTokenRefreshTime = tokenIntervalMin / 2 * 1000L + System.currentTimeMillis();
                String finalUuid = uuid;
                caffeineCache.policy().expireVariably().ifPresent(e -> {
                    e.put(refreshTimeKey, newTokenRefreshTime, tokenIntervalMin, TimeUnit.MINUTES);
                    e.put(enUuid, finalUuid, tokenIntervalMin, TimeUnit.MINUTES);
                });
            }
        }
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
        } catch (ExpiredJwtException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException(e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
