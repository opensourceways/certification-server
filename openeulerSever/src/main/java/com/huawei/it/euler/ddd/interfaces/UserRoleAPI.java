/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.account.UserRole;
import com.huawei.it.euler.ddd.domain.account.UserRoleVO;
import com.huawei.it.euler.ddd.domain.account.query.UserRoleQuery;
import com.huawei.it.euler.ddd.interfaces.converter.UserRoleModifyRequestToEntityConverter;
import com.huawei.it.euler.ddd.interfaces.converter.UserRoleRequestToEntityConverter;
import com.huawei.it.euler.ddd.interfaces.converter.UserRoleRequestToQueryConverter;
import com.huawei.it.euler.ddd.interfaces.converter.UserRoleVOToDTOConverter;
import com.huawei.it.euler.ddd.interfaces.request.UserRoleDTO;
import com.huawei.it.euler.ddd.interfaces.request.UserRoleModifyRequest;
import com.huawei.it.euler.ddd.interfaces.request.UserRoleRequest;
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
    @PreAuthorize("hasRole('admin')")
    public JsonResponse<PageResult<UserRoleDTO>> getAccountRoleListByQuery(
        @Valid @RequestBody UserRoleRequest userRoleRequest,
        @RequestParam("curPage") @NotNull(message = "页码不能为空") @PositiveOrZero(message = "页码错误") Integer curPage,
        @RequestParam("pageSize") @NotNull(message = "每页展示条数不能为空") @Range(min = 0, max = 100,
            message = "每页展示条数超出范围") Integer pageSize,
        HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        UserRoleQuery userRoleQuery = UserRoleRequestToQueryConverter.INSTANCE.convert(userRoleRequest);
        PageResult<UserRoleVO> roleVoPage =
            roleService.getAccountRoleListByQuery(loginUuid, userRoleQuery, curPage, pageSize);
        return JsonResponse.success(UserRoleVOToDTOConverter.INSTANCE.convert(roleVoPage));
    }

    @PostMapping("/AccountRole")
    @PreAuthorize("hasRole('admin')")
    public JsonResponse<String> createAccountRole(@Valid @RequestBody UserRoleRequest userRoleRequest,
        HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        UserRole userRole = UserRoleRequestToEntityConverter.INSTANCE.convert(userRoleRequest);
        String id = roleService.createUserRole(loginUuid, userRole);
        return JsonResponse.success(id);
    }

    @PutMapping("/AccountRole")
    @PreAuthorize("hasRole('admin')")
    public JsonResponse<String> modifyAccountRole(@Valid @RequestBody UserRoleModifyRequest userRoleRequest,
        HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        UserRole userRole = UserRoleModifyRequestToEntityConverter.INSTANCE.convert(userRoleRequest);
        String id = roleService.modifyUserRole(loginUuid, userRole);
        return JsonResponse.success(id);
    }

    @DeleteMapping("/AccountRole")
    @PreAuthorize("hasRole('admin')")
    public JsonResponse<String> deleteAccountRole(@Valid @RequestBody UserRoleDTO userRoleDTO,
        HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        roleService.deleteUserRole(loginUuid, userRoleDTO.getId());
        return JsonResponse.success("success");
    }
}
