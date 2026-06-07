package com.my.flowabledemo.controller;

import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.common.R;
import com.my.flowabledemo.service.ProcessDefinitionService;
import com.my.flowabledemo.vo.process.DeploymentVO;
import com.my.flowabledemo.vo.process.ProcessDefinitionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 流程定义管理控制器
 * 
 * 提供流程定义的部署、查询、挂起、激活等管理功能
 */
@Tag(name = "流程定义管理", description = "流程定义的部署、查询、挂起、激活等操作")
@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessDefinitionController {

    private final ProcessDefinitionService processDefinitionService;

    /**
     * 部署流程定义（文件上传方式）
     * 
     * 支持 .bpmn、.bpmn20.xml、.zip 格式的文件上传
     */
    @Operation(summary = "部署流程（文件上传）", description = "上传BPMN文件部署流程定义")
    @PostMapping("/deploy")
    public R<DeploymentVO> deploy(
            @Parameter(description = "BPMN文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "部署名称") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "租户ID") @RequestParam(value = "tenantId", required = false) String tenantId) {
        DeploymentVO deployment = processDefinitionService.deploy(file, name, tenantId);
        return R.ok(deployment);
    }

    /**
     * 部署流程定义（XML字符串方式）
     * 
     * 直接传入BPMN XML内容进行部署
     */
    @Operation(summary = "部署流程（XML方式）", description = "通过XML字符串部署流程定义")
    @PostMapping("/deploy/xml")
    public R<DeploymentVO> deployByXml(
            @Parameter(description = "流程名称") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "租户ID") @RequestParam(value = "tenantId", required = false) String tenantId,
            @Parameter(description = "BPMN XML内容") @RequestBody String xml) {
        DeploymentVO deployment = processDefinitionService.deployByXml(xml, name, tenantId);
        return R.ok(deployment);
    }

    /**
     * 获取流程定义列表（分页）
     * 
     * 支持按名称模糊查询和按Key精确查询
     */
    @Operation(summary = "流程定义列表", description = "分页查询流程定义列表")
    @GetMapping("/definitions")
    public R<PageResponse<ProcessDefinitionVO>> getDefinitions(
            @Parameter(description = "流程名称（模糊查询）") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "流程Key（精确查询）") @RequestParam(value = "key", required = false) String key,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResponse<ProcessDefinitionVO> result = processDefinitionService.getDefinitions(name, key, pageNum, pageSize);
        return R.ok(result);
    }

    /**
     * 获取最新版本的流程定义列表
     * 
     * 返回每个流程Key的最新版本
     */
    @Operation(summary = "最新流程定义列表", description = "获取每个流程Key的最新版本")
    @GetMapping("/definitions/latest")
    public R<List<ProcessDefinitionVO>> getLatestDefinitions() {
        List<ProcessDefinitionVO> list = processDefinitionService.getLatestDefinitions();
        return R.ok(list);
    }

    /**
     * 获取流程定义详情
     */
    @Operation(summary = "流程定义详情", description = "根据ID获取流程定义详情")
    @GetMapping("/definition/{id}")
    public R<ProcessDefinitionVO> getDefinition(
            @Parameter(description = "流程定义ID") @PathVariable("id") String id) {
        ProcessDefinitionVO definition = processDefinitionService.getDefinition(id);
        return R.ok(definition);
    }

    /**
     * 获取流程定义的BPMN XML
     * 
     * 返回原始的BPMN XML内容，可用于前端流程设计器回显
     */
    @Operation(summary = "获取流程XML", description = "获取流程定义的BPMN XML内容")
    @GetMapping("/definition/{id}/xml")
    public R<String> getDefinitionXml(
            @Parameter(description = "流程定义ID") @PathVariable("id") String id) {
        String xml = processDefinitionService.getDefinitionXml(id);
        return R.ok(xml);
    }

    /**
     * 删除部署
     * 
     * cascade=false：如果有运行中的流程实例会报错
     * cascade=true：级联删除，删除所有关联的流程实例和历史
     */
    @Operation(summary = "删除部署", description = "删除流程部署")
    @DeleteMapping("/deployment/{id}")
    public R<Void> deleteDeployment(
            @Parameter(description = "部署ID") @PathVariable("id") String id,
            @Parameter(description = "是否级联删除") @RequestParam(value = "cascade", defaultValue = "false") boolean cascade) {
        processDefinitionService.deleteDeployment(id, cascade);
        return R.ok();
    }

    /**
     * 挂起流程定义
     * 
     * 挂起后，该流程定义将无法启动新的流程实例
     */
    @Operation(summary = "挂起流程定义", description = "挂起流程定义，无法启动新实例")
    @PutMapping("/definition/{id}/suspend")
    public R<Void> suspendDefinition(
            @Parameter(description = "流程定义ID") @PathVariable("id") String id) {
        processDefinitionService.suspendDefinition(id);
        return R.ok();
    }

    /**
     * 激活流程定义
     * 
     * 激活后，可以正常启动新的流程实例
     */
    @Operation(summary = "激活流程定义", description = "激活流程定义，恢复启动新实例")
    @PutMapping("/definition/{id}/activate")
    public R<Void> activateDefinition(
            @Parameter(description = "流程定义ID") @PathVariable("id") String id) {
        processDefinitionService.activateDefinition(id);
        return R.ok();
    }
}
