package com.my.flowabledemo.service;

import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.dto.task.CompleteTaskDTO;
import com.my.flowabledemo.dto.task.TaskAssignDTO;
import com.my.flowabledemo.vo.task.TaskVO;

import java.util.List;

/**
 * 任务管理服务接口
 */
public interface TaskService {

    /**
     * 获取待办任务列表（分页）
     *
     * @param userId 用户ID
     * @param processDefinitionKey 流程定义Key（可选）
     * @param taskName 任务名称（可选，模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResponse<TaskVO> getTodoTasks(String userId, String processDefinitionKey, String taskName,
                                       int pageNum, int pageSize);

    /**
     * 获取已办任务列表（分页）
     *
     * @param userId 用户ID
     * @param processDefinitionKey 流程定义Key（可选）
     * @param taskName 任务名称（可选，模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResponse<TaskVO> getDoneTasks(String userId, String processDefinitionKey, String taskName,
                                       int pageNum, int pageSize);

    /**
     * 获取任务详情
     *
     * @param taskId 任务ID
     * @return 任务信息
     */
    TaskVO getTask(String taskId);

    /**
     * 完成任务（审批）
     *
     * @param taskId 任务ID
     * @param dto 完成任务请求参数
     * @param userId 操作人ID
     * @param userName 操作人姓名
     */
    void completeTask(String taskId, CompleteTaskDTO dto, String userId, String userName);

    /**
     * 委派任务
     * 
     * 委派后，任务执行人变为被委派人，原执行人变为owner
     * 被委派人完成任务后，任务会自动回到原执行人
     *
     * @param taskId 任务ID
     * @param dto 委派请求参数
     */
    void delegateTask(String taskId, TaskAssignDTO dto);

    /**
     * 转派任务
     * 
     * 转派后，任务执行人直接变为目标用户
     *
     * @param taskId 任务ID
     * @param dto 转派请求参数
     */
    void transferTask(String taskId, TaskAssignDTO dto);

    /**
     * 签收任务
     * 
     * 将候选任务签收为自己的任务
     *
     * @param taskId 任务ID
     * @param userId 签收人ID
     */
    void claimTask(String taskId, String userId);

    /**
     * 取消签收
     * 
     * 将已签收的任务退回到候选状态
     *
     * @param taskId 任务ID
     */
    void unclaimTask(String taskId);

    /**
     * 获取候选任务列表
     * 
     * 查询用户所在组的候选任务
     *
     * @param userId 用户ID
     * @param groups 用户所属组列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResponse<TaskVO> getCandidateTasks(String userId, List<String> groups, int pageNum, int pageSize);
}
