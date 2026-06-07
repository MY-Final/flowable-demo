package com.my.flowabledemo.dto.process;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 启动流程实例请求DTO
 */
@Data
@Schema(description = "启动流程实例请求")
public class StartProcessDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "流程定义Key不能为空")
    @Schema(description = "流程定义Key", required = true)
    private String processDefinitionKey;

    @Schema(description = "业务Key（用于关联业务数据）")
    private String businessKey;

    @Schema(description = "流程标题")
    private String title;

    @Schema(description = "流程变量")
    private Map<String, Object> variables;
}
