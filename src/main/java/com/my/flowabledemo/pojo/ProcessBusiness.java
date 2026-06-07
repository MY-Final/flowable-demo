package com.my.flowabledemo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 流程业务数据关联表
 * @TableName process_business
 */
@TableName(value ="process_business")
@Data
public class ProcessBusiness {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 流程定义Key
     */
    private String processDefKey;

    /**
     * 业务Key
     */
    private String businessKey;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 流程标题
     */
    private String title;

    /**
     * 发起人ID
     */
    private Long initiator;

    /**
     * 发起人姓名
     */
    private String initiatorName;

    /**
     * 状态（running/completed/canceled）
     */
    private String status;

    /**
     * 表单数据JSON
     */
    private String formData;

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
        ProcessBusiness other = (ProcessBusiness) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProcessInstanceId() == null ? other.getProcessInstanceId() == null : this.getProcessInstanceId().equals(other.getProcessInstanceId()))
            && (this.getProcessDefKey() == null ? other.getProcessDefKey() == null : this.getProcessDefKey().equals(other.getProcessDefKey()))
            && (this.getBusinessKey() == null ? other.getBusinessKey() == null : this.getBusinessKey().equals(other.getBusinessKey()))
            && (this.getBusinessType() == null ? other.getBusinessType() == null : this.getBusinessType().equals(other.getBusinessType()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getInitiator() == null ? other.getInitiator() == null : this.getInitiator().equals(other.getInitiator()))
            && (this.getInitiatorName() == null ? other.getInitiatorName() == null : this.getInitiatorName().equals(other.getInitiatorName()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getFormData() == null ? other.getFormData() == null : this.getFormData().equals(other.getFormData()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProcessInstanceId() == null) ? 0 : getProcessInstanceId().hashCode());
        result = prime * result + ((getProcessDefKey() == null) ? 0 : getProcessDefKey().hashCode());
        result = prime * result + ((getBusinessKey() == null) ? 0 : getBusinessKey().hashCode());
        result = prime * result + ((getBusinessType() == null) ? 0 : getBusinessType().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getInitiator() == null) ? 0 : getInitiator().hashCode());
        result = prime * result + ((getInitiatorName() == null) ? 0 : getInitiatorName().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getFormData() == null) ? 0 : getFormData().hashCode());
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
        sb.append(", processInstanceId=").append(processInstanceId);
        sb.append(", processDefKey=").append(processDefKey);
        sb.append(", businessKey=").append(businessKey);
        sb.append(", businessType=").append(businessType);
        sb.append(", title=").append(title);
        sb.append(", initiator=").append(initiator);
        sb.append(", initiatorName=").append(initiatorName);
        sb.append(", status=").append(status);
        sb.append(", formData=").append(formData);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}