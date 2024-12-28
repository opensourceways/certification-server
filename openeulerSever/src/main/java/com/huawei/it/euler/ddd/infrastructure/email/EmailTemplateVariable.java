/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.email;

/**
 * 邮件业务相关变量
 *
 * @author zhaoyan
 * @since 2024-12-20
 */
public class EmailTemplateVariable {

    /**
     * 用户中心自动生成邮箱，该邮箱无法收到邮件
     */
    public static final String AUTO_GENERATE_EMAIL = "@user.noreply.osinfra.cn";

    /**
     * 测评业务进度通知模板
     */
    public static final String PROGRESS_NOTICE_TEMPLATE_PATH = "/docs/ProgressNoticeEmailTemplate.html";

    /**
     * 测评业务进度通知主题
     */
    public static final String PROGRESS_NOTICE_SUBJECT = "测评业务进度通知";

    /**
     * 测评业务进度通知模板
     */
    public static final String APPLY_INTEL_NOTICE_PATH = "/docs/IntelNoticeEmailTemplate.html";

    /**
     * 英特尔测评业务申请通知主题
     */
    public static final String APPLY_INTEL_NOTICE_SUBJECT = "英特尔先进技术评测业务申请";

}