package com.bootscoder.shopping_common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 全局统一结果配置
 *
 * @author bootsCoder
 * @date created on 2024/4/15
 */
@Data
@AllArgsConstructor
public class BaseResult<T> {
    // 状态码(成功:200 失败:其他)
    private Integer code;
    // 提示消息
    private String message;
    // 返回数据
    private T data;


    // 构建成功结果
    public static <T> BaseResult<T> ok() {
        return new BaseResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }


    // 构建带有数据的成功结果
    public static <T> BaseResult<T> ok(T data) {
        return new BaseResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }
}




