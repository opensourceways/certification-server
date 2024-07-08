/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import com.huawei.it.euler.mapper.MasterDataMapper;
import com.huawei.it.euler.model.entity.AuditLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * LogUtils
 *
 * @since 2024/07/02
 */
@Component
@Slf4j
public class LogUtils {
    @Autowired
    private MasterDataMapper masterDataMapper;

    @Async
    public void insertAuditLog(HttpServletRequest request, String uuid, String module,
                               String operate, String message) {
        Date date = new Date();
        String ipAddress = getIPAddress(request);
        AuditLog auditLog = new AuditLog();
        auditLog.setTime(date)
                .setHostIp(ipAddress)
                .setUuid(uuid)
                .setModule(module)
                .setOperate(operate)
                .setMessage(message);
        masterDataMapper.insertAuditLog(auditLog);
    }

    private static String getIPAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
