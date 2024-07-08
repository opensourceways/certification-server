/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * EncryptUtils hutool插件整合加密解密
 *
 * @since 2024/06/29
 */
@Service
public class EncryptUtils {
    private static final String ENCRYPT_TAG_WORDS = "security";

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtils.class);

    /*@Value("${data.aes.gcm.first}")
    private String aesGcmFirst;

    @Value("${data.aes.gcm.second}")
    private String aesGcmSecond;

    @Value("${data.aes.gcm.third}")
    private String aesGcmThird;

    @Value("${data.aes.gcm.salt}")
    private String aesGcmSalt;

    @Value("${data.aes.gcm.workKey}")
    private String enWorkKey;*/

    // todo 加密解密方法，无法导入包

    /**
     * 判断某个字符内容是否被加密
     *
     * @param content 手机、邮箱字段
     * @return 是否加密
     */
    public boolean isEncrypted(String content) {
        return true;
    }

    /**
     * 加密
     *
     * @param content 手机、邮箱字段
     * @return 加密文本
     */
    public String aesEncrypt(String content) {
        return content;
    }

    /**
     * 解密
     *
     * @param enContent 密文
     * @return 解密文本
     */
    public String aesDecrypt(String enContent) {
        return enContent;
    }
}
