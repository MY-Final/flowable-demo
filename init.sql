-- =============================================
-- Flowable Demo 业务扩展表
-- 注意：Flowable会自动创建28张表，这里只创建业务扩展表
-- =============================================

-- ----------------------------
-- 1. 部门表
-- ----------------------------
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    parent_id   BIGINT       DEFAULT 0   COMMENT '父部门ID',
    name        VARCHAR(50)  NOT NULL    COMMENT '部门名称',
    sort        INT          DEFAULT 0   COMMENT '显示顺序',
    leader      VARCHAR(20)  DEFAULT NULL COMMENT '负责人',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    email       VARCHAR(50)  DEFAULT NULL COMMENT '邮箱',
    status      TINYINT      DEFAULT 1   COMMENT '状态（1正常 0停用）',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 2. 用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL    COMMENT '用户名',
    password    VARCHAR(100) NOT NULL    COMMENT '密码',
    real_name   VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
    dept_id     BIGINT       DEFAULT NULL COMMENT '部门ID',
    email       VARCHAR(50)  DEFAULT NULL COMMENT '邮箱',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    avatar      VARCHAR(200) DEFAULT NULL COMMENT '头像',
    status      TINYINT      DEFAULT 1   COMMENT '状态（1正常 0停用）',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 3. 角色表
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name   VARCHAR(50)  NOT NULL    COMMENT '角色名称',
    role_key    VARCHAR(50)  NOT NULL    COMMENT '角色标识',
    sort        INT          DEFAULT 0   COMMENT '显示顺序',
    status      TINYINT      DEFAULT 1   COMMENT '状态（1正常 0停用）',
    remark      VARCHAR(200) DEFAULT NULL COMMENT '备注',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 4. 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 5. 表单定义表
-- ----------------------------
DROP TABLE IF EXISTS form_definition;
CREATE TABLE form_definition (
    id            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '表单ID',
    form_key      VARCHAR(100)  NOT NULL    COMMENT '表单标识',
    form_name     VARCHAR(100)  NOT NULL    COMMENT '表单名称',
    form_json     TEXT          NOT NULL    COMMENT '表单JSON Schema',
    version       INT           DEFAULT 1   COMMENT '版本号',
    status        TINYINT       DEFAULT 1   COMMENT '状态（1正常 0停用）',
    remark        VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    create_by     BIGINT        DEFAULT NULL COMMENT '创建人',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_form_key_version (form_key, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单定义表';

-- ----------------------------
-- 6. 流程-表单关联表
-- ----------------------------
DROP TABLE IF EXISTS process_form;
CREATE TABLE process_form (
    id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    process_def_key  VARCHAR(100) NOT NULL COMMENT '流程定义Key',
    process_def_id   VARCHAR(100) DEFAULT NULL COMMENT '流程定义ID',
    node_id          VARCHAR(100) NOT NULL COMMENT '节点ID（start表示启动表单）',
    node_name        VARCHAR(100) DEFAULT NULL COMMENT '节点名称',
    form_id          BIGINT       NOT NULL COMMENT '表单ID',
    sort             INT          DEFAULT 0  COMMENT '排序',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_process_def_key (process_def_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程表单关联表';

-- ----------------------------
-- 7. 审批记录表（扩展Flowable）
-- ----------------------------
DROP TABLE IF EXISTS approval_record;
CREATE TABLE approval_record (
    id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    process_instance_id  VARCHAR(100) NOT NULL COMMENT '流程实例ID',
    task_id              VARCHAR(100) NOT NULL COMMENT '任务ID',
    task_name            VARCHAR(100) DEFAULT NULL COMMENT '任务名称',
    process_def_key      VARCHAR(100) DEFAULT NULL COMMENT '流程定义Key',
    business_key         VARCHAR(100) DEFAULT NULL COMMENT '业务Key',
    assignee             BIGINT       DEFAULT NULL COMMENT '审批人ID',
    assignee_name        VARCHAR(50)  DEFAULT NULL COMMENT '审批人姓名',
    opinion              VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
    status               VARCHAR(20)  NOT NULL COMMENT '状态（agree/reject/delegate/transfer）',
    duration             BIGINT       DEFAULT NULL COMMENT '耗时（毫秒）',
    create_time          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_process_instance (process_instance_id),
    KEY idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';

-- ----------------------------
-- 8. 流程业务数据关联表
-- ----------------------------
DROP TABLE IF EXISTS process_business;
CREATE TABLE process_business (
    id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    process_instance_id  VARCHAR(100) NOT NULL COMMENT '流程实例ID',
    process_def_key      VARCHAR(100) NOT NULL COMMENT '流程定义Key',
    business_key         VARCHAR(100) NOT NULL COMMENT '业务Key',
    business_type        VARCHAR(50)  DEFAULT NULL COMMENT '业务类型',
    title                VARCHAR(200) DEFAULT NULL COMMENT '流程标题',
    initiator            BIGINT       DEFAULT NULL COMMENT '发起人ID',
    initiator_name       VARCHAR(50)  DEFAULT NULL COMMENT '发起人姓名',
    status               VARCHAR(20)  DEFAULT 'running' COMMENT '状态（running/completed/canceled）',
    form_data            TEXT         DEFAULT NULL COMMENT '表单数据JSON',
    create_time          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_business_key (business_key),
    KEY idx_process_instance (process_instance_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程业务数据关联表';


-- =============================================
-- 初始化数据
-- =============================================

-- ----------------------------
-- 初始化部门数据
-- ----------------------------
INSERT INTO sys_dept (id, parent_id, name, sort, leader) VALUES
(1, 0, '总公司', 0, '管理员'),
(2, 1, '技术部', 1, '张三'),
(3, 1, '产品部', 2, '李四'),
(4, 1, '人事部', 3, '王五'),
(5, 1, '财务部', 4, '赵六');

-- ----------------------------
-- 初始化用户数据（密码都是123456的BCrypt加密）
-- ----------------------------
INSERT INTO sys_user (id, username, password, real_name, dept_id, phone) VALUES
(1, 'admin',   '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '管理员', 1, '13800000001'),
(2, 'zhangsan','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三', 2, '13800000002'),
(3, 'lisi',    '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四', 3, '13800000003'),
(4, 'wangwu',  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王五', 4, '13800000004'),
(5, 'zhaoliu', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵六', 5, '13800000005');

-- ----------------------------
-- 初始化角色数据
-- ----------------------------
INSERT INTO sys_role (id, role_name, role_key, remark) VALUES
(1, '管理员', 'admin', '拥有所有权限'),
(2, '部门经理', 'dept_manager', '部门审批权限'),
(3, '普通员工', 'employee', '基本操作权限'),
(4, 'HR', 'hr', '人事相关权限');

-- ----------------------------
-- 用户角色关联
-- ----------------------------
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 2),
(4, 4),
(5, 3);
