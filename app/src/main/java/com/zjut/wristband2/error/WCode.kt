package com.zjut.wristband2.error

enum class WCode(val num: Int, val error: String) {
    OK(0, "成功"),
    UnDefinedError(5, "未定义错误"),
    NetworkError(9, "网络连接异常"),
    JsonParseError(11, "Json解析错误"),
    ServerError(15, "服务器异常"),
    AccountError(19, "用户名或密码错误"),
    PasswordError(20, "密码错误"),
    TokenInvalidError(21, "Token失效，请退出重新登录"),
    SendError(25, "验证码发送失败"),
    EmailNotFoundError(26, "邮箱不存在，请联系管理员"),
    ResetError(27, "重置失败"),
    VerifyCodeError(28, "验证码错误"),
    DatabaseError(29, "数据库错误")
}