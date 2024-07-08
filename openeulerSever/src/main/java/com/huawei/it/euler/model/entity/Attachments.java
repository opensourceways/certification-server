/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;

/**
 * Attachments
 *
 * @since 2024/07/01
 */
@Data
public class Attachments {
    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 认证id
     */
    private String softwareId;

    /**
     * 上传用户uuid
     */
    private String uuid;

    /**
     * 文件类型
     */
    private String fileType;
}
