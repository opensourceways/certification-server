/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * FileModel
 *
 * @since 2024/07/01
 */
@Data
public class FileModel {
    private Integer id;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 软件id
     */
    private Integer softwareId;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 上传用户uuid
     */
    private String uuid;
}
