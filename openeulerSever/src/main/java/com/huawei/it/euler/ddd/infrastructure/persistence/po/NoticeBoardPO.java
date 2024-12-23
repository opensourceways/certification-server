/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统公告 PO对象
 *
 * @author zhaoyan
 * @since 2024-12-16
 */
@Data
@TableName("notice_board_t")
public class NoticeBoardPO {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 公告消息
     */
    @TableField(value = "notice_info")
    String noticeInfo;

    /**
     * 是否有效状态
     */
    @TableField(value = "is_active")
    String isActive;

    /**
     * 过期时间
     */
    @TableField(value = "expire_time")
    private Date expireTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}