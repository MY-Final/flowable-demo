package com.my.flowabledemo.service.impl;

import com.my.flowabledemo.common.BusinessException;
import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.common.enums.ResultCode;
import com.my.flowabledemo.dto.task.CompleteTaskDTO;
import com.my.flowabledemo.dto.task.TaskAssignDTO;
import com.my.flowabledemo.service.TaskService;
import com.my.flowabledemo.vo.task.TaskVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.task.Task;
import org.flowable.engine.task.TaskQuery;
import org.flowable.identitylink.api.IdentityLink;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 任务管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskService taskService;
    private final HistoryService historyService;

    /**
     * 获取待办任务列表（分页）
     */
    @Override
    public PageResponse<TaskVO> getTodoTasks(String userId, String processDefinitionKey, String taskName,
                                              int pageNum, int pageSize) {
        // 构建查询条件
        TaskQuery query = taskService.createTaskQuery()
                .taskAssignee(userId)
                .orderByTaskCreateTime().desc();

        // 按流程定义Key查询
        if (StringUtils.hasText(processDefinitionKey)) {
            query.processDefinitionKey(processDefinitionKey);
        }

        // 按任务名称模糊查询
        if (StringUtils.hasText(taskName)) {
            query.taskNameLike("%" + taskName + "%");
        }

        // 查询总数
        long total = query.count();

        // 查询分页数据
        List<Task> tasks = query.listPage((pageNum - 1) * pageSize, pageSize);

        // 转换为VO
        List<TaskVO> voList = new ArrayList<>();
        for (Task task : tasks) {
            voList.add(convertToTaskVO(task));
        }

        return PageResponse.of(total, voList, pageNum, pageSize);
    }

    /**
     * 获取已办任务列表（分页）
     */
    @Override
    public PageResponse<TaskVO> getDoneTasks(String userId, String processDefinitionKey, String taskName,
                                              int pageNum, int pageSize) {
        // 查询已完成的历史任务
        org.flowable.task.api.history.HistoricTaskInstanceQuery query = historyService
                .createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .finished()
                .orderByHistoricTaskInstanceEndTime().desc();

        // 按流程定义Key查询
        if (StringUtils.hasText(processDefinitionKey)) {
            query.processDefinitionKey(processDefinitionKey);
        }

        // 按任务名称模糊查询
        if (StringUtils.hasText(taskName)) {
            query.taskNameLike("%" + taskName + "%");
        }

        // 查询总数
        long total = query.count();

        // 查询分页数据
        List<org.flowable.task.api.history.HistoricTaskInstance> tasks = query
                .listPage((pageNum - 1) * pageSize, pageSize);

        // 转换为VO
        List<TaskVO> voList = new ArrayList<>();
        for (org.flowable.task.api.history.HistoricTaskInstance task : tasks) {
            voList.add(convertToHistoryTaskVO(task));
        }

        return PageResponse.of(total, voList, pageNum, pageSize);
    }

    /**
     * 获取任务详情
     */
    @Override
    public TaskVO getTask(String taskId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        return convertToTaskVO(task);
    }

    /**
     * 完成任务（审批）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(String taskId, CompleteTaskDTO dto, String userId, String userName) {
        try {
            // 查询任务
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(ResultCode.TASK_NOT_FOUND);
            }

            // 检查任务是否已分配给当前用户
            if (!userId.equals(task.getAssignee())) {
                throw new BusinessException("该任务不是分配给你的，无法操作");
            }

            // 构建任务变量
            Map<String, Object> variables = dto.getVariables() != null ? new HashMap<>(dto.getVariables()) : new HashMap<>();
            variables.put("approved", "agree".equals(dto.getStatus()));
            variables.put("opinion", dto.getOpinion());
            variables.put("approver", userId);
            variables.put("approverName", userName);

            // 完成任务
            taskService.complete(taskId, variables);

            log.info("任务完成，任务ID: {}, 操作人: {}, 状态: {}", taskId, userName, dto.getStatus());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("完成任务失败", e);
            throw new BusinessException("完成任务失败: " + e.getMessage());
        }
    }

    /**
     * 委派任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(String taskId, TaskAssignDTO dto) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(ResultCode.TASK_NOT_FOUND);
            }

            // 委派任务
            taskService.delegateTask(taskId, dto.getUserId());

            log.info("任务委派成功，任务ID: {}, 委派人: {}, 被委派人: {}", 
                    taskId, task.getAssignee(), dto.getUserName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("委派任务失败", e);
            throw new BusinessException("委派任务失败: " + e.getMessage());
        }
    }

    /**
     * 转派任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferTask(String taskId, TaskAssignDTO dto) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(ResultCode.TASK_NOT_FOUND);
            }

            // 转派任务（设置新的执行人）
            taskService.setAssignee(taskId, dto.getUserId());

            log.info("任务转派成功，任务ID: {}, 原执行人: {}, 新执行人: {}", 
                    taskId, task.getAssignee(), dto.getUserName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("转派任务失败", e);
            throw new BusinessException("转派任务失败: " + e.getMessage());
        }
    }

    /**
     * 签收任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimTask(String taskId, String userId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(ResultCode.TASK_NOT_FOUND);
            }

            if (task.getAssignee() != null) {
                throw new BusinessException("任务已被其他人签收");
            }

            // 签收任务
            taskService.claim(taskId, userId);

            log.info("任务签收成功，任务ID: {}, 签收人: {}", taskId, userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("签收任务失败", e);
            throw new BusinessException("签收任务失败: " + e.getMessage());
        }
    }

    /**
     * 取消签收
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unclaimTask(String taskId) {
        try {
            Task task = taskService.createTaskQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new BusinessException(ResultCode.TASK_NOT_FOUND);
            }

            // 取消签收
            taskService.unclaim(taskId);

            log.info("取消签收成功，任务ID: {}", taskId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("取消签收失败", e);
            throw new BusinessException("取消签收失败: " + e.getMessage());
        }
    }

    /**
     * 获取候选任务列表
     */
    @Override
    public PageResponse<TaskVO> getCandidateTasks(String userId, List<String> groups, int pageNum, int pageSize) {
        // 查询候选任务（用户或用户所在组）
        TaskQuery query = taskService.createTaskQuery()
                .taskCandidateUser(userId)
                .orderByTaskCreateTime().desc();

        // 如果有组信息，也查询组的候选任务
        if (groups != null && !groups.isEmpty()) {
            query = taskService.createTaskQuery()
                    .or()
                    .taskCandidateUser(userId)
                    .taskCandidateGroupIn(groups)
                    .endOr()
                    .orderByTaskCreateTime().desc();
        }

        // 查询总数
        long total = query.count();

        // 查询分页数据
        List<Task> tasks = query.listPage((pageNum - 1) * pageSize, pageSize);

        // 转换为VO
        List<TaskVO> voList = new ArrayList<>();
        for (Task task : tasks) {
            voList.add(convertToTaskVO(task));
        }

        return PageResponse.of(total, voList, pageNum, pageSize);
    }

    // ==================== 私有方法 ====================

    /**
     * 转换为任务VO
     */
    private TaskVO convertToTaskVO(Task task) {
        TaskVO vo = new TaskVO();
        vo.setId(task.getId());
        vo.setName(task.getName());
        vo.setDescription(task.getDescription());
        vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        vo.setAssignee(task.getAssignee());
        vo.setCreateTime(task.getCreateTime());
        vo.setDueDate(task.getDueDate());
        vo.setPriority(task.getPriority());
        vo.setClaimed(task.getAssignee() != null);
        vo.setDelegated(task.getDelegationState() != null && task.getDelegationState() == org.flowable.task.api.DelegationState.PENDING);
        vo.setOwner(task.getOwner());

        // 获取流程相关信息
        if (StringUtils.hasText(task.getProcessInstanceId())) {
            org.flowable.engine.runtime.ProcessInstance processInstance = org.flowable.engine.impl.context.Context.getProcessEngineConfiguration()
                    .getRuntimeService()
                    .createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            
            if (processInstance != null) {
                vo.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
                vo.setProcessDefinitionName(processInstance.getProcessDefinitionName());
                vo.setBusinessKey(processInstance.getBusinessKey());
                vo.setStartUserId(processInstance.getStartUserId());
            }
        }

        // 获取任务变量
        Map<String, Object> variables = taskService.getVariables(task.getId());
        if (variables != null) {
            vo.setTitle((String) variables.get("title"));
            vo.setVariables(variables);
        }

        return vo;
    }

    /**
     * 转换为历史任务VO
     */
    private TaskVO convertToHistoryTaskVO(org.flowable.task.api.history.HistoricTaskInstance task) {
        TaskVO vo = new TaskVO();
        vo.setId(task.getId());
        vo.setName(task.getName());
        vo.setDescription(task.getDescription());
        vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
        vo.setProcessInstanceId(task.getProcessInstanceId());
        vo.setProcessDefinitionId(task.getProcessDefinitionId());
        vo.setAssignee(task.getAssignee());
        vo.setCreateTime(task.getCreateTime());
        vo.setDueDate(task.getDueDate());
        vo.setPriority(task.getPriority());

        // 获取流程相关信息
        if (StringUtils.hasText(task.getProcessInstanceId())) {
            HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            
            if (processInstance != null) {
                vo.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
                vo.setProcessDefinitionName(processInstance.getProcessDefinitionName());
                vo.setBusinessKey(processInstance.getBusinessKey());
                vo.setStartUserId(processInstance.getStartUserId());
            }
        }

        return vo;
    }
}
