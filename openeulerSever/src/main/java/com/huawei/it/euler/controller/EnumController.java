/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huawei.it.euler.model.enumeration.CenterEnum;
import com.huawei.it.euler.model.enumeration.NodeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * UserController
 *
 * @since 2024/07/05
 */
@Slf4j
@RestController
@Validated
@RequestMapping("/enums")
public class EnumController {

    @GetMapping("/centers")
    public List<CenterEnum> getAllCenterEnums() {
        return CenterEnum.getAllCenters();
    }

    @GetMapping("/nodes")
    public List<NodeEnum> getAllNodeEnums() {
        return NodeEnum.getAllNodes();
    }
}