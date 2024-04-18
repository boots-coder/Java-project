package com.bootscoder.shopping_manager_api.security;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.rmi.AccessException;

/**
 * 统一异常处理
 *
 * @author bootsCoder
 * @date created on 2024/4/18
 */
@RestControllerAdvice
public class AccessDeniedExceptionHandler {
    // 处理权限不足异常，捕获到异常后再次抛出，交给AccessDeniedHandler处理
    @ExceptionHandler(AccessDeniedException.class)
    public void defaultExceptionHandler(AccessDeniedException e) throws
             AccessDeniedException {
        throw e;
    }
}
