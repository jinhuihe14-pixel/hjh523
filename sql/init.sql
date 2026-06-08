-- =============================================
-- 艺术培训集团管理系统 - 用户中心与权限中心数据库脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS `art_training` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `art_training`;

-- =============================================
-- 校区表
-- =============================================
DROP TABLE IF EXISTS `sys_campus`;
CREATE TABLE `sys_campus` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `campus_code` VARCHAR(32) NOT NULL COMMENT '校区编码',
    `campus_name` VARCHAR(64) NOT NULL COMMENT '校区名称',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '校区地址',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `principal` VARCHAR(32) DEFAULT NULL COMMENT '负责人',
    `principal_id` BIGINT DEFAULT NULL COMMENT '负责人ID',
    `province` VARCHAR(32) DEFAULT NULL COMMENT '省',
    `city` VARCHAR(32) DEFAULT NULL COMMENT '市',
    `district` VARCHAR(32) DEFAULT NULL COMMENT '区',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
    `version` INT DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_campus_code` (`campus_code`),
    KEY `idx_campus_name` (`campus_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='校区表';

-- =============================================
-- 用户表
-- =============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
    `nick_name` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `gender` TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `user_type` TINYINT NOT NULL DEFAULT 5 COMMENT '用户类型 0-超级管理员 1-集团管理员 2-校区校长 3-教师 4-课程顾问 5-学员 6-财务',
    `campus_id` BIGINT DEFAULT NULL COMMENT '所属校区ID',
    `campus_code` VARCHAR(32) DEFAULT NULL COMMENT '所属校区编码',
    `employee_no` VARCHAR(32) DEFAULT NULL COMMENT '员工编号',
    `student_no` VARCHAR(32) DEFAULT NULL COMMENT '学员编号',
    `entry_date` DATE DEFAULT NULL COMMENT '入职日期',
    `department` VARCHAR(64) DEFAULT NULL COMMENT '所属部门',
    `position` VARCHAR(64) DEFAULT NULL COMMENT '职位',
    `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    `emergency_contact` VARCHAR(32) DEFAULT NULL COMMENT '紧急联系人',
    `emergency_phone` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `password_update_time` DATETIME DEFAULT NULL COMMENT '密码更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
    `version` INT DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_real_name` (`real_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 角色表
-- =============================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码',
    `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `role_type` TINYINT DEFAULT 1 COMMENT '角色类型 1-系统角色 2-业务角色',
    `data_scope` TINYINT DEFAULT 1 COMMENT '数据权限范围 1-全部 2-本校区 3-本人',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
    `version` INT DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- =============================================
-- 用户角色关联表
-- =============================================
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =============================================
-- 权限表
-- =============================================
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父级ID',
    `permission_code` VARCHAR(128) NOT NULL COMMENT '权限编码',
    `permission_name` VARCHAR(64) NOT NULL COMMENT '权限名称',
    `permission_type` TINYINT NOT NULL DEFAULT 1 COMMENT '权限类型 1-菜单 2-按钮 3-接口',
    `path` VARCHAR(255) DEFAULT NULL COMMENT '路由路径',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '前端组件',
    `icon` VARCHAR(64) DEFAULT NULL COMMENT '图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `visible` TINYINT DEFAULT 1 COMMENT '是否可见 0-隐藏 1-显示',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
    `version` INT DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- =============================================
-- 角色权限关联表
-- =============================================
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- =============================================
-- 初始化数据
-- =============================================

-- 校区数据
INSERT INTO `sys_campus` (`id`, `campus_code`, `campus_name`, `address`, `phone`, `sort_order`, `status`) VALUES
(1, 'CAMPUS001', '北京朝阳校区', '北京市朝阳区建国路88号', '010-12345678', 1, 1),
(2, 'CAMPUS002', '上海浦东校区', '上海市浦东新区陆家嘴环路1000号', '021-87654321', 2, 1),
(3, 'CAMPUS003', '广州天河校区', '广州市天河区天河路385号', '020-11112222', 3, 1),
(4, 'CAMPUS004', '深圳南山校区', '深圳市南山区科技园路1号', '0755-33334444', 4, 1),
(5, 'CAMPUS005', '杭州西湖校区', '杭州市西湖区文三路478号', '0571-55556666', 5, 1),
(6, 'CAMPUS006', '成都高新校区', '成都市高新区天府大道1700号', '028-77778888', 6, 1);

-- 超级管理员用户 (密码: 123456，BCrypt加密)
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `user_type`, `status`) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', '13800000000', 'admin@arttraining.com', 0, 1);

-- 角色数据
INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `role_type`, `data_scope`, `sort_order`, `status`, `remark`) VALUES
(1, 'super_admin', '超级管理员', 1, 1, 1, 1, '系统超级管理员，拥有全部权限'),
(2, 'group_admin', '集团管理员', 1, 1, 2, 1, '集团总部管理员'),
(3, 'campus_principal', '校区校长', 2, 2, 3, 1, '校区负责人'),
(4, 'teacher', '授课教师', 2, 3, 4, 1, '授课老师'),
(5, 'consultant', '课程顾问', 2, 2, 5, 1, '招生顾问'),
(6, 'finance', '财务人员', 2, 2, 6, 1, '财务人员'),
(7, 'student', '学员', 2, 3, 7, 1, '学员角色');

-- 用户角色关联
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES
(1, 1, 1);

-- 菜单权限数据
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `component`, `icon`, `sort_order`, `status`, `visible`) VALUES
(1, 0, 'system', '系统管理', 1, '/system', 'Layout', 'setting', 1, 1, 1),
(2, 1, 'system:user', '用户管理', 1, '/system/user', 'system/user/index', 'user', 1, 1, 1),
(3, 1, 'system:role', '角色管理', 1, '/system/role', 'system/role/index', 'role', 2, 1, 1),
(4, 1, 'system:permission', '权限管理', 1, '/system/permission', 'system/permission/index', 'permission', 3, 1, 1),
(5, 1, 'system:campus', '校区管理', 1, '/system/campus', 'system/campus/index', 'building', 4, 1, 1),
(6, 2, 'system:user:list', '用户列表', 3, '', '', '', 1, 1, 1),
(7, 2, 'system:user:add', '新增用户', 3, '', '', '', 2, 1, 1),
(8, 2, 'system:user:edit', '编辑用户', 3, '', '', '', 3, 1, 1),
(9, 2, 'system:user:delete', '删除用户', 3, '', '', '', 4, 1, 1),
(10, 2, 'system:user:resetPwd', '重置密码', 3, '', '', '', 5, 1, 1),
(11, 3, 'system:role:list', '角色列表', 3, '', '', '', 1, 1, 1),
(12, 3, 'system:role:add', '新增角色', 3, '', '', '', 2, 1, 1),
(13, 3, 'system:role:edit', '编辑角色', 3, '', '', '', 3, 1, 1),
(14, 3, 'system:role:delete', '删除角色', 3, '', '', '', 4, 1, 1),
(15, 3, 'system:role:permission', '角色权限', 3, '', '', '', 5, 1, 1),
(16, 4, 'system:permission:list', '权限列表', 3, '', '', '', 1, 1, 1),
(17, 4, 'system:permission:add', '新增权限', 3, '', '', '', 2, 1, 1),
(18, 4, 'system:permission:edit', '编辑权限', 3, '', '', '', 3, 1, 1),
(19, 4, 'system:permission:delete', '删除权限', 3, '', '', '', 4, 1, 1),
(20, 5, 'system:campus:list', '校区列表', 3, '', '', '', 1, 1, 1),
(21, 5, 'system:campus:add', '新增校区', 3, '', '', '', 2, 1, 1),
(22, 5, 'system:campus:edit', '编辑校区', 3, '', '', '', 3, 1, 1),
(23, 5, 'system:campus:delete', '删除校区', 3, '', '', '', 4, 1, 1);

-- 角色权限关联 (超级管理员拥有全部权限)
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 1, 3),
(4, 1, 4),
(5, 1, 5),
(6, 1, 6),
(7, 1, 7),
(8, 1, 8),
(9, 1, 9),
(10, 1, 10),
(11, 1, 11),
(12, 1, 12),
(13, 1, 13),
(14, 1, 14),
(15, 1, 15),
(16, 1, 16),
(17, 1, 17),
(18, 1, 18),
(19, 1, 19),
(20, 1, 20),
(21, 1, 21),
(22, 1, 22),
(23, 1, 23);
