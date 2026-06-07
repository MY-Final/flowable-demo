package com.my.flowabledemo.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 完成任务请求DTO
 */
@Data
@Schema(description = "完成任务请求")
public class CompleteTaskDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "审批意见")
    private String opinion;

    @NotBlank(message = "审批状态不能为空")
    @Schema(description = "审批状态（agree/reject）", required = true)
    private String status;

    @Schema(description = "任务变量")
    private Map<String, Object> variables;
}
