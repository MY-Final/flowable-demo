package com.my.flowabledemo.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 任务信息VO
 */
@Data
@Schema(description = "任务信息")
public class TaskVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "任务ID")
    private String id;

    @Schema(description = "任务名称")
    private String name;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "任务Key")
    private String taskDefinitionKey;

    @Schema(description = "流程实例ID")
    private String processInstanceId;

    @Schema(description = "流程定义ID")
    private String processDefinitionId;

    @Schema(description = "流程定义Key")
    private String processDefinitionKey;

    @Schema(description = "流程定义名称")
    private String processDefinitionName;

    @Schema(description = "业务Key")
    private String businessKey;

    @Schema(description = "流程标题")
    private String title;

    @Schema(description = "任务执行人")
    private String assignee;

    @Schema(description = "任务执行人姓名")
    private String assigneeName;

    @Schema(description = "任务创建时间")
    private Date createTime;

    @Schema(description = "任务到期时间")
    private Date dueDate;

    @Schema(description = "任务优先级")
    private Integer priority;

    @Schema(description = "是否已签收")
    private boolean claimed;

    @Schema(description = "是否委派任务")
    private boolean delegated;

    @Schema(description = "委派人")
    private String owner;

    @Schema(description = "流程发起人")
    private String startUserId;

    @Schema(description = "流程发起人姓名")
    private String startUserName;

    @Schema(description = "任务变量")
    private Map<String, Object> variables;
}
