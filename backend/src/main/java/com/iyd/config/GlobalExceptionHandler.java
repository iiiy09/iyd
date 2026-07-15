package com.iyd.config;

import com.iyd.common.R;
import com.iyd.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingRequestHeaderException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public R<?> handleMissingHeader(MissingRequestHeaderException e) {
        log.error("MissingRequestHeader: {}", e.getMessage());
        return R.fail(401, "请先登录");
    }

    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        log.error("Exception: ", e);
        return R.fail("系统繁忙，请稍后重试");
    }
}