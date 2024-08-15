/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.exception.UrlRetryException;

import lombok.extern.slf4j.Slf4j;

/**
 * 公司三要素检查，调用第三方接口
 *
 * @since 2024/07/04
 */
@Slf4j
@Service
public class CompanyVerifyClient {

    @Value("${verifyCompany.appKey}")
    private String verifyCompanyAppCode;

    @Value("${verifyCompany.url}")
    private String verifyCompanyInfoUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Retryable(value = {UrlRetryException.class}, maxAttempts = 2, backoff = @Backoff(delay = 3000))
    public Boolean checkCompanyInfo(String companyName, String creditNo, String legalPerson) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("X-Apig-AppCode", verifyCompanyAppCode);
        HashMap<String, Object> params = new HashMap<>();
        params.put("companyname", companyName);
        params.put("creditno", creditNo);
        params.put("legalperson", legalPerson);
        HttpEntity httpEntity = new HttpEntity(params, httpHeaders);
        ResponseEntity<String> responseEntity =
            restTemplate.postForEntity(verifyCompanyInfoUrl, httpEntity, String.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.info("response body: {}", responseEntity.getBody());
            if (responseEntity.getStatusCode() == HttpStatus.BAD_GATEWAY) {
                throw new UrlRetryException("Backend timeout");
            }
            return false;
        }
        String body = responseEntity.getBody();
        JSONObject bodyJson = JSONObject.parseObject(body);
        bodyJson = JSONObject.parseObject(bodyJson.getString("data"));
        String message = bodyJson.getString("result");
        return "1".equals(message);
    }

    @Recover
    public Boolean recover(UrlRetryException e, String input) {
        log.error("Retry failed: {}", input);
        return false;
    }
}

