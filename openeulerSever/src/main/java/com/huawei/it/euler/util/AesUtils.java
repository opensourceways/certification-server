package com.huawei.it.euler.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
  
//@Component
public class AesUtils {
  
    private static final String ALGORITHM = "AES";  
    private SecretKeySpec secretKeySpec;  
  
    @Value("${aes.key}")
    private String aesKey;
  
    @PostConstruct
    public void init() {  
        // 初始化密钥  
        byte[] keyBytes = aesKey.getBytes();  
        secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);  
    }  
  
    public String encrypt(String data) throws Exception {  
        Cipher cipher = Cipher.getInstance(ALGORITHM);  
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);  
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());  
        return Base64.getEncoder().encodeToString(encryptedBytes);  
    }  
  
    public String decrypt(String encryptedData) throws Exception {  
        Cipher cipher = Cipher.getInstance(ALGORITHM);  
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);  
        byte[] originalBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));  
        return new String(originalBytes);  
    }  
}