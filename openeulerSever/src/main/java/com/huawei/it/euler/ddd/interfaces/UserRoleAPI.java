/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.account.UserRoleVO;
import com.huawei.it.euler.ddd.domain.account.query.UserRoleQuery;
import com.huawei.it.euler.ddd.interfaces.converter.UserRoleQueryRequestToQueryConverter;
import com.huawei.it.euler.ddd.interfaces.converter.UserRoleVOToDTOConverter;
import com.huawei.it.euler.ddd.interfaces.request.UserRoleDTO;
import com.huawei.it.euler.ddd.interfaces.request.UserRoleQueryRequest;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.UserRoleService;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.model.vo.PageResult;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/role")
public class UserRoleAPI {
    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRoleService roleService;

    @PostMapping("/getAccountRoleList:query")
    public JsonResponse<PageResult<UserRoleDTO>> getAccountRoleListByQuery(
        @Valid @RequestBody UserRoleQueryRequest userRoleQueryRequest,
        @RequestParam("curPage") @NotNull(message = "页码不能为空") @PositiveOrZero(message = "页码错误") Integer curPage,
        @RequestParam("pageSize") @NotNull(message = "每页展示条数不能为空") @Range(min = 0, max = 100,
            message = "每页展示条数超出范围") Integer pageSize,
        HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        UserRoleQuery userRoleQuery =
            UserRoleQueryRequestToQueryConverter.INSTANCE.convert(userRoleQueryRequest);
        PageResult<UserRoleVO> roleVoPage = roleService.getAccountRoleListByQuery(loginUuid, userRoleQuery, curPage, pageSize);
        return JsonResponse.success(UserRoleVOToDTOConverter.INSTANCE.convert(roleVoPage));
    }
}
