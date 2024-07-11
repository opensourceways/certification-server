/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * HttpClientUtils
 *
 * @since 2024/06/28
 */
public class HttpClientUtils {

    /**
     * 获取 CloseableHttpClient 方法
     *
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient acceptsUntrustedCertsHttClient()
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // 设置 SSL 上下文，以接受不受信任的证书
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial((chain, authType) -> true).build();

        // 创建 SSL 连接工厂
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

        // 创建连接管理器
        PoolingHttpClientConnectionManager mgr = PoolingHttpClientConnectionManagerBuilder
                .create().setSSLSocketFactory(sslSocketFactory).build();
        mgr.setMaxTotal(200);
        mgr.setDefaultMaxPerRoute(100);

        // 创建 HttpClient 构建器
        return HttpClients.custom()
                .setConnectionManager(mgr)
                .evictIdleConnections(TimeValue.ofSeconds(30))
                .build();
    }
}

