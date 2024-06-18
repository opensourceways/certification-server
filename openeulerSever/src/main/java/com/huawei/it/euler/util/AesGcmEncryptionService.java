package com.huawei.it.euler.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class AesGcmEncryptionService {

    private static final int GCM_IV_LENGTH = 12; // GCM模式下的IV长度
    private static final int GCM_TAG_LENGTH = 16; // GCM模式下的TAG长度
    private static final String ALGORITHM = "AES"; // 使用的加密算法
    private static final Logger log = LoggerFactory.getLogger(AesGcmEncryptionService.class);
    private SecretKeySpec secretKeySpec;

     @Value("${aes.key}")
    private String aesKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(aesKey);
        if (keyBytes.length != 32) { // 确保密钥长度为256位
            log.error("无效的AES密钥长度（必须是32字节）");
            throw new IllegalArgumentException("无效的AES密钥长度（必须是32字节）");
        }
        secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public String encrypt(String plaintext) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedData = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encryptedData, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public String decrypt(String ciphertext) throws Exception {
        byte[] decodedData = Base64.getDecoder().decode(ciphertext);

        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(decodedData, 0, iv, 0, iv.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec);

        byte[] decryptedData = cipher.doFinal(decodedData, GCM_IV_LENGTH, decodedData.length - GCM_IV_LENGTH);

        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
