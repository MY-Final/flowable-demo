package com.my.flowabledemo.dto.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 表单定义请求DTO
 */
@Data
@Schema(description = "表单定义请求")
public class FormDefinitionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "表单标识不能为空")
    @Schema(description = "表单标识", required = true)
    private String formKey;

    @NotBlank(message = "表单名称不能为空")
    @Schema(description = "表单名称", required = true)
    private String formName;

    @NotBlank(message = "表单JSON不能为空")
    @Schema(description = "表单JSON Schema", required = true)
    private String formJson;

    @Schema(description = "表单描述")
    private String remark;
}
