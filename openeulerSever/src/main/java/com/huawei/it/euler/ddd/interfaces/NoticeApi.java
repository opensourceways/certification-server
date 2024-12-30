/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.notice.NoticeBoard;
import com.huawei.it.euler.ddd.service.notice.NoticeApplicationService;
import com.huawei.it.euler.ddd.service.notice.cqe.NoticeBoardAddCommand;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 消息api
 *
 * @author zhaoyan
 * @since 2024-12-24
 */
@RestController
@RequestMapping("/notice")
public class NoticeApi {

    @Autowired
    private NoticeApplicationService noticeApplicationService;

    /**
     * 系统公告消息查询
     *
     * @return JsonResponse 系统公告集合
     */
    @Operation(summary = "系统公告消息")
    @GetMapping("/findNoticeBoardList")
    public JsonResponse<List<NoticeBoard>> findNoticeBoardList() {
        return JsonResponse.success(noticeApplicationService.findActiveList());
    }

    @Operation(summary = "发布系统公告")
    @PostMapping("/publishBoard")
    public JsonResponse<NoticeBoard> publishBoard(NoticeBoardAddCommand addCommand) {
        NoticeBoard publish = noticeApplicationService.publish(addCommand);
        return JsonResponse.success(publish);
    }

}
