package com.my.flowabledemo.controller;

import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.common.R;
import com.my.flowabledemo.dto.task.CompleteTaskDTO;
import com.my.flowabledemo.dto.task.TaskAssignDTO;
import com.my.flowabledemo.service.TaskService;
import com.my.flowabledemo.vo.task.TaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务管理控制器
 * 
 * 提供待办/已办任务查询、任务审批、委派、转派、签收等功能
 */
@Tag(name = "任务管理", description = "待办/已办任务查询、审批、委派、转派、签收等操作")
@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 获取待办任务列表
     * 
     * 查询分配给当前用户的待办任务
     */
    @Operation(summary = "待办任务列表", description = "查询当前用户的待办任务")
    @GetMapping("/todo")
    public R<PageResponse<TaskVO>> getTodoTasks(
            @Parameter(description = "用户ID") @RequestHeader(value = "X-User-Id", defaultValue = "1") String userId,
            @Parameter(description = "流程定义Key") @RequestParam(value = "processDefinitionKey", required = false) String processDefinitionKey,
            @Parameter(description = "任务名称") @RequestParam(value = "taskName", required = false) String taskName,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResponse<TaskVO> result = taskService.getTodoTasks(userId, processDefinitionKey, taskName, pageNum, pageSize);
        return R.ok(result);
    }

    /**
     * 获取已办任务列表
     * 
     * 查询当前用户已完成的历史任务
     */
    @Operation(summary = "已办任务列表", description = "查询当前用户的已办任务")
    @GetMapping("/done")
    public R<PageResponse<TaskVO>> getDoneTasks(
            @Parameter(description = "用户ID") @RequestHeader(value = "X-User-Id", defaultValue = "1") String userId,
            @Parameter(description = "流程定义Key") @RequestParam(value = "processDefinitionKey", required = false) String processDefinitionKey,
            @Parameter(description = "任务名称") @RequestParam(value = "taskName", required = false) String taskName,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResponse<TaskVO> result = taskService.getDoneTasks(userId, processDefinitionKey, taskName, pageNum, pageSize);
        return R.ok(result);
    }

    /**
     * 获取候选任务列表
     * 
     * 查询用户所在组的候选任务，需要手动签收
     */
    @Operation(summary = "候选任务列表", description = "查询用户所在组的候选任务")
    @GetMapping("/candidate")
    public R<PageResponse<TaskVO>> getCandidateTasks(
            @Parameter(description = "用户ID") @RequestHeader(value = "X-User-Id", defaultValue = "1") String userId,
            @Parameter(description = "用户组") @RequestParam(value = "groups", required = false) List<String> groups,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageResponse<TaskVO> result = taskService.getCandidateTasks(userId, groups, pageNum, pageSize);
        return R.ok(result);
    }

    /**
     * 获取任务详情
     */
    @Operation(summary = "任务详情", description = "根据ID获取任务详情")
    @GetMapping("/{id}")
    public R<TaskVO> getTask(
            @Parameter(description = "任务ID") @PathVariable("id") String id) {
        TaskVO task = taskService.getTask(id);
        return R.ok(task);
    }

    /**
     * 完成任务（审批）
     * 
     * 执行审批操作，支持通过/拒绝，并可添加审批意见
     */
    @Operation(summary = "完成任务", description = "执行任务审批（通过/拒绝）")
    @PostMapping("/{id}/complete")
    public R<Void> completeTask(
            @Parameter(description = "任务ID") @PathVariable("id") String id,
            @Valid @RequestBody CompleteTaskDTO dto,
            @Parameter(description = "操作人ID") @RequestHeader(value = "X-User-Id", defaultValue = "1") String userId,
            @Parameter(description = "操作人姓名") @RequestHeader(value = "X-User-Name", defaultValue = "管理员") String userName) {
        taskService.completeTask(id, dto, userId, userName);
        return R.ok();
    }

    /**
     * 委派任务
     * 
     * 委派后，任务执行人变为被委派人，原执行人变为owner
     * 被委派人完成任务后，任务会自动回到原执行人
     */
    @Operation(summary = "委派任务", description = "将任务委派给其他人处理")
    @PostMapping("/{id}/delegate")
    public R<Void> delegateTask(
            @Parameter(description = "任务ID") @PathVariable("id") String id,
            @Valid @RequestBody TaskAssignDTO dto) {
        taskService.delegateTask(id, dto);
        return R.ok();
    }

    /**
     * 转派任务
     * 
     * 转派后，任务执行人直接变为目标用户，不再回到原执行人
     */
    @Operation(summary = "转派任务", description = "将任务转派给其他人")
    @PostMapping("/{id}/transfer")
    public R<Void> transferTask(
            @Parameter(description = "任务ID") @PathVariable("id") String id,
            @Valid @RequestBody TaskAssignDTO dto) {
        taskService.transferTask(id, dto);
        return R.ok();
    }

    /**
     * 签收任务
     * 
     * 将候选任务签收为自己的任务
     */
    @Operation(summary = "签收任务", description = "将候选任务签收为自己的任务")
    @PostMapping("/{id}/claim")
    public R<Void> claimTask(
            @Parameter(description = "任务ID") @PathVariable("id") String id,
            @Parameter(description = "签收人ID") @RequestHeader(value = "X-User-Id", defaultValue = "1") String userId) {
        taskService.claimTask(id, userId);
        return R.ok();
    }

    /**
     * 取消签收
     * 
     * 将已签收的任务退回到候选状态
     */
    @Operation(summary = "取消签收", description = "将任务退回到候选状态")
    @PostMapping("/{id}/unclaim")
    public R<Void> unclaimTask(
            @Parameter(description = "任务ID") @PathVariable("id") String id) {
        taskService.unclaimTask(id);
        return R.ok();
    }
}
