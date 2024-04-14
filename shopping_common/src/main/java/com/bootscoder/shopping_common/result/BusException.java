package com.bootscoder.shopping_common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义业务异常
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusException extends RuntimeException {
    // 状态码
    private Integer code;
    // 错误消息
    private String msg;

    public BusException(CodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMessage();
    }

}
