/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import static com.huawei.it.euler.third.ConfigConstants.SDK_VERSION;
import static com.huawei.it.euler.third.ConfigConstants.REGISTER_INFO;
import static com.huawei.it.euler.third.ConfigConstants.PUBLIC_KEY_URL;
import static java.util.Objects.nonNull;

/**
 * PublicKey
 *
 * @since 2024/07/04
 */
@Slf4j
public class PublicKey {
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private static Algorithm PUBLIC_KEY_ALGORITHM;

    public static Optional<Algorithm> getJwtPublicKey() {
        if (nonNull(PUBLIC_KEY_ALGORITHM)) {
            return Optional.ofNullable(PUBLIC_KEY_ALGORITHM);
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("iam-sdk-version", SDK_VERSION);
        headers.set("iam-sdk-register-info", REGISTER_INFO);
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<PublicKeyDTO> exchange =
                    restTemplate.exchange(PUBLIC_KEY_URL, HttpMethod.GET, entity, PublicKeyDTO.class);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                PublicKeyDTO publicKeyDTO = exchange.getBody();
                jwtPublicKeyAlgorithm(publicKeyDTO.getPublicKey(), publicKeyDTO.getAlgorithm());
                return Optional.ofNullable(PUBLIC_KEY_ALGORITHM);
            }
        } catch (Exception e) {
            log.error("get public key error", e);
        }
        return Optional.empty();
    }

    public static void jwtPublicKeyAlgorithm(String jwtPublic, String jwtPublicKeyAlgorithm) {
        byte[] base64DecodePublicKey = DECODER.decode(jwtPublic);
        switch (jwtPublicKeyAlgorithm) {
            case "RS256":
                Key key = getRSAPublicKey(base64DecodePublicKey);
                PUBLIC_KEY_ALGORITHM = Algorithm.RSA256((RSAPublicKey) key, null);
                return;
            case "RS384":
                key = getRSAPublicKey(base64DecodePublicKey);
                PUBLIC_KEY_ALGORITHM = Algorithm.RSA384((RSAPublicKey) key, null);
                return;
            case "RS512":
                key = getRSAPublicKey(base64DecodePublicKey);
                PUBLIC_KEY_ALGORITHM = Algorithm.RSA512((RSAPublicKey) key, null);
                return;
            case "HS256":
                PUBLIC_KEY_ALGORITHM = Algorithm.HMAC256(base64DecodePublicKey);
                return;
            case "HS384":
                PUBLIC_KEY_ALGORITHM = Algorithm.HMAC384(base64DecodePublicKey);
                return;
            case "HS512":
                PUBLIC_KEY_ALGORITHM = Algorithm.HMAC512(base64DecodePublicKey);
                return;
            default:
                throw new RuntimeException("Verify JWT Token Exception JWT Algorithm " +
                        jwtPublicKeyAlgorithm + " is not support.");
        }
    }


    private static Key getRSAPublicKey(byte[] secretBytes) {
        try {
            X509EncodedKeySpec pkcs8KeySpec = new X509EncodedKeySpec(secretBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(pkcs8KeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException exception) {
            throw new RuntimeException("Verify JWT Token Exception could not get public key, "
                    + exception.getMessage());
        }
    }
}
