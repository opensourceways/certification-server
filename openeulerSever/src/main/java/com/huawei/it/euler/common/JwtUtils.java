/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt工具类
 *
 * @since 2024/06/28
 */
@Data
@Component
@ConfigurationProperties(prefix = "euler.jwt")
public class JwtUtils {
    @Value("${euler.jwt.expire}")
    private long expire;

    @Value("${euler.jwt.secret}")
    private String secret;

    @Value("${euler.jwt.header}")
    private String header;

    /**
     * token创建
     *
     * @param uuid uuid
     * @return
     */
    public String generateToken(String uuid) {
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);
        Map<String, Object> payloadInfo = new HashMap<>();
        payloadInfo.put("uuid", uuid);
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setSubject(uuid)
                .setClaims(payloadInfo)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}
