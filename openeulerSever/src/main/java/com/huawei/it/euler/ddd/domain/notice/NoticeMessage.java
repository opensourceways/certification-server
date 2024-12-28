/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice;

import com.huawei.it.euler.ddd.domain.notice.primitive.MsgType;
import com.huawei.it.euler.ddd.domain.notice.primitive.SendType;
import com.huawei.it.euler.ddd.domain.notice.primitive.Status;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafKaMessageDTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统消息通知记录
 *
 * @author zhaoyan
 * @since 2024-12-16
 */
@Data
public class NoticeMessage {
    private final static String DEFAULT_SENDER = "system";
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 发送方，默认：system
     */
    public String sender = DEFAULT_SENDER;

    /**
     * 接收方
     */
    public String receiver;

    /**
     * 消息类型
     */
    public MsgType msgType;

    /**
     * 发送方式
     */
    public SendType sendType;

    /**
     * 模板
     */
    public String template;

    /**
     * 消息主题
     */
    public String subject;

    /**
     * 消息内容
     */
    public String content;

    /**
     * KafKa消息内容
     */
    public KafKaMessageDTO kafKaMessageDTO;

    /**
     * 消息状态
     */
    public Status status;

    /**
     * 备注
     */
    public String remark;

    /**
     * 创建时间
     */
    public LocalDateTime createTime;

    /**
     * 更新时间
     */
    public LocalDateTime updateTime;

    public void sendCreate(){
        this.createTime = LocalDateTime.now();
    }

    public void sendSuccess(){
        this.status = Status.SUCCESS;
    }

    public void sendFailed(String errMsg){
        this.status = Status.FAILED;
        this.remark = errMsg;
    }
}