package com.bootscoder.shopping_common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回状态码枚举类
 */
@Getter
@AllArgsConstructor
public enum CodeEnum {
    // 正常
    SUCCESS(200,"OK"),
    // 系统异常
    SYSTEM_ERROR(500,"系统异常"),
    // 业务异常
    PARAMETER_ERROR(601,"参数异常"),
    // 添加商品类型异常
    INSERT_PRODUCT_TYPE_ERROR(602,"3级商品类型不能添加子类型"),
    DELETE_PRODUCT_TYPE_ERROR(603,"该类型有子类型，禁止删除"),
    UPLOAD_FILE_ERROR(604,"文件上传异常"),
    REGISTER_CODE_ERROR(605,"注册验证码错误"),
    REGISTER_REPEAT_PHONE_ERROR(606,"注册手机号重复"),
    REGISTER_REPEAT_NAME_ERROR(607,"注册用户名重复"),
    LOGIN_NAME_PASSWORD_ERROR(608,"用户名或密码错误"),
    LOGIN_CODE_ERROR(609,"验证码错误"),
    LOGIN_NOPHONE_ERROR(610,"该手机号没有注册"),
    LOGIN_USER_STATUS_ERROR(611,"该用户状态异常"),
    QR_CODE_ERROR(612,"二维码生成异常"),
    CHECK_SIGN_ERROR(613,"支付宝验签异常"),
    NO_STOCK_ERROR(614,"商品库存不足"),
    ORDER_EXPIRED_ERROR(615,"订单不存在或订单超时"),
    ORDER_STATUS_ERROR(616,"订单状态异常")
    ;

    private final Integer code;
    private final String message;
}
