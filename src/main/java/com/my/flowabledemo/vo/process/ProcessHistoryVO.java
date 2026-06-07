package com.my.flowabledemo.vo.process;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程历史记录VO
 */
@Data
@Schema(description = "流程历史记录")
public class ProcessHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "历史记录ID")
    private String id;

    @Schema(description = "流程实例ID")
    private String processInstanceId;

    @Schema(description = "活动ID")
    private String activityId;

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "活动类型（startEvent/userTask/endEvent等）")
    private String activityType;

    @Schema(description = "任务执行人")
    private String assignee;

    @Schema(description = "任务执行人姓名")
    private String assigneeName;

    @Schema(description = "开始时间")
    private Date startTime;

    @Schema(description = "结束时间")
    private Date endTime;

    @Schema(description = "持续时间（毫秒）")
    private Long durationInMillis;

    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "删除原因")
    private String deleteReason;
}
