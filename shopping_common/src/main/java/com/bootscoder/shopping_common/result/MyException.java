package com.bootscoder.shopping_common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 自定义异常
 *
 * @author bootsCoder
 * @date created on 2024/4/15
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MyException extends RuntimeException {
    // 状态码
    private Integer code;
    // 错误消息
    private String msg;

    public MyException(ResultCode resultCode){
        this.code = resultCode.getCode();
        this.msg = resultCode.getMessage();
    }
}