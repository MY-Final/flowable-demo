package com.my.flowabledemo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 流程表单关联表
 * @TableName process_form
 */
@TableName(value ="process_form")
@Data
public class ProcessForm {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 流程定义Key
     */
    private String processDefKey;

    /**
     * 流程定义ID
     */
    private String processDefId;

    /**
     * 节点ID（start表示启动表单）
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 表单ID
     */
    private Long formId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ProcessForm other = (ProcessForm) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProcessDefKey() == null ? other.getProcessDefKey() == null : this.getProcessDefKey().equals(other.getProcessDefKey()))
            && (this.getProcessDefId() == null ? other.getProcessDefId() == null : this.getProcessDefId().equals(other.getProcessDefId()))
            && (this.getNodeId() == null ? other.getNodeId() == null : this.getNodeId().equals(other.getNodeId()))
            && (this.getNodeName() == null ? other.getNodeName() == null : this.getNodeName().equals(other.getNodeName()))
            && (this.getFormId() == null ? other.getFormId() == null : this.getFormId().equals(other.getFormId()))
            && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProcessDefKey() == null) ? 0 : getProcessDefKey().hashCode());
        result = prime * result + ((getProcessDefId() == null) ? 0 : getProcessDefId().hashCode());
        result = prime * result + ((getNodeId() == null) ? 0 : getNodeId().hashCode());
        result = prime * result + ((getNodeName() == null) ? 0 : getNodeName().hashCode());
        result = prime * result + ((getFormId() == null) ? 0 : getFormId().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", processDefKey=").append(processDefKey);
        sb.append(", processDefId=").append(processDefId);
        sb.append(", nodeId=").append(nodeId);
        sb.append(", nodeName=").append(nodeName);
        sb.append(", formId=").append(formId);
        sb.append(", sort=").append(sort);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}