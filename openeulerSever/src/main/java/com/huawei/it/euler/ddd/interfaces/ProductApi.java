/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.masterdata.Product;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.Command;
import com.huawei.it.euler.ddd.service.masterdata.ProductApplicationService;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductQuery;
import com.huawei.it.euler.exception.NoLoginException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author zhaoyan
 * @since 2024-11-14
 */
@Tag(name = "产品类型接口", description = "产品类型接口")
@RestController
@RequestMapping("/product")
public class ProductApi {

    @Autowired
    private ProductApplicationService productApplicationService;

    @Autowired
    private AccountService accountService;

    @Operation(summary = "列表查询")
    @GetMapping("/getList")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<List<Product>> getList(@ParameterObject ProductQuery query) {
        List<Product> productList = productApplicationService.findList(query);
        return JsonResponse.success(productList);
    }

    @Operation(summary = "新增")
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<Product> add(@RequestBody @Validated({Command.Add.class}) ProductCommand command, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        command.setLastUpdatedBy(loginUuid);
        command.setLastUpdatedTime(new Date());
        Product product = productApplicationService.add(command);
        return JsonResponse.success(product);
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<Boolean> delete(@RequestBody @Validated({Command.Update.class}) ProductCommand command) throws NoLoginException {
        productApplicationService.delete(command);
        return JsonResponse.success(true);
    }
}
