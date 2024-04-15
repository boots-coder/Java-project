package com.bootscoder.shopping_common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Demo class
 *
 * @author bootsCoder
 * @date created on 2024/4/15
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    // 正常
    SUCCESS(200, "OK")
    ;

    private final Integer code;
    private final String message;
}
