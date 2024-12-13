package com.huawei.it.euler.config.extension;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class SmsConfig {
    private static final Logger logger = LoggerFactory.getLogger(SmsConfig.class);

    @Value("${sns.messageUrl}")
    private String messageUrl;

    @Value("${sns.senderId}")
    private String senderId;

    @Value("${sns.appKey}")
    private String appKey;

    @Value("${sns.appSecret}")
    private String appSecret;

    private static final String SIGNATURE_ALGORITHM_SDK_HMAC_SHA256 = "SDK-HMAC-SHA256";
    private static CloseableHttpClient client = null;

    @SneakyThrows
    public void sendNotification(String templateId, String receiver, String templateParas) {
        client = createIgnoreSSLHttpClient();
        String statusCallBack = "";
        String signature = "";
        String body = buildRequestBody(senderId, receiver, templateId, templateParas, statusCallBack, signature);
        if (body.isEmpty()) {
            logger.warn("body is null.");
            return;
        }

        Request request = new Request();
        request.setKey(appKey);
        request.setSecret(appSecret);
        request.setMethod("POST");
        request.setUrl(messageUrl);
        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setBody(body);
        try {
            HttpRequestBase signedRequest = Client.sign(request, SIGNATURE_ALGORITHM_SDK_HMAC_SHA256);
            HttpResponse response = client.execute(signedRequest);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                logger.info("Processing Body with name: {} and value: {}", System.getProperty("line.separator"),
                        EntityUtils.toString(resEntity, "UTF-8"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
            body.append(key).append(URLEncoder.encode(value, "UTF-8"));
        }
    }

    private CloseableHttpClient createIgnoreSSLHttpClient() throws Exception {
        SSLContext sslContext =
                new SSLContextBuilder().loadTrustMaterial(null, (x509CertChain, authType) -> true).build();
        return HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)).build();
    }
}
