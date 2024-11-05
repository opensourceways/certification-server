/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.file;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("attachments_t")
public class AttachmentPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 附件名称
     */
    @TableField("filename")
    private String fileName;

    /**
     * 附件id
     */
    @TableField("fileid")
    private String fileId;

    /**
     * 软件认证id
     */
    @TableField("software_id")
    private String softwareId;

    /**
     * 软件认证id
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 文件具体路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 上传用户id
     */
    @TableField("uuid")
    private String uuid;
}
