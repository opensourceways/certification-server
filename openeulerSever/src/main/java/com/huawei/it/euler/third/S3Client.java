/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.model.entity.Attachments;
import com.huawei.it.euler.model.entity.FileModel;
import com.huawei.it.euler.util.S3Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 公司三要素检查，调用第三方接口
 *
 * @since 2024/07/04
 */
@Slf4j
@Service
public class S3Client {

    private static final String BASE_URL =
        "https://openeuler.shanhaitujian.cn/certification/software/downloadAttachments";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SoftwareMapper softwareMapper;

    @Autowired
    private S3Utils s3Utils;

//    @Scheduled(fixedRate = 60000) // 60000 milliseconds = 1 minute
    public void callApiAndDownloadFile() {
        FileModel file = new FileModel();
        try {
            if (!getFileId(file)) {
                log.info("没有需要下载的文件");
                return;
            }

            URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL).queryParam("fileId", file.getFileId()).build().toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization",
                    "eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFM1MTIifQ.eyJleHAiOjE3MjMxMTM2MzksInV1aWQiOiIxMTEzMDIxMjMwMDI2MTk1NiIsImlhdCI6MTcyMzEwNjQzOX0.0_q1l3_AKHKaKCPGjFkvFDZQI8R0bOwfGN3j25MVOZ55RtA6_s_AlQKdRKdhowUCV7aFNfEuXXCEzY6K-96cMg");
            headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
            HttpEntity<Object> entity = new HttpEntity<>(null, headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    byte[].class
            );

            // 打印响应头，用于调试
            System.out.println("Response headers:");
            response.getHeaders().forEach((key, value) -> {
                System.out.println(key + ": " + value);
            });

            // 检查 Content-Length
            List<String> contentLengthHeaders = response.getHeaders().get(HttpHeaders.CONTENT_LENGTH);
            if (contentLengthHeaders != null && !contentLengthHeaders.isEmpty()) {
                long contentLength = Long.parseLong(contentLengthHeaders.get(0));
                if (contentLength == 0) {
                    log.warn("API返回的Content-Length为0，无文件内容");
                    file.setFlag(3);  // 假设3表示没有收到文件
                    softwareMapper.updateFlag(file);
                    return;
                }
            }

            if (response.getBody() != null && response.getBody().length > 0) {
                String filename = getFilenameFromResponse(response.getHeaders());
                try (InputStream inputStream = new ByteArrayInputStream(response.getBody())) {
                    file.setFlag(1);
                    s3Utils.uploadFile(inputStream, file.getFileId());
                    log.info("File uploaded: {}， Size: {} bytes", filename, response.getBody().length);
                }
            } else {
                file.setFlag(3);
                log.warn("No file content received from API.");
            }
            softwareMapper.updateFlag(file);
        } catch (Exception e) {
            file.setFlag(2);
            softwareMapper.updateFlag(file);
            log.error("Error calling API or downloading file: " + e.getMessage(), e);
        }
    }


    private String getFilenameFromResponse(HttpHeaders headers) {
        String contentDisposition = headers.getFirst(HttpHeaders.CONTENT_DISPOSITION);
        String filename = "downloaded_file";

        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            filename = contentDisposition.split("filename=")[1].replaceAll("\"", "");
            try {
                filename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                System.err.println("Error decoding filename: " + e.getMessage());
            }
            filename = filename.replaceAll("[\\\\/:*?\"<>|]", "_");
        }

        return filename;
    }

    private Boolean getFileId(FileModel file) {
        Attachments attachments = softwareMapper.getEmptyAttachmentsNames();
        String fileId = attachments.getFileId();
        if (fileId != null) {
            file.setFileId(fileId);
            file.setFileName(attachments.getFileName());
            return true;
        }
        return false;
    }

}
