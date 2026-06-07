package com.my.flowabledemo.vo.process;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程部署响应VO
 */
@Data
@Schema(description = "流程部署信息")
public class DeploymentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "部署ID")
    private String id;

    @Schema(description = "部署名称")
    private String name;

    @Schema(description = "部署时间")
    private Date deploymentTime;

    @Schema(description = "租户ID")
    private String tenantId;
}
