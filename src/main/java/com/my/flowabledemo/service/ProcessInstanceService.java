package com.my.flowabledemo.service;

import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.dto.process.StartProcessDTO;
import com.my.flowabledemo.vo.process.ProcessHistoryVO;
import com.my.flowabledemo.vo.process.ProcessInstanceVO;

import java.util.List;

/**
 * 流程实例管理服务接口
 */
public interface ProcessInstanceService {

    /**
     * 启动流程实例
     *
     * @param dto 启动流程请求参数
     * @param userId 发起人ID
     * @param userName 发起人姓名
     * @return 流程实例信息
     */
    ProcessInstanceVO startProcess(StartProcessDTO dto, String userId, String userName);

    /**
     * 获取流程实例列表（分页）
     *
     * @param processDefinitionKey 流程定义Key（可选）
     * @param businessKey 业务Key（可选）
     * @param startedBy 发起人（可选）
     * @param suspended 是否挂起（可选）
     * @param finished 是否已结束（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResponse<ProcessInstanceVO> getInstances(String processDefinitionKey, String businessKey,
                                                  String startedBy, Boolean suspended, Boolean finished,
                                                  int pageNum, int pageSize);

    /**
     * 获取流程实例详情
     *
     * @param processInstanceId 流程实例ID
     * @return 流程实例信息
     */
    ProcessInstanceVO getInstance(String processInstanceId);

    /**
     * 删除/终止流程实例
     *
     * @param processInstanceId 流程实例ID
     * @param reason 删除原因
     */
    void deleteInstance(String processInstanceId, String reason);

    /**
     * 挂起流程实例
     *
     * @param processInstanceId 流程实例ID
     */
    void suspendInstance(String processInstanceId);

    /**
     * 激活流程实例
     *
     * @param processInstanceId 流程实例ID
     */
    void activateInstance(String processInstanceId);

    /**
     * 获取流程执行历史
     *
     * @param processInstanceId 流程实例ID
     * @return 历史记录列表
     */
    List<ProcessHistoryVO> getHistory(String processInstanceId);
}
