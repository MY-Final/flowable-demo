package com.my.flowabledemo.controller;

import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.common.R;
import com.my.flowabledemo.dto.form.FormBindDTO;
import com.my.flowabledemo.dto.form.FormDefinitionDTO;
import com.my.flowabledemo.service.FormService;
import com.my.flowabledemo.vo.form.FormDefinitionVO;
import com.my.flowabledemo.vo.form.ProcessFormConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 表单配置管理控制器
 * 
 * 提供表单定义的CRUD操作，以及表单与流程节点的绑定管理
 */
@Tag(name = "表单配置管理", description = "表单定义的增删改查，以及表单与流程节点的绑定")
@RestController
@RequestMapping("/api/form")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    /**
     * 创建表单定义
     * 
     * 创建一个新的表单定义，表单内容以JSON Schema格式存储
     */
    @Operation(summary = "创建表单", description = "创建新的表单定义")
    @PostMapping
    public R<FormDefinitionVO> createForm(
            @Valid @RequestBody FormDefinitionDTO dto,
            @Parameter(description = "创建人ID") @RequestHeader(value = "X-User-Id", defaultValue = "1") Long createBy) {
        FormDefinitionVO form = formService.createForm(dto, createBy);
        return R.ok(form);
    }

    /**
     * 更新表单定义
     * 
     * 更新已有的表单定义内容
     */
    @Operation(summary = "更新表单", description = "更新表单定义")
    @PutMapping("/{id}")
    public R<FormDefinitionVO> updateForm(
            @Parameter(description = "表单ID") @PathVariable("id") Long id,
            @Valid @RequestBody FormDefinitionDTO dto) {
        FormDefinitionVO form = formService.updateForm(id, dto);
        return R.ok(form);
    }

    /**
     * 获取表单定义详情
     */
    @Operation(summary = "表单详情", description = "根据ID获取表单定义详情")
    @GetMapping("/{id}")
    public R<FormDefinitionVO> getForm(
            @Parameter(description = "表单ID") @PathVariable("id") Long id) {
        FormDefinitionVO form = formService.getForm(id);
        return R.ok(form);
    }

    /**
     * 获取表单定义列表
     * 
     * 支持按表单标识和名称筛选
     */
    @Operation(summary = "表单列表", description = "分页查询表单定义列表")
    @GetMapping("/list")
    public R<PageResponse<FormDefinitionVO>> getForms(
            @Parameter(description = "表单标识") @RequestParam(value = "formKey", required = false) String formKey,
            @Parameter(description = "表单名称") @RequestParam(value = "formName", required = false) String formName,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResponse<FormDefinitionVO> result = formService.getForms(formKey, formName, pageNum, pageSize);
        return R.ok(result);
    }

    /**
     * 删除表单定义
     * 
     * 如果表单已绑定流程节点，需要先解绑才能删除
     */
    @Operation(summary = "删除表单", description = "删除表单定义")
    @DeleteMapping("/{id}")
    public R<Void> deleteForm(
            @Parameter(description = "表单ID") @PathVariable("id") Long id) {
        formService.deleteForm(id);
        return R.ok();
    }

    /**
     * 绑定表单到流程节点
     * 
     * 将表单绑定到指定流程的指定节点，节点ID为"start"表示启动表单
     */
    @Operation(summary = "绑定表单", description = "将表单绑定到流程节点")
    @PostMapping("/bind")
    public R<Void> bindForm(@Valid @RequestBody FormBindDTO dto) {
        formService.bindForm(dto);
        return R.ok();
    }

    /**
     * 解绑流程节点的表单
     */
    @Operation(summary = "解绑表单", description = "解绑流程节点的表单")
    @DeleteMapping("/unbind")
    public R<Void> unbindForm(
            @Parameter(description = "流程定义Key") @RequestParam("processDefKey") String processDefKey,
            @Parameter(description = "节点ID") @RequestParam("nodeId") String nodeId) {
        formService.unbindForm(processDefKey, nodeId);
        return R.ok();
    }

    /**
     * 获取流程关联的所有表单配置
     * 
     * 返回流程的启动表单和各节点表单配置
     */
    @Operation(summary = "流程表单配置", description = "获取流程关联的所有表单配置")
    @GetMapping("/process/{processDefKey}")
    public R<ProcessFormConfigVO> getProcessFormConfig(
            @Parameter(description = "流程定义Key") @PathVariable("processDefKey") String processDefKey) {
        ProcessFormConfigVO config = formService.getProcessFormConfig(processDefKey);
        return R.ok(config);
    }

    /**
     * 获取指定节点的表单
     */
    @Operation(summary = "节点表单", description = "获取流程指定节点的表单")
    @GetMapping("/process/{processDefKey}/node/{nodeId}")
    public R<FormDefinitionVO> getNodeForm(
            @Parameter(description = "流程定义Key") @PathVariable("processDefKey") String processDefKey,
            @Parameter(description = "节点ID") @PathVariable("nodeId") String nodeId) {
        FormDefinitionVO form = formService.getNodeForm(processDefKey, nodeId);
        return R.ok(form);
    }
}
