package com.my.flowabledemo.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // 服务端错误
    FAILURE(500, "操作失败"),
    INTERNAL_ERROR(500, "系统内部错误"),

    // 业务错误 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "用户已禁用"),
    DATA_NOT_FOUND(1004, "数据不存在"),
    DATA_ALREADY_EXISTS(1005, "数据已存在"),
    OPERATION_FAILED(1006, "操作失败"),

    // 流程错误 2xxx
    PROCESS_DEFINITION_NOT_FOUND(2001, "流程定义不存在"),
    PROCESS_INSTANCE_NOT_FOUND(2002, "流程实例不存在"),
    PROCESS_START_FAILED(2003, "流程启动失败"),
    TASK_NOT_FOUND(2004, "任务不存在"),
    TASK_ALREADY_COMPLETED(2005, "任务已完成"),
    TASK_ASSIGN_FAILED(2006, "任务分配失败"),
    BPMN_PARSE_ERROR(2007, "BPMN解析错误"),
    DEPLOY_FAILED(2008, "流程部署失败"),

    // 表单错误 3xxx
    FORM_NOT_FOUND(3001, "表单不存在"),
    FORM_VALID_ERROR(3002, "表单验证失败"),
    FORM_BIND_EXISTS(3003, "表单已绑定流程");

    /** 状态码 */
    private final int code;

    /** 消息 */
    private final String message;
}