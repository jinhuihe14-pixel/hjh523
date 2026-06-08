package com.arttraining.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    LOGIN_ERROR(1001, "用户名或密码错误"),
    TOKEN_EXPIRED(1002, "Token已过期"),
    TOKEN_INVALID(1003, "Token无效"),
    ACCOUNT_DISABLED(1004, "账号已禁用"),
    ACCOUNT_LOCKED(1005, "账号已锁定"),

    USER_NOT_EXIST(1101, "用户不存在"),
    USER_ALREADY_EXIST(1102, "用户已存在"),
    USERNAME_ALREADY_EXIST(1103, "用户名已存在"),
    PHONE_ALREADY_EXIST(1104, "手机号已存在"),
    EMAIL_ALREADY_EXIST(1105, "邮箱已存在"),

    ROLE_NOT_EXIST(1201, "角色不存在"),
    ROLE_ALREADY_EXIST(1202, "角色已存在"),
    ROLE_CODE_ALREADY_EXIST(1203, "角色编码已存在"),
    ROLE_CANNOT_DELETE(1204, "角色不能删除"),

    PERMISSION_NOT_EXIST(1301, "权限不存在"),
    PERMISSION_ALREADY_EXIST(1302, "权限已存在"),
    PERMISSION_CODE_ALREADY_EXIST(1303, "权限编码已存在"),

    CAMPUS_NOT_EXIST(1401, "校区不存在"),
    CAMPUS_ALREADY_EXIST(1402, "校区已存在"),
    CAMPUS_CODE_ALREADY_EXIST(1403, "校区编码已存在"),

    DATA_NOT_EXIST(1501, "数据不存在"),
    DATA_ALREADY_EXIST(1502, "数据已存在"),
    DATA_CANNOT_DELETE(1503, "数据不能删除"),
    DATA_CANNOT_MODIFY(1504, "数据不能修改"),

    RESOURCE_CONFLICT(1601, "资源冲突"),
    RESOURCE_OCCUPIED(1602, "资源已被占用"),

    FINANCIAL_LOCKED(1701, "财务单据已锁定，不能修改"),
    FINANCIAL_APPROVING(1702, "财务单据审批中，不能修改"),

    BUSINESS_ERROR(1801, "业务异常"),
    SYSTEM_ERROR(1901, "系统异常");

    private final Integer code;
    private final String message;
}
