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

    /**
     * 兼容消息topic
     */
    public static final String TOPIC = "openeuler_certification";

    /**
     * 消息类型：通知
     */
    public static final String TYPE_NOTICE = "notice";

    /**
     * 消息类型：待办
     */
    public static final String TYPE_TODO = "todo";

    /**
     * 待办状态: 新建
     */
    public static final String TODO_STATUS_CREATE = "create";

    /**
     * 待办状态: 完成
     */
    public static final String TODO_STATUS_DONE = "done";

    /**
     * 测评申请审核待办内容模板
     */
    public static final String CONTENT_TODO = "您收到一笔测评业务申请，请及时处理！";

    /**
     * 企业审核结果通知内容模板
     */
    public static final String CONTENT_COMPANY_NOTICE = "您的企业认证已%s，审核意见：%s，请及时处理！";

    /**
     * 测评进度通知内容模板
     */
    public static final String CONTENT_PROGRESS_NOTICE = "您的%s已%s，当前状态：%s，请及时处理！";

    /**
     * 兼容测评主页链接
     */
    public static final String URL_INDEX = "/";

    /**
     * 测评申请详情链接
     */
    public static final String URL_SOFTWARE_DETAIL = "/certificationDetailsHuawei?id=%s&productName=%s";

    /**
     * 组装测评进度通知内容
     * @param productName 测评产品名称
     * @param handler 测评操作结果
     * @param node 当前测评节点
     * @return 通知内容
     */
    public static String getProgressNoticeContent(String productName,String handler,String node){
        return String.format(CONTENT_PROGRESS_NOTICE, productName, handler, node);
    }

    /**
     * 组装企业审核通知内容
     * @param handler 审核结果
     * @param message 审核意见
     * @return 通知内容
     */
    public static String getCompanyNoticeContent(String handler, String message) {
        return String.format(CONTENT_COMPANY_NOTICE, handler, message);
    }

    /**
     * 组装测评详情链接
     * @param id 测评业务id
     * @param productName 测评业务产品名称
     * @return 详情链接
     */
    public static String getSoftwareDetailUrl(String id, String productName) {
        return String.format(URL_SOFTWARE_DETAIL, id, productName);
    }
}
