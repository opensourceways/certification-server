/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * 公司三要素检查，调用第三方接口
 *
 * @since 2024/07/04
 */
@Slf4j
@Service
public class S3Client {

    private static final String BASE_URL = "https://openeuler.shanhaitujian.cn/certification/software/downloadAttachments";

    @Autowired
    private RestTemplate restTemplate;


    @Scheduled(fixedRate = 60000) // 60000 milliseconds = 1 minute
    public void callApiAndDownloadFile() {
        try {
            // 假设id是一个动态生成的值，这里我们用一个示例值
            String id = "744437d4-5f27-4213-9c43-dd1225cffb9b";

            URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("fileId", id)
                    .build()
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization","eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFM1MTIifQ.eyJleHAiOjE3MjMxMTM2MzksInV1aWQiOiIxMTEzMDIxMjMwMDI2MTk1NiIsImlhdCI6MTcyMzEwNjQzOX0.0_q1l3_AKHKaKCPGjFkvFDZQI8R0bOwfGN3j25MVOZ55RtA6_s_AlQKdRKdhowUCV7aFNfEuXXCEzY6K-96cMg");
            HttpEntity<Object> entity = new HttpEntity<>(null, headers);
            ResponseEntity<Resource> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    Resource.class
            );

            if (response.getBody() != null) {
                String filename = getFilenameFromResponse(response);
               log.info("filename: {}",filename);
                System.out.println("File downloaded: " + filename);
            } else {
                System.out.println("No file received from API.");
            }
        } catch (Exception e) {
            System.err.println("Error calling API or downloading file: " + e.getMessage());
        }
    }

    private String getFilenameFromResponse(ResponseEntity<Resource> response) {
        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        String filename = "downloaded_file_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            filename = contentDisposition.split("filename=")[1].replaceAll("\"", "");
        }

        return filename;
    }
}

