package com.my.flowabledemo.vo.process;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程定义响应VO
 */
@Data
@Schema(description = "流程定义信息")
public class ProcessDefinitionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "流程定义ID")
    private String id;

    @Schema(description = "流程定义Key")
    private String key;

    @Schema(description = "流程定义名称")
    private String name;

    @Schema(description = "流程定义版本")
    private Integer version;

    @Schema(description = "部署ID")
    private String deploymentId;

    @Schema(description = "资源名称（BPMN文件名）")
    private String resourceName;

    @Schema(description = "流程描述")
    private String description;

    @Schema(description = "是否已挂起")
    private boolean suspended;

    @Schema(description = "部署时间")
    private Date deploymentTime;
}
