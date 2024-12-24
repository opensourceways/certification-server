/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.kafka;

/**
 * kafka 消息模板
 *
 * @author zhaoyan
 * @since 2024-12-24
 */
public class KafkaMessageTemplate {

    public static final String TOPIC = "openeuler_certification";

    public static final String TYPE_NOTICE = "notice";

    public static final String TYPE_TODO = "todo";

    public static final String CONTENT_TODO = "您收到一笔测评业务申请，请及时处理！";

    public static final String CONTENT_COMPANY_NOTICE = "您的企业认证已%s，审核意见：%s，请及时处理！";

    public static final String CONTENT_PROGRESS_NOTICE = "您的%s已%s，当前状态：%s，请及时处理！";

    public static final String URL_INDEX = "/";

    public static final String URL_SOFTWARE_DETAIL = "/certificationDetailsHuawei?id=%s&productName=%s";

    public static String getProgressNoticeContent(String productName,String handler,String node){
        return String.format(CONTENT_PROGRESS_NOTICE, productName, handler, node);
    }

    public static String getCompanyNoticeContent(String handler, String message) {
        return String.format(CONTENT_COMPANY_NOTICE, handler, message);
    }

    public static String getSoftwareDetailUrl(String id, String productName) {
        return String.format(URL_SOFTWARE_DETAIL, id, productName);
    }
}
