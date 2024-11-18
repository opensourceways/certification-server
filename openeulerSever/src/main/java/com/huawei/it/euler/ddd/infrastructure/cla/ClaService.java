/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.cla;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Data
@Slf4j
@Component
public class ClaService {

    @Value("${cla.check}")
    private String checkUrl;

    @Autowired
    private RestTemplate restTemplate;

    public boolean check(String email) {
        String queryUrl = checkUrl + "?email=" + email;
        HttpEntity<String> entity = new HttpEntity<>(null, null);
        ResponseEntity<String> responseEntity = restTemplate.exchange(queryUrl, HttpMethod.GET, entity, String.class);
        return JSONObject.parseObject(responseEntity.getBody()).getJSONObject("data").getBoolean("signed");
    }
}
