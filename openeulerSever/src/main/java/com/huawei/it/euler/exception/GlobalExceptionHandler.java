/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.exception;

import com.alibaba.fastjson.JSONException;
import com.huawei.it.euler.common.JsonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.InsufficientResourcesException;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.security.AccessControlException;
import java.security.acl.NotOwnerException;
import java.util.ConcurrentModificationException;
import java.util.MissingResourceException;
import java.util.jar.JarException;

/**
 * 全局异常处理
 *
 * @since 2024/07/01
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String ERROR_MESSAGE = "系统繁忙";

    /**
     * MethodArgumentNotValidException
     *
     * @param exception 异常
     * @param request request
     * @return JsonResponse
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public JsonResponse<String> handler(MethodArgumentNotValidException exception, HttpServletRequest request) {
        BindingResult result = exception.getBindingResult();
        ObjectError objectError = result.getAllErrors().stream().findFirst().get();
        log.error("URL: {}, MethodArgumentNotValidException, errorMessage: {}",
                request.getRequestURL(), objectError.getDefaultMessage());
        return JsonResponse.failed(objectError.getDefaultMessage());
    }

    /**
     * RuntimeException
     *
     * @param exception 异常
     * @param request request
     * @return JsonResponse
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = RuntimeException.class)
    public JsonResponse<String> handler(RuntimeException exception, HttpServletRequest request) {
        log.error("URL: {}, RuntimeException, errorMessage: {}", request.getRequestURL(), exception.getMessage());
        return JsonResponse.failed(ERROR_MESSAGE);
    }

    /**
     * IllegalArgumentException
     *
     * @param exception 异常
     * @param request request
     * @return JsonResponse
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public JsonResponse<String> handler(IllegalArgumentException exception, HttpServletRequest request) {
        log.error("URL: {}, IllegalArgumentException, errorMessage: {}",
                request.getRequestURL(), exception.getMessage());
        return JsonResponse.failed(ERROR_MESSAGE);
    }

    /**
     * AccessDeniedException
     *
     * @param exception 异常
     * @param request request
     * @return JsonResponse
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = AccessDeniedException.class)
    public JsonResponse<String> handler(AccessDeniedException exception, HttpServletRequest request) {
        log.error("URL: {}, AccessDeniedException, errorMessage: {}", request.getRequestURL(), exception.getMessage());
        return JsonResponse.failed(ERROR_MESSAGE);
    }

    /**
     * BindException
     *
     * @param exception 异常
     * @param request request
     * @return JsonResponse
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = BindException.class)
    public JsonResponse<String> handler(BindException exception, HttpServletRequest request) {
        log.error("URL: {}, BindException, errorMessage: {}", request.getRequestURL(), exception.getMessage());
        return JsonResponse.failed(ERROR_MESSAGE);
    }

    /**
     * InputException
     *
     * @param exception 异常
     * @param request request
     * @return JsonResponse
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = InputException.class)
    public JsonResponse<String> handler(InputException exception, HttpServletRequest request) {
        log.error("URL: {}, InputException, errorMessage: {}", request.getRequestURL(), exception.getMessage());
        return JsonResponse.failed(ERROR_MESSAGE);
    }

    /**
     * TestReportExceedMaxAmountException
     *
     * @param exception 异常
     * @param request request
     * @return JsonResponse
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = TestReportExceedMaxAmountException.class)
    public JsonResponse<String> handler(TestReportExceedMaxAmountException exception, HttpServletRequest request) {
        log.error("URL: {}, TestReportExceedMaxAmountException, errorMessage: {}",
                request.getRequestURL(), exception.getMessage());
        return JsonResponse.failed(ERROR_MESSAGE);
    }

    /**
     * 捕获http请求方法不支持异常
     *
     * @param exception 异常
     * @return JsonResponse
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public JsonResponse<String> resolveMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error("HttpRequestMethodNotSupportedException: {}, errorMessage: {}", exception.getMessage(), exception);
        return JsonResponse.failed(exception.getMessage());
    }

    /**
     * json数据解析错误
     *
     * @param exception 异常
     * @return JsonResponse
     */
    @ExceptionHandler(value = JSONException.class)
    public JsonResponse<String> resolveJsonException(JSONException exception) {
        log.error("JSONException: {}, errorMessage: {}", exception.getMessage(), exception);
        return JsonResponse.failed("数据解析错误");
    }

    /**
     * 参数校验错误
     *
     * @param exception 异常
     * @return JsonResponse
     */
    @ExceptionHandler(value = ParamException.class)
    public JsonResponse<String> resolveParamException(ParamException exception) {
        log.error("ParamException: {}, errorMessage: {}", exception.getMessage(), exception);
        return JsonResponse.failed(StringUtils.isEmpty(exception.getMessage()) ? "参数错误" : exception.getMessage());
    }

    /**
     * 敏感异常：文件未找到
     *
     * @return JsonResponse
     */
    @ExceptionHandler(value = FileNotFoundException.class)
    public JsonResponse<String> resolveFileNotFoundException() {
        log.error("FileNotFoundException");
        return JsonResponse.failed("文件未找到");
    }

    /**
     * 敏感异常：jar包错误
     *
     * @return JsonResponse
     */
    @ExceptionHandler(value = JarException.class)
    public JsonResponse<String> resolveJarException() {
        log.error("JarException");
        return JsonResponse.failed("服务内部错误");
    }

    /**
     * 敏感异常：MissingResourceException
     *
     * @return JsonResponse
     */
    @ExceptionHandler(value = MissingResourceException.class)
    public JsonResponse<String> resolveMissingResourceException() {
        log.error("MissingResourceException");
        return JsonResponse.failed("服务内部错误");
    }

    /**
     * 敏感异常：NotOwnerException
     *
     * @return JsonResponse
     */
    @ExceptionHandler(value = NotOwnerException.class)
    public JsonResponse<String> resolveNotOwnerException() {
        log.error("NotOwnerException");
        return JsonResponse.failed("服务内部错误");
    }

    /**
     * 敏感异常：ConcurrentModificationException
     *
     * @return JsonResponse
     */
    @ExceptionHandler(value = ConcurrentModificationException.class)
    public JsonResponse<String> resolveConcurrentModificationException() {
        log.error("ConcurrentModificationException");
        return JsonResponse.failed("服务内部错误");
    }

    /**
     * 敏感异常：InsufficientResourcesException
     *
     * @return JsonResponse
     */
    @ExceptionHandler(value = InsufficientResourcesException.class)
    public JsonResponse<String> resolveInsufficientResourcesException() {
        log.error("InsufficientResourcesException");
        return JsonResponse.failed("服务内部错误");
    }

    /**
     * 敏感异常：BindException
     *
     * @return JsonResponse
     */
    @ExceptionHandler(value = java.net.BindException.class)
    public JsonResponse<String> resolveBindException() {
        log.error("BindException");
        return JsonResponse.failed("服务内部错误");
    }

    /**
     * 敏感异常：OutOfMemoryError
     *
     * @return JsonResponse
     */
    @ExceptionHandler(value = OutOfMemoryError.class)
    public JsonResponse<String> resolveOutOfMemoryError() {
        log.error("OutOfMemoryError");
        return JsonResponse.failed("服务内部错误");
    }

    /**
     * 敏感异常：StackOverflowError
     *
     * @return JsonResponse
     */
    @ExceptionHandler(value = StackOverflowError.class)
    public JsonResponse<String> resolveStackOverflowError() {
        log.error("StackOverflowError");
        return JsonResponse.failed("服务内部错误");
    }

    /**
     * 敏感异常：AccessControlException
     *
     * @return JsonResponse
     */
    @ExceptionHandler(value = AccessControlException.class)
    public JsonResponse<String> resolveAccessControlException() {
        log.error("AccessControlException");
        return JsonResponse.failed("服务内部错误");
    }
}
