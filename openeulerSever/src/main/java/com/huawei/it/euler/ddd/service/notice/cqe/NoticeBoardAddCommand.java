/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.notice.cqe;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * @author zhaoyan
 * @since 2024-12-24
 */
@Tag(name = "系统公告发布命令", description = "系统公告发布命令")
@Data
public class NoticeBoardAddCommand {

    @Schema(description = "公告消息内容")
    @NotNull(message = "公告消息内容不能为空！")
    String noticeInfo;

    @Schema(description = "公告消息过期时间")
    private Date expireTime;
}
