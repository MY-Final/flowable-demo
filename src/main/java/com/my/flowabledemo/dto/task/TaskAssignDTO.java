package com.my.flowabledemo.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务转派/委派请求DTO
 */
@Data
@Schema(description = "任务转派/委派请求")
public class TaskAssignDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "目标用户ID不能为空")
    @Schema(description = "目标用户ID", required = true)
    private String userId;

    @NotBlank(message = "目标用户名不能为空")
    @Schema(description = "目标用户名", required = true)
    private String userName;

    @Schema(description = "原因/备注")
    private String reason;
}
