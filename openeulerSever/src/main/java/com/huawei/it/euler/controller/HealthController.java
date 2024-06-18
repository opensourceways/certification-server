package com.huawei.it.euler.controller;

import com.huawei.it.euler.common.JsonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/health")
public class HealthController {


    @GetMapping("/check")
    public JsonResponse<String> healthCheck() {
        return JsonResponse.success("ok");
    }
}
