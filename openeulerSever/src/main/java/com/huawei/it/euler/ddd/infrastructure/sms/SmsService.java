package com.huawei.it.euler.ddd.infrastructure.sms;

import com.alibaba.fastjson.JSONObject;
import com.cloud.apigateway.sdk.utils.Client;
import com.cloud.apigateway.sdk.utils.Request;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Autowired
    private SmsProperties smsProperties;

    private static final String SIGNATURE_ALGORITHM_SDK_HMAC_SHA256 = "SDK-HMAC-SHA256";

    private static CloseableHttpClient client = null;

    @SneakyThrows
    public SmsResponse sendNotification(String templateId, String receiver, String templateParas) {
        client = createIgnoreSSLHttpClient();
        String statusCallBack = "";
        String signatureName = "";
        String body = buildRequestBody(smsProperties.getSenderId(), receiver, templateId, templateParas, statusCallBack, signatureName);
        if (body.isEmpty()) {
            logger.warn("body is null.");
            return SmsResponse.invalidParameterResponse();
        }

        Request request = new Request();
        request.setUrl(smsProperties.getMessageUrl());
        request.setKey(smsProperties.getAppKey());
        request.setSecret(smsProperties.getAppSecret());
        request.setMethod(HttpMethod.POST.name());
        request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        request.setBody(body);
        try {
            HttpRequestBase signedRequest = Client.sign(request, SIGNATURE_ALGORITHM_SDK_HMAC_SHA256);
            HttpResponse response = client.execute(signedRequest);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                String resEntityStr = EntityUtils.toString(resEntity, StandardCharsets.UTF_8);
                logger.info("Processing Body with name: {} and value: {}", System.lineSeparator(),
                        resEntityStr);
                return JSONObject.parseObject(resEntityStr, SmsResponse.class);
            }
            return SmsResponse.noResponse();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return SmsResponse.sendExceptionResponse(e.getMessage());
        }
    }

    private String buildRequestBody(String sender, String receiver, String templateId, String templateParas,
                                    String statusCallBack, String signature) throws UnsupportedEncodingException {
        StringBuilder body = new StringBuilder();
        appendToBody(body, "from=", sender);
        appendToBody(body, "&to=", receiver);
        appendToBody(body, "&templateId=", templateId);
        appendToBody(body, "&templateParas=", templateParas);
        appendToBody(body, "&statusCallback=", statusCallBack);
        appendToBody(body, "&signature=", signature);
        return body.toString();
    }

    private void appendToBody(StringBuilder body, String key, String value) throws UnsupportedEncodingException {
        if (null != value && !value.isEmpty()) {
            body.append(key).append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        }
    }

    private CloseableHttpClient createIgnoreSSLHttpClient() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (x509CertChain, authType) -> true).build();
        return HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)).build();
    }

}
