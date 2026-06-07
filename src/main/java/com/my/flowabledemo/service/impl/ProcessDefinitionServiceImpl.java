package com.my.flowabledemo.service.impl;

import com.my.flowabledemo.common.BusinessException;
import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.common.enums.ResultCode;
import com.my.flowabledemo.service.ProcessDefinitionService;
import com.my.flowabledemo.vo.process.DeploymentVO;
import com.my.flowabledemo.vo.process.ProcessDefinitionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * 流程定义管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    private final RepositoryService repositoryService;

    /**
     * 部署流程定义（通过文件上传）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeploymentVO deploy(MultipartFile file, String name, String tenantId) {
        try {
            String originalFilename = file.getOriginalFilename();
            
            // 构建部署对象
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
            
            // 设置部署名称
            if (StringUtils.hasText(name)) {
                deploymentBuilder.name(name);
            } else if (StringUtils.hasText(originalFilename)) {
                deploymentBuilder.name(originalFilename);
            }
            
            // 设置租户ID
            if (StringUtils.hasText(tenantId)) {
                deploymentBuilder.tenantId(tenantId);
            }

            // 根据文件类型处理
            if (originalFilename != null && originalFilename.endsWith(".zip")) {
                // ZIP文件处理
                try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
                    deploymentBuilder.addZipInputStream(zipInputStream);
                }
            } else {
                // BPMN文件处理
                deploymentBuilder.addInputStream(originalFilename, file.getInputStream());
            }

            // 执行部署
            Deployment deployment = deploymentBuilder.deploy();
            log.info("流程部署成功，部署ID: {}, 名称: {}", deployment.getId(), deployment.getName());

            return convertToDeploymentVO(deployment);
        } catch (IOException e) {
            log.error("流程部署失败", e);
            throw new BusinessException(ResultCode.DEPLOY_FAILED, "文件读取失败: " + e.getMessage());
        }
    }

    /**
     * 通过XML字符串部署流程
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeploymentVO deployByXml(String xml, String name, String tenantId) {
        try {
            // 构建部署对象
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
            
            // 设置部署名称，默认使用流程Key
            String resourceName = "process.bpmn20.xml";
            if (StringUtils.hasText(name)) {
                deploymentBuilder.name(name);
            }
            
            // 设置租户ID
            if (StringUtils.hasText(tenantId)) {
                deploymentBuilder.tenantId(tenantId);
            }

            // 添加XML资源
            deploymentBuilder.addString(resourceName, xml);

            // 执行部署
            Deployment deployment = deploymentBuilder.deploy();
            log.info("流程部署成功（XML方式），部署ID: {}, 名称: {}", deployment.getId(), deployment.getName());

            return convertToDeploymentVO(deployment);
        } catch (Exception e) {
            log.error("流程部署失败（XML方式）", e);
            throw new BusinessException(ResultCode.DEPLOY_FAILED, "XML解析失败: " + e.getMessage());
        }
    }

    /**
     * 获取流程定义列表（分页）
     */
    @Override
    public PageResponse<ProcessDefinitionVO> getDefinitions(String name, String key, int pageNum, int pageSize) {
        // 构建查询条件
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .orderByProcessDefinitionName().asc()
                .orderByProcessDefinitionVersion().desc();

        // 按名称模糊查询
        if (StringUtils.hasText(name)) {
            query.processDefinitionNameLike("%" + name + "%");
        }

        // 按Key精确查询
        if (StringUtils.hasText(key)) {
            query.processDefinitionKey(key);
        }

        // 查询总数
        long total = query.count();

        // 查询分页数据
        List<ProcessDefinition> definitions = query
                .listPage((pageNum - 1) * pageSize, pageSize);

        // 转换为VO
        List<ProcessDefinitionVO> voList = new ArrayList<>();
        for (ProcessDefinition definition : definitions) {
            voList.add(convertToDefinitionVO(definition));
        }

        return PageResponse.of(total, voList, pageNum, pageSize);
    }

    /**
     * 获取流程定义详情
     */
    @Override
    public ProcessDefinitionVO getDefinition(String processDefinitionId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();

        if (definition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        return convertToDefinitionVO(definition);
    }

    /**
     * 获取流程定义的BPMN XML
     */
    @Override
    public String getDefinitionXml(String processDefinitionId) {
        // 获取流程定义
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();

        if (definition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        // 读取XML资源
        try (InputStream inputStream = repositoryService.getResourceAsStream(
                definition.getDeploymentId(), definition.getResourceName())) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("获取流程定义XML失败", e);
            throw new BusinessException("获取XML失败: " + e.getMessage());
        }
    }

    /**
     * 删除部署
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeployment(String deploymentId, boolean cascade) {
        // 检查部署是否存在
        Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentId(deploymentId)
                .singleResult();

        if (deployment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部署不存在");
        }

        // 删除部署
        if (cascade) {
            // 级联删除：删除关联的流程实例和历史
            repositoryService.deleteDeployment(deploymentId, true);
            log.info("级联删除部署成功，部署ID: {}", deploymentId);
        } else {
            // 普通删除：如果有运行中的流程实例会报错
            repositoryService.deleteDeployment(deploymentId);
            log.info("删除部署成功，部署ID: {}", deploymentId);
        }
    }

    /**
     * 挂起流程定义
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void suspendDefinition(String processDefinitionId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();

        if (definition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        if (definition.isSuspended()) {
            throw new BusinessException("流程定义已处于挂起状态");
        }

        // 挂起流程定义，true表示同时挂起所有关联的流程实例
        repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
        log.info("流程定义挂起成功，ID: {}", processDefinitionId);
    }

    /**
     * 激活流程定义
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateDefinition(String processDefinitionId) {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();

        if (definition == null) {
            throw new BusinessException(ResultCode.PROCESS_DEFINITION_NOT_FOUND);
        }

        if (!definition.isSuspended()) {
            throw new BusinessException("流程定义已处于激活状态");
        }

        // 激活流程定义，true表示同时激活所有关联的流程实例
        repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
        log.info("流程定义激活成功，ID: {}", processDefinitionId);
    }

    /**
     * 获取最新版本的流程定义列表
     */
    @Override
    public List<ProcessDefinitionVO> getLatestDefinitions() {
        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .orderByProcessDefinitionName().asc()
                .list();

        List<ProcessDefinitionVO> voList = new ArrayList<>();
        for (ProcessDefinition definition : definitions) {
            voList.add(convertToDefinitionVO(definition));
        }

        return voList;
    }

    // ==================== 私有方法 ====================

    /**
     * 转换为流程定义VO
     */
    private ProcessDefinitionVO convertToDefinitionVO(ProcessDefinition definition) {
        ProcessDefinitionVO vo = new ProcessDefinitionVO();
        vo.setId(definition.getId());
        vo.setKey(definition.getKey());
        vo.setName(definition.getName());
        vo.setVersion(definition.getVersion());
        vo.setDeploymentId(definition.getDeploymentId());
        vo.setResourceName(definition.getResourceName());
        vo.setDescription(definition.getDescription());
        vo.setSuspended(definition.isSuspended());

        // 获取部署时间
        Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentId(definition.getDeploymentId())
                .singleResult();
        if (deployment != null) {
            vo.setDeploymentTime(deployment.getDeploymentTime());
        }

        return vo;
    }

    /**
     * 转换为部署VO
     */
    private DeploymentVO convertToDeploymentVO(Deployment deployment) {
        DeploymentVO vo = new DeploymentVO();
        vo.setId(deployment.getId());
        vo.setName(deployment.getName());
        vo.setDeploymentTime(deployment.getDeploymentTime());
        vo.setTenantId(deployment.getTenantId());
        return vo;
    }
}
