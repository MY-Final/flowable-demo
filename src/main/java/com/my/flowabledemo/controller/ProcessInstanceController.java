package com.my.flowabledemo.controller;

import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.common.R;
import com.my.flowabledemo.dto.process.StartProcessDTO;
import com.my.flowabledemo.service.ProcessInstanceService;
import com.my.flowabledemo.vo.process.ProcessHistoryVO;
import com.my.flowabledemo.vo.process.ProcessInstanceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程实例管理控制器
 * 
 * 提供流程实例的启动、查询、挂起、激活、删除等管理功能
 */
@Tag(name = "流程实例管理", description = "流程实例的启动、查询、挂起、激活、删除等操作")
@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessInstanceController {

    private final ProcessInstanceService processInstanceService;

    /**
     * 启动流程实例
     * 
     * 根据流程定义Key启动一个新的流程实例
     * 流程变量中会自动添加发起人信息（initiator, initiatorName）
     */
    @Operation(summary = "启动流程实例", description = "根据流程定义Key启动新的流程实例")
    @PostMapping("/instance/start")
    public R<ProcessInstanceVO> startProcess(
            @Valid @RequestBody StartProcessDTO dto,
            @Parameter(description = "发起人ID") @RequestHeader(value = "X-User-Id", defaultValue = "1") String userId,
            @Parameter(description = "发起人姓名") @RequestHeader(value = "X-User-Name", defaultValue = "管理员") String userName) {
        ProcessInstanceVO instance = processInstanceService.startProcess(dto, userId, userName);
        return R.ok(instance);
    }

    /**
     * 获取流程实例列表（分页）
     * 
     * 支持按流程定义Key、业务Key、发起人、状态等条件筛选
     */
    @Operation(summary = "流程实例列表", description = "分页查询流程实例列表")
    @GetMapping("/instances")
    public R<PageResponse<ProcessInstanceVO>> getInstances(
            @Parameter(description = "流程定义Key") @RequestParam(value = "processDefinitionKey", required = false) String processDefinitionKey,
            @Parameter(description = "业务Key") @RequestParam(value = "businessKey", required = false) String businessKey,
            @Parameter(description = "发起人") @RequestParam(value = "startedBy", required = false) String startedBy,
            @Parameter(description = "是否挂起") @RequestParam(value = "suspended", required = false) Boolean suspended,
            @Parameter(description = "是否已结束") @RequestParam(value = "finished", required = false) Boolean finished,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResponse<ProcessInstanceVO> result = processInstanceService.getInstances(
                processDefinitionKey, businessKey, startedBy, suspended, finished, pageNum, pageSize);
        return R.ok(result);
    }

    /**
     * 获取流程实例详情
     * 
     * 返回流程实例的详细信息，包括当前状态、活动节点等
     */
    @Operation(summary = "流程实例详情", description = "根据ID获取流程实例详情")
    @GetMapping("/instance/{id}")
    public R<ProcessInstanceVO> getInstance(
            @Parameter(description = "流程实例ID") @PathVariable("id") String id) {
        ProcessInstanceVO instance = processInstanceService.getInstance(id);
        return R.ok(instance);
    }

    /**
     * 删除/终止流程实例
     * 
     * 终止正在运行的流程实例，可指定删除原因
     */
    @Operation(summary = "删除流程实例", description = "终止并删除流程实例")
    @DeleteMapping("/instance/{id}")
    public R<Void> deleteInstance(
            @Parameter(description = "流程实例ID") @PathVariable("id") String id,
            @Parameter(description = "删除原因") @RequestParam(value = "reason", required = false) String reason) {
        processInstanceService.deleteInstance(id, reason);
        return R.ok();
    }

    /**
     * 挂起流程实例
     * 
     * 挂起后，流程实例将暂停执行，无法进行任务操作
     */
    @Operation(summary = "挂起流程实例", description = "挂起流程实例，暂停执行")
    @PutMapping("/instance/{id}/suspend")
    public R<Void> suspendInstance(
            @Parameter(description = "流程实例ID") @PathVariable("id") String id) {
        processInstanceService.suspendInstance(id);
        return R.ok();
    }

    /**
     * 激活流程实例
     * 
     * 激活后，流程实例恢复正常执行
     */
    @Operation(summary = "激活流程实例", description = "激活流程实例，恢复执行")
    @PutMapping("/instance/{id}/activate")
    public R<Void> activateInstance(
            @Parameter(description = "流程实例ID") @PathVariable("id") String id) {
        processInstanceService.activateInstance(id);
        return R.ok();
    }

    /**
     * 获取流程执行历史
     * 
     * 返回流程实例的所有历史活动记录，包括开始事件、用户任务、网关等
     */
    @Operation(summary = "流程执行历史", description = "获取流程实例的历史活动记录")
    @GetMapping("/instance/{id}/history")
    public R<List<ProcessHistoryVO>> getHistory(
            @Parameter(description = "流程实例ID") @PathVariable("id") String id) {
        List<ProcessHistoryVO> history = processInstanceService.getHistory(id);
        return R.ok(history);
    }
}
