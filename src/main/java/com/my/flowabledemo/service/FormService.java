package com.my.flowabledemo.service;

import com.my.flowabledemo.common.PageResponse;
import com.my.flowabledemo.dto.form.FormBindDTO;
import com.my.flowabledemo.dto.form.FormDefinitionDTO;
import com.my.flowabledemo.vo.form.FormDefinitionVO;
import com.my.flowabledemo.vo.form.ProcessFormConfigVO;

/**
 * 表单配置管理服务接口
 */
public interface FormService {

    /**
     * 创建表单定义
     *
     * @param dto 表单定义请求参数
     * @param createBy 创建人ID
     * @return 表单定义信息
     */
    FormDefinitionVO createForm(FormDefinitionDTO dto, Long createBy);

    /**
     * 更新表单定义
     *
     * @param id 表单ID
     * @param dto 表单定义请求参数
     * @return 表单定义信息
     */
    FormDefinitionVO updateForm(Long id, FormDefinitionDTO dto);

    /**
     * 获取表单定义详情
     *
     * @param id 表单ID
     * @return 表单定义信息
     */
    FormDefinitionVO getForm(Long id);

    /**
     * 获取表单定义列表（分页）
     *
     * @param formKey 表单标识（可选）
     * @param formName 表单名称（可选，模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResponse<FormDefinitionVO> getForms(String formKey, String formName, int pageNum, int pageSize);

    /**
     * 删除表单定义
     *
     * @param id 表单ID
     */
    void deleteForm(Long id);

    /**
     * 绑定表单到流程节点
     *
     * @param dto 绑定请求参数
     */
    void bindForm(FormBindDTO dto);

    /**
     * 解绑流程节点的表单
     *
     * @param processDefKey 流程定义Key
     * @param nodeId 节点ID
     */
    void unbindForm(String processDefKey, String nodeId);

    /**
     * 获取流程关联的所有表单配置
     *
     * @param processDefKey 流程定义Key
     * @return 流程表单配置
     */
    ProcessFormConfigVO getProcessFormConfig(String processDefKey);

    /**
     * 获取指定节点的表单
     *
     * @param processDefKey 流程定义Key
     * @param nodeId 节点ID
     * @return 表单定义信息
     */
    FormDefinitionVO getNodeForm(String processDefKey, String nodeId);
}
