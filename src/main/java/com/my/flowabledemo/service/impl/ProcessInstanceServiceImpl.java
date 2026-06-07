package com.my.flowabledemo.service.impl;

import com.my.flowabledemo.common.BusinessException;
import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.common.enums.ResultCode;
import com.my.flowabledemo.dto.process.StartProcessDTO;
import com.my.flowabledemo.service.ProcessInstanceService;
import com.my.flowabledemo.vo.process.ProcessHistoryVO;
import com.my.flowabledemo.vo.process.ProcessInstanceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 流程实例管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    /**
     * 启动流程实例
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstanceVO startProcess(StartProcessDTO dto, String userId, String userName) {
        try {
            // 构建流程变量
            Map<String, Object> variables = dto.getVariables() != null ? new HashMap<>(dto.getVariables()) : new HashMap<>();
            
            // 设置发起人信息
            variables.put("initiator", userId);
            variables.put("initiatorName", userName);

            // 构建业务Key
            String businessKey = dto.getBusinessKey();
            if (!StringUtils.hasText(businessKey)) {
                businessKey = UUID.randomUUID().toString().replace("-", "");
            }

            // 启动流程实例
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                    dto.getProcessDefinitionKey(),
                    businessKey,
                    variables
            );

            // 设置流程标题（如果提供）
            if (StringUtils.hasText(dto.getTitle())) {
                // 将标题存储在流程变量中，或者关联到业务表
                variables.put("title", dto.getTitle());
            }

            log.info("流程实例启动成功，实例ID: {}, 流程Key: {}, 发起人: {}", 
                    processInstance.getId(), dto.getProcessDefinitionKey(), userName);

            // 转换为VO返回
            return convertToInstanceVO(processInstance);
        } catch (Exception e) {
            log.error("启动流程实例失败", e);
            throw new BusinessException(ResultCode.PROCESS_START_FAILED, "启动流程失败: " + e.getMessage());
        }
    }

    /**
     * 获取流程实例列表（分页）
     */
    @Override
    public PageResponse<ProcessInstanceVO> getInstances(String processDefinitionKey, String businessKey,
                                                         String startedBy, Boolean suspended, Boolean finished,
                                                         int pageNum, int pageSize) {
        // 构建查询条件
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery()
                .orderByProcessInstanceId().desc();

        // 按流程定义Key查询
        if (StringUtils.hasText(processDefinitionKey)) {
            query.processDefinitionKey(processDefinitionKey);
        }

        // 按业务Key查询
        if (StringUtils.hasText(businessKey)) {
            query.processInstanceBusinessKey(businessKey);
        }

        // 按发起人查询
        if (StringUtils.hasText(startedBy)) {
            query.startedBy(startedBy);
        }

        // 按挂起状态查询
        if (suspended != null && suspended) {
            query.suspended();
        } else if (suspended != null && !suspended) {
            query.active();
        }

        // 查询总数
        long total = query.count();

        // 查询分页数据
        List<ProcessInstance> instances = query.listPage((pageNum - 1) * pageSize, pageSize);

        // 转换为VO
        List<ProcessInstanceVO> voList = new ArrayList<>();
        for (ProcessInstance instance : instances) {
            voList.add(convertToInstanceVO(instance));
        }

        return PageResponse.of(total, voList, pageNum, pageSize);
    }

    /**
     * 获取流程实例详情
     */
    @Override
    public ProcessInstanceVO getInstance(String processInstanceId) {
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (instance == null) {
            // 检查是否在历史记录中
            throw new BusinessException(ResultCode.PROCESS_INSTANCE_NOT_FOUND);
        }

        return convertToInstanceVO(instance);
    }

    /**
     * 删除/终止流程实例
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInstance(String processInstanceId, String reason) {
        try {
            // 检查流程实例是否存在
            ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (instance == null) {
                throw new BusinessException(ResultCode.PROCESS_INSTANCE_NOT_FOUND);
            }

            // 删除流程实例
            runtimeService.deleteProcessInstance(processInstanceId, reason);
            log.info("流程实例删除成功，实例ID: {}, 原因: {}", processInstanceId, reason);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除流程实例失败", e);
            throw new BusinessException("删除流程实例失败: " + e.getMessage());
        }
    }

    /**
     * 挂起流程实例
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void suspendInstance(String processInstanceId) {
        try {
            ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (instance == null) {
                throw new BusinessException(ResultCode.PROCESS_INSTANCE_NOT_FOUND);
            }

            if (instance.isSuspended()) {
                throw new BusinessException("流程实例已处于挂起状态");
            }

            runtimeService.suspendProcessInstanceById(processInstanceId);
            log.info("流程实例挂起成功，实例ID: {}", processInstanceId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("挂起流程实例失败", e);
            throw new BusinessException("挂起流程实例失败: " + e.getMessage());
        }
    }

    /**
     * 激活流程实例
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateInstance(String processInstanceId) {
        try {
            ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (instance == null) {
                throw new BusinessException(ResultCode.PROCESS_INSTANCE_NOT_FOUND);
            }

            if (!instance.isSuspended()) {
                throw new BusinessException("流程实例已处于激活状态");
            }

            runtimeService.activateProcessInstanceById(processInstanceId);
            log.info("流程实例激活成功，实例ID: {}", processInstanceId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("激活流程实例失败", e);
            throw new BusinessException("激活流程实例失败: " + e.getMessage());
        }
    }

    /**
     * 获取流程执行历史
     */
    @Override
    public List<ProcessHistoryVO> getHistory(String processInstanceId) {
        // 查询历史活动实例
        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();

        // 转换为VO
        List<ProcessHistoryVO> historyList = new ArrayList<>();
        for (HistoricActivityInstance activity : activityInstances) {
            historyList.add(convertToHistoryVO(activity));
        }

        return historyList;
    }

    // ==================== 私有方法 ====================

    /**
     * 转换为流程实例VO
     */
    private ProcessInstanceVO convertToInstanceVO(ProcessInstance instance) {
        ProcessInstanceVO vo = new ProcessInstanceVO();
        vo.setId(instance.getId());
        vo.setProcessDefinitionId(instance.getProcessDefinitionId());
        vo.setProcessDefinitionKey(instance.getProcessDefinitionKey());
        vo.setProcessDefinitionName(instance.getProcessDefinitionName());
        vo.setProcessDefinitionVersion(instance.getProcessDefinitionVersion());
        vo.setBusinessKey(instance.getBusinessKey());
        vo.setEnded(instance.isEnded());
        vo.setSuspended(instance.isSuspended());
        vo.setStartUserId(instance.getStartUserId());
        vo.setStartTime(instance.getStartTime());

        // 获取当前活动节点
        List<Execution> executions = runtimeService.createExecutionQuery()
                .processInstanceId(instance.getId())
                .list();
        if (!executions.isEmpty()) {
            // 这里简化处理，实际可能需要更复杂的逻辑来确定当前活动
            vo.setCurrentActivityId(instance.getActivityId());
        }

        // 获取流程变量中的标题
        Map<String, Object> variables = runtimeService.getVariables(instance.getId());
        if (variables != null && variables.containsKey("title")) {
            vo.setTitle((String) variables.get("title"));
        }

        return vo;
    }

    /**
     * 转换为历史记录VO
     */
    private ProcessHistoryVO convertToHistoryVO(HistoricActivityInstance activity) {
        ProcessHistoryVO vo = new ProcessHistoryVO();
        vo.setId(activity.getId());
        vo.setProcessInstanceId(activity.getProcessInstanceId());
        vo.setActivityId(activity.getActivityId());
        vo.setActivityName(activity.getActivityName());
        vo.setActivityType(activity.getActivityType());
        vo.setAssignee(activity.getAssignee());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setDurationInMillis(activity.getDurationInMillis());
        vo.setTaskId(activity.getTaskId());
        vo.setDeleteReason(activity.getDeleteReason());
        return vo;
    }
}
