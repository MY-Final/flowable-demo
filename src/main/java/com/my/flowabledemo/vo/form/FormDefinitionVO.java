package com.my.flowabledemo.vo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 表单定义响应VO
 */
@Data
@Schema(description = "表单定义信息")
public class FormDefinitionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "表单ID")
    private Long id;

    @Schema(description = "表单标识")
    private String formKey;

    @Schema(description = "表单名称")
    private String formName;

    @Schema(description = "表单JSON Schema")
    private String formJson;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "状态（1正常 0停用）")
    private Integer status;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建人姓名")
    private String createByName;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;
}
