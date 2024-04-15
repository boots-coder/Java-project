package com.bootscoder.shopping_common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态码
 *
 * @author bootsCoder
 * @date created on 2024/4/15
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    // 正常
    SUCCESS(200, "OK")
    ,
    // 系统异常
    SYSTEM_ERROR(500, "系统异常"),
    // 参数异常
    PARAMETER_ERROR(601, "参数异常")
    ;
    private final Integer code;
    private final String message;
}
