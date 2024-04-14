package com.bootscoder.shopping_common.result;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 处理业务异常
    @ExceptionHandler(BusException.class)
    public BaseResult defaultExceptionHandler(BusException e){
        BaseResult baseResult = new BaseResult(e.getCode(), e.getMsg(), null);
        return baseResult;
    }

    // 处理系统异常
    @ExceptionHandler(Exception.class)
    public BaseResult defaultExceptionHandler(Exception e){
        e.printStackTrace();
        BaseResult baseResult = new BaseResult(CodeEnum.SYSTEM_ERROR.getCode(), CodeEnum.SYSTEM_ERROR.getMessage(), null);
        return baseResult;
    }
}
