package com.my.flowabledemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.my.flowabledemo.common.BusinessException;
import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.common.enums.ResultCode;
import com.my.flowabledemo.dto.form.FormBindDTO;
import com.my.flowabledemo.dto.form.FormDefinitionDTO;
import com.my.flowabledemo.mapper.FormDefinitionMapper;
import com.my.flowabledemo.mapper.ProcessFormMapper;
import com.my.flowabledemo.pojo.FormDefinition;
import com.my.flowabledemo.pojo.ProcessForm;
import com.my.flowabledemo.service.FormService;
import com.my.flowabledemo.vo.form.FormDefinitionVO;
import com.my.flowabledemo.vo.form.ProcessFormConfigVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 表单配置管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {

    private final FormDefinitionMapper formDefinitionMapper;
    private final ProcessFormMapper processFormMapper;

    /**
     * 创建表单定义
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FormDefinitionVO createForm(FormDefinitionDTO dto, Long createBy) {
        // 检查表单标识是否已存在
        LambdaQueryWrapper<FormDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FormDefinition::getFormKey, dto.getFormKey());
        Long count = formDefinitionMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS, "表单标识已存在");
        }

        // 创建表单定义
        FormDefinition form = new FormDefinition();
        form.setFormKey(dto.getFormKey());
        form.setFormName(dto.getFormName());
        form.setFormJson(dto.getFormJson());
        form.setVersion(1);
        form.setStatus(1);
        form.setRemark(dto.getRemark());
        form.setCreateBy(createBy);
        form.setCreateTime(new Date());
        form.setUpdateTime(new Date());

        formDefinitionMapper.insert(form);

        log.info("表单创建成功，ID: {}, 标识: {}", form.getId(), form.getFormKey());

        return convertToVO(form);
    }

    /**
     * 更新表单定义
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FormDefinitionVO updateForm(Long id, FormDefinitionDTO dto) {
        FormDefinition form = formDefinitionMapper.selectById(id);
        if (form == null) {
            throw new BusinessException(ResultCode.FORM_NOT_FOUND);
        }

        // 检查表单标识是否被其他表单使用
        LambdaQueryWrapper<FormDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FormDefinition::getFormKey, dto.getFormKey())
               .ne(FormDefinition::getId, id);
        Long count = formDefinitionMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS, "表单标识已被其他表单使用");
        }

        // 更新表单
        form.setFormKey(dto.getFormKey());
        form.setFormName(dto.getFormName());
        form.setFormJson(dto.getFormJson());
        form.setRemark(dto.getRemark());
        form.setUpdateTime(new Date());

        formDefinitionMapper.updateById(form);

        log.info("表单更新成功，ID: {}", id);

        return convertToVO(form);
    }

    /**
     * 获取表单定义详情
     */
    @Override
    public FormDefinitionVO getForm(Long id) {
        FormDefinition form = formDefinitionMapper.selectById(id);
        if (form == null) {
            throw new BusinessException(ResultCode.FORM_NOT_FOUND);
        }
        return convertToVO(form);
    }

    /**
     * 获取表单定义列表（分页）
     */
    @Override
    public PageResponse<FormDefinitionVO> getForms(String formKey, String formName, int pageNum, int pageSize) {
        LambdaQueryWrapper<FormDefinition> wrapper = new LambdaQueryWrapper<>();

        // 按表单标识查询
        if (StringUtils.hasText(formKey)) {
            wrapper.eq(FormDefinition::getFormKey, formKey);
        }

        // 按表单名称模糊查询
        if (StringUtils.hasText(formName)) {
            wrapper.like(FormDefinition::getFormName, formName);
        }

        wrapper.orderByDesc(FormDefinition::getCreateTime);

        // 查询总数
        Long total = formDefinitionMapper.selectCount(wrapper);

        // 查询分页数据
        wrapper.last("LIMIT " + (pageNum - 1) * pageSize + ", " + pageSize);
        List<FormDefinition> forms = formDefinitionMapper.selectList(wrapper);

        // 转换为VO
        List<FormDefinitionVO> voList = new ArrayList<>();
        for (FormDefinition form : forms) {
            voList.add(convertToVO(form));
        }

        return PageResponse.of(total, voList, pageNum, pageSize);
    }

    /**
     * 删除表单定义
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteForm(Long id) {
        FormDefinition form = formDefinitionMapper.selectById(id);
        if (form == null) {
            throw new BusinessException(ResultCode.FORM_NOT_FOUND);
        }

        // 检查表单是否已绑定流程
        LambdaQueryWrapper<ProcessForm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessForm::getFormId, id);
        Long count = processFormMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCode.FORM_BIND_EXISTS, "表单已绑定流程，请先解绑");
        }

        formDefinitionMapper.deleteById(id);

        log.info("表单删除成功，ID: {}", id);
    }

    /**
     * 绑定表单到流程节点
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindForm(FormBindDTO dto) {
        // 检查表单是否存在
        FormDefinition form = formDefinitionMapper.selectById(dto.getFormId());
        if (form == null) {
            throw new BusinessException(ResultCode.FORM_NOT_FOUND);
        }

        // 检查是否已绑定
        LambdaQueryWrapper<ProcessForm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessForm::getProcessDefKey, dto.getProcessDefKey())
               .eq(ProcessForm::getNodeId, dto.getNodeId());
        ProcessForm existingBind = processFormMapper.selectOne(wrapper);

        if (existingBind != null) {
            // 更新绑定
            existingBind.setFormId(dto.getFormId());
            existingBind.setNodeName(dto.getNodeName());
            existingBind.setUpdateTime(new Date());
            processFormMapper.updateById(existingBind);
        } else {
            // 创建新绑定
            ProcessForm processForm = new ProcessForm();
            processForm.setProcessDefKey(dto.getProcessDefKey());
            processForm.setNodeId(dto.getNodeId());
            processForm.setNodeName(dto.getNodeName());
            processForm.setFormId(dto.getFormId());
            processForm.setCreateTime(new Date());
            processForm.setUpdateTime(new Date());

            processFormMapper.insert(processForm);
        }

        log.info("表单绑定成功，流程Key: {}, 节点: {}, 表单ID: {}", 
                dto.getProcessDefKey(), dto.getNodeId(), dto.getFormId());
    }

    /**
     * 解绑流程节点的表单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindForm(String processDefKey, String nodeId) {
        LambdaQueryWrapper<ProcessForm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessForm::getProcessDefKey, processDefKey)
               .eq(ProcessForm::getNodeId, nodeId);

        int count = processFormMapper.delete(wrapper);

        if (count > 0) {
            log.info("表单解绑成功，流程Key: {}, 节点: {}", processDefKey, nodeId);
        }
    }

    /**
     * 获取流程关联的所有表单配置
     */
    @Override
    public ProcessFormConfigVO getProcessFormConfig(String processDefKey) {
        ProcessFormConfigVO config = new ProcessFormConfigVO();
        config.setProcessDefKey(processDefKey);

        // 查询流程关联的所有表单
        LambdaQueryWrapper<ProcessForm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessForm::getProcessDefKey, processDefKey);
        wrapper.orderByAsc(ProcessForm::getSort);
        List<ProcessForm> processForms = processFormMapper.selectList(wrapper);

        // 构建节点表单列表
        List<ProcessFormConfigVO.NodeFormVO> nodeForms = new ArrayList<>();
        for (ProcessForm processForm : processForms) {
            FormDefinition form = formDefinitionMapper.selectById(processForm.getFormId());
            if (form == null) {
                continue;
            }

            ProcessFormConfigVO.NodeFormVO nodeForm = new ProcessFormConfigVO.NodeFormVO();
            nodeForm.setNodeId(processForm.getNodeId());
            nodeForm.setNodeName(processForm.getNodeName());
            nodeForm.setFormId(form.getId());
            nodeForm.setFormKey(form.getFormKey());
            nodeForm.setFormName(form.getFormName());
            nodeForm.setFormJson(form.getFormJson());

            // 区分启动表单和节点表单
            if ("start".equals(processForm.getNodeId())) {
                config.setStartForm(convertToVO(form));
            } else {
                nodeForms.add(nodeForm);
            }
        }

        config.setNodeForms(nodeForms);

        return config;
    }

    /**
     * 获取指定节点的表单
     */
    @Override
    public FormDefinitionVO getNodeForm(String processDefKey, String nodeId) {
        LambdaQueryWrapper<ProcessForm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessForm::getProcessDefKey, processDefKey)
               .eq(ProcessForm::getNodeId, nodeId);
        ProcessForm processForm = processFormMapper.selectOne(wrapper);

        if (processForm == null) {
            return null;
        }

        FormDefinition form = formDefinitionMapper.selectById(processForm.getFormId());
        if (form == null) {
            return null;
        }

        return convertToVO(form);
    }

    // ==================== 私有方法 ====================

    /**
     * 转换为VO
     */
    private FormDefinitionVO convertToVO(FormDefinition form) {
        FormDefinitionVO vo = new FormDefinitionVO();
        vo.setId(form.getId());
        vo.setFormKey(form.getFormKey());
        vo.setFormName(form.getFormName());
        vo.setFormJson(form.getFormJson());
        vo.setVersion(form.getVersion());
        vo.setStatus(form.getStatus());
        vo.setRemark(form.getRemark());
        vo.setCreateBy(form.getCreateBy());
        vo.setCreateTime(form.getCreateTime());
        vo.setUpdateTime(form.getUpdateTime());
        return vo;
    }
}
