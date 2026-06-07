package com.my.flowabledemo.vo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 流程表单配置VO（包含启动表单和各节点表单）
 */
@Data
@Schema(description = "流程表单配置")
public class ProcessFormConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "流程定义Key")
    private String processDefKey;

    @Schema(description = "流程定义名称")
    private String processDefName;

    @Schema(description = "启动表单")
    private FormDefinitionVO startForm;

    @Schema(description = "节点表单列表")
    private List<NodeFormVO> nodeForms;

    /**
     * 节点表单信息
     */
    @Data
    @Schema(description = "节点表单信息")
    public static class NodeFormVO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "节点ID")
        private String nodeId;

        @Schema(description = "节点名称")
        private String nodeName;

        @Schema(description = "表单ID")
        private Long formId;

        @Schema(description = "表单标识")
        private String formKey;

        @Schema(description = "表单名称")
        private String formName;

        @Schema(description = "表单JSON Schema")
        private String formJson;
    }
}
