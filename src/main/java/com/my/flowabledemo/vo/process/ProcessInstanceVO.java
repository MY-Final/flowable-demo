package com.my.flowabledemo.vo.process;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 流程实例响应VO
 */
@Data
@Schema(description = "流程实例信息")
public class ProcessInstanceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "流程实例ID")
    private String id;

    @Schema(description = "流程定义ID")
    private String processDefinitionId;

    @Schema(description = "流程定义Key")
    private String processDefinitionKey;

    @Schema(description = "流程定义名称")
    private String processDefinitionName;

    @Schema(description = "流程定义版本")
    private Integer processDefinitionVersion;

    @Schema(description = "业务Key")
    private String businessKey;

    @Schema(description = "流程标题")
    private String title;

    @Schema(description = "是否已结束")
    private boolean ended;

    @Schema(description = "是否已挂起")
    private boolean suspended;

    @Schema(description = "发起人ID")
    private String startUserId;

    @Schema(description = "发起人姓名")
    private String startUserName;

    @Schema(description = "启动时间")
    private Date startTime;

    @Schema(description = "结束时间")
    private Date endTime;

    @Schema(description = "当前活动节点")
    private String currentActivityId;

    @Schema(description = "当前活动名称")
    private String currentActivityName;

    @Schema(description = "流程变量")
    private Map<String, Object> variables;
}
