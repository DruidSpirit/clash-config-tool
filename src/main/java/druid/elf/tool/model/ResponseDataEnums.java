package druid.elf.tool.model;

import lombok.AllArgsConstructor;

@SuppressWarnings("unused")
@AllArgsConstructor
public enum ResponseDataEnums implements  ResponseInterface {

    RESPONSE_SYSTEM_ERROR(1000, "系统内部异常"),

    RESPONSE_SUCCESS(2000,"数据响应成功"),
    RESPONSE_SUCCESS_LOGIN(2001,"登入成功"),
    RESPONSE_SUCCESS_REGISTER(2002,"注册成功"),

    RESPONSE_FAIL_AUTHENTICATION(3001,"认证异常"),
    RESPONSE_FAIL_USERNAME_OR_PASSWORD_NOTFOUND(3002,"用户名或者密码输入错误，请重新输入"),
    RESPONSE_FAIL_DISABLED(3003,"账户被禁用，请联系管理员"),

    RESPONSE_FAIL_PARAMS(3004,"参数响应失败"),
    RESPONSE_FAIL_HANDLE(3005,"操作过于频繁"),
    RESPONSE_FAIL_PARAMS_FORMAT(3006,"参数格式不正确"),
    RESPONSE_FAIL_DENIED(3007,"拒绝访问"),
    RESPONSE_FAIL_NO_PERMISSION(3008,"登入账号无权限"),

    RESPONSE_FAIL_NOT_LOGIN(3009,"用户未登入"),
    RESPONSE_FAIL_FREQUENTLY_VISIT(3010,"访问过于频繁，IP已被锁定"),
    RESPONSE_FAIL_NOT_BIND_WECHAT(3011,"微信用户未绑定"),
    RESPONSE_FAIL_INVALID_TOKEN(3012,"无效的token"),
    RESPONSE_FAIL_CHECK_TOKEN(3013,"token校验不通过"),

    RESPONSE_FAIL(5000,"数据响应失败"),
    ;


    public final Integer statusCode;
    public final String name;

    @Override
    public Integer getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
