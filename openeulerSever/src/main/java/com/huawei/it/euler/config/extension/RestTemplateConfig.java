/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.config.extension;

import com.huawei.it.euler.util.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;


/**
 * RestTemplate配置
 *
 * @since 2024/06/28
 */
@Configuration
public class RestTemplateConfig {

    @Primary
    @Bean
    public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory httpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
                // 默认处理非200的返回，会抛异常
            }
        });
        return restTemplate;
    }

    /**
     * HttpComponentsClientHttpRequestFactory工厂
     *
     * @return HttpComponentsClientHttpRequestFactory
     * @throws Exception e
     */
    @Bean
    public HttpComponentsClientHttpRequestFactory httpRequestFactory() throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.acceptsUntrustedCertsHttClient();
        HttpComponentsClientHttpRequestFactory httpFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        httpFactory.setConnectTimeout(40000);
        httpFactory.setReadTimeout(40000);
        return httpFactory;
    }
}
