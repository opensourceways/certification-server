/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.software.SoftwareDisplayPO;
import com.huawei.it.euler.ddd.domain.software.SoftwareDisplayService;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.exception.NoLoginException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/softwareDisplay")
public class SoftwareDisplayApi {

    @Autowired
    private SoftwareDisplayService displayService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/getPage")
    public JsonResponse<Page<SoftwareDisplayPO>> getPage(
            @RequestParam(value = "productName",required = false) String productName,
            @RequestParam("curPage") @NotNull(message = "页码不能为空") @PositiveOrZero(message = "页码错误") Integer curPage,
            @RequestParam("pageSize") @NotNull(message = "每页展示条数不能为空")
            @Range(min = 0, max = 100, message = "每页展示条数超出范围") Integer pageSize,
            HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        Page<SoftwareDisplayPO> hiddenPage = displayService.getHiddenPage(loginUuid, productName, curPage, pageSize);
        return JsonResponse.success(hiddenPage);
    }

    @PostMapping("/hidden")
    public JsonResponse<Boolean> hidden(@RequestBody SoftwareDisplayDto softwareDisplayDto, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        boolean hidden = displayService.hidden(loginUuid, softwareDisplayDto.getSoftwareId());
        return JsonResponse.success(hidden);
    }

    @PostMapping("/show")
    public JsonResponse<Boolean> show(@RequestBody SoftwareDisplayDto softwareDisplayDto) {
        boolean show = displayService.show(softwareDisplayDto.getIdList());
        return JsonResponse.success(show);
    }
}
