package com.my.flowabledemo.service;

import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.vo.process.DeploymentVO;
import com.my.flowabledemo.vo.process.ProcessDefinitionVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 流程定义管理服务接口
 */
public interface ProcessDefinitionService {

    /**
     * 部署流程定义
     *
     * @param file      BPMN文件（.bpmn 或 .bpmn20.xml）
     * @param name      部署名称（可选）
     * @param tenantId  租户ID（可选）
     * @return 部署信息
     */
    DeploymentVO deploy(MultipartFile file, String name, String tenantId);

    /**
     * 通过XML字符串部署流程
     *
     * @param xml       BPMN XML内容
     * @param name      部署名称
     * @param tenantId  租户ID（可选）
     * @return 部署信息
     */
    DeploymentVO deployByXml(String xml, String name, String tenantId);

    /**
     * 获取流程定义列表
     *
     * @param name      流程名称（模糊查询）
     * @param key       流程Key（精确查询）
     * @param pageNum   页码
     * @param pageSize  每页数量
     * @return 分页结果
     */
    PageResponse<ProcessDefinitionVO> getDefinitions(String name, String key, int pageNum, int pageSize);

    /**
     * 获取流程定义详情
     *
     * @param processDefinitionId 流程定义ID
     * @return 流程定义信息
     */
    ProcessDefinitionVO getDefinition(String processDefinitionId);

    /**
     * 获取流程定义的BPMN XML
     *
     * @param processDefinitionId 流程定义ID
     * @return XML内容
     */
    String getDefinitionXml(String processDefinitionId);

    /**
     * 删除部署
     *
     * @param deploymentId 部署ID
     * @param cascade      是否级联删除（删除关联的流程实例和历史）
     */
    void deleteDeployment(String deploymentId, boolean cascade);

    /**
     * 挂起流程定义
     *
     * @param processDefinitionId 流程定义ID
     */
    void suspendDefinition(String processDefinitionId);

    /**
     * 激活流程定义
     *
     * @param processDefinitionId 流程定义ID
     */
    void activateDefinition(String processDefinitionId);

    /**
     * 获取最新版本的流程定义列表
     *
     * @return 流程定义列表
     */
    List<ProcessDefinitionVO> getLatestDefinitions();
}
