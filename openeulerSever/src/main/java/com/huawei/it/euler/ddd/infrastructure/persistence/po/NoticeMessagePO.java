/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统消息通知记录 PO对象
 *
 * @author zhaoyan
 * @since 2024-12-16
 */
@Data
@TableName("notice_message_t")
public class NoticeMessagePO {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 发送方
     */
    @TableField(value = "sender")
    public String sender;

    /**
     * 接收方
     */
    @TableField(value = "receiver")
    public String receiver;

    /**
     * 消息类型
     */
    @TableField(value = "msg_type")
    public String msgType;

    /**
     * 发送方式
     */
    @TableField(value = "send_type")
    public String sendType;

    /**
     * 模板
     */
    @TableField(value = "template")
    public String template;

    /**
     * 消息主题
     */
    @TableField(value = "subject")
    public String subject;

    /**
     * 消息内容
     */
    @TableField(value = "content")
    public String content;

    /**
     * 消息状态
     */
    @TableField(value = "status")
    public String status;

    /**
     * 备注
     */
    @TableField(value = "remark")
    public String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    public LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    public LocalDateTime updateTime;
}
