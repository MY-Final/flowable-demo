package com.my.flowabledemo.dto.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 表单绑定请求DTO
 */
@Data
@Schema(description = "表单绑定请求")
public class FormBindDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "流程定义Key不能为空")
    @Schema(description = "流程定义Key", required = true)
    private String processDefKey;

    @NotBlank(message = "节点ID不能为空")
    @Schema(description = "节点ID（start表示启动表单）", required = true)
    private String nodeId;

    @Schema(description = "节点名称")
    private String nodeName;

    @NotNull(message = "表单ID不能为空")
    @Schema(description = "表单ID", required = true)
    private Long formId;
}
