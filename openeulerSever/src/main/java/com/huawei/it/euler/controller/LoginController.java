/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * LoginController
 *
 * @since 2024/07/05
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginController {


    @Autowired
    private AccountService accountService;

    @GetMapping("/login")
    public JsonResponse<String> login() {
        return new JsonResponse<>(accountService.toLogin());
    }

    @GetMapping("/callback")
    public void callBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        accountService.login(request, response);
    }

    @GetMapping("/logout")
    public JsonResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        accountService.logout(request,response);
        String logout = accountService.toLogout();
        return JsonResponse.success(logout);
    }

    /**
     * user center will call that when other application logout to clear current application session
     *
     * @param request  request
     * @param response responses
     * @return JsonResponse
     */
    @GetMapping("/logoutForCenter")
    public JsonResponse<String> logoutForCenter(HttpServletRequest request, HttpServletResponse response) {
        accountService.logoutForCenter(request, response);
        return JsonResponse.success();
    }

}
