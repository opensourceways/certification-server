/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;


import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.ObsObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * S3Utils
 *
 * @since 2024/07/01
 */
@Slf4j
@Component
public class S3Utils {
    @Value("${s3.endPoint}")
    private String endPoint;

    @Value("${s3.ak}")
    private String ak;

    @Value("${s3.sk}")
    private String sk;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Value("${s3.pathStyle}")
    private Boolean pathStyle;

    /**
     * 上传文件到s3桶
     *
     * @param file file
     * @param fileId fileId
     * @throws ObsException
     * @throws IOException
     */
    public void uploadFile(MultipartFile file, String fileId) throws ObsException, IOException {
        try (ObsClient obsClient = getObsClient()) {
            obsClient.putObject(bucketName, fileId, file.getInputStream());
        }
    }

    /**
     * 上传文件到s3桶
     *
     * @param inputStream inputStream
     * @param fileId fileId
     * @throws ObsException
     * @throws IOException
     */
    public void uploadFile(InputStream inputStream, String fileId) throws ObsException, IOException {
        try (ObsClient obsClient = getObsClient()) {
            obsClient.putObject(bucketName, fileId, inputStream);
        }
    }

    public InputStream downloadFile(String fileId) throws IOException {
        InputStream inputStream;
        try (ObsClient obsClient = getObsClient()) {
            ObsObject obsObject = obsClient.getObject(bucketName, fileId);
            inputStream = obsObject.getObjectContent();
        }
        return inputStream;
    }

    private ObsClient getObsClient() {
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        config.setPathStyle(pathStyle);
        return new ObsClient(ak, sk, config);
    }
}
