-- =============================================
-- 广告制作业务系统 - 数据库脚本
-- =============================================

USE `art_training`;

-- =============================================
-- 客户表
-- =============================================
DROP TABLE IF EXISTS `biz_customer`;
CREATE TABLE `biz_customer` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `customer_no` VARCHAR(32) NOT NULL COMMENT '客户编号',
    `customer_name` VARCHAR(128) NOT NULL COMMENT '客户名称',
    `customer_type` TINYINT DEFAULT 1 COMMENT '客户类型 1-个人 2-企业',
    `contact_person` VARCHAR(32) DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `contact_email` VARCHAR(128) DEFAULT NULL COMMENT '联系邮箱',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `province` VARCHAR(32) DEFAULT NULL COMMENT '省',
    `city` VARCHAR(32) DEFAULT NULL COMMENT '市',
    `district` VARCHAR(32) DEFAULT NULL COMMENT '区',
    `wechat` VARCHAR(64) DEFAULT NULL COMMENT '微信号',
    `qq` VARCHAR(32) DEFAULT NULL COMMENT 'QQ号',
    `company_name` VARCHAR(128) DEFAULT NULL COMMENT '公司名称',
    `tax_no` VARCHAR(64) DEFAULT NULL COMMENT '税号',
    `bank_name` VARCHAR(128) DEFAULT NULL COMMENT '开户银行',
    `bank_account` VARCHAR(64) DEFAULT NULL COMMENT '银行账号',
    `invoice_title` VARCHAR(128) DEFAULT NULL COMMENT '发票抬头',
    `level` TINYINT DEFAULT 1 COMMENT '客户等级 1-普通 2-优质 3-VIP 4-战略合作',
    `source` TINYINT DEFAULT 1 COMMENT '客户来源 1-门店 2-转介绍 3-线上推广 4-电话营销 5-其他',
    `preferences` TEXT DEFAULT NULL COMMENT '制作偏好（JSON格式）',
    `total_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '累计消费金额',
    `order_count` INT DEFAULT 0 COMMENT '订单数量',
    `last_order_time` DATETIME DEFAULT NULL COMMENT '最后下单时间',
    `campus_id` BIGINT DEFAULT NULL COMMENT '所属校区ID',
    `campus_code` VARCHAR(32) DEFAULT NULL COMMENT '所属校区编码',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
    `version` INT DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_customer_no` (`customer_no`),
    KEY `idx_customer_name` (`customer_name`),
    KEY `idx_contact_phone` (`contact_phone`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_customer_type` (`customer_type`),
    KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户表';

-- =============================================
-- 合同档案表
-- =============================================
DROP TABLE IF EXISTS `biz_contract`;
CREATE TABLE `biz_contract` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `contract_no` VARCHAR(64) NOT NULL COMMENT '合同编号',
    `contract_name` VARCHAR(128) NOT NULL COMMENT '合同名称',
    `customer_id` BIGINT NOT NULL COMMENT '客户ID',
    `customer_name` VARCHAR(128) DEFAULT NULL COMMENT '客户名称',
    `contract_type` TINYINT DEFAULT 1 COMMENT '合同类型 1-年度框架 2-项目合同 3-长期合作',
    `sign_date` DATE DEFAULT NULL COMMENT '签订日期',
    `start_date` DATE DEFAULT NULL COMMENT '开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '结束日期',
    `contract_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '合同金额',
    `settlement_method` TINYINT DEFAULT 1 COMMENT '结算方式 1-月结 2-季结 3-年结 4-按单结算',
    `discount_rate` DECIMAL(5,2) DEFAULT 100.00 COMMENT '折扣率（%）',
    `contact_person` VARCHAR(32) DEFAULT NULL COMMENT '我方联系人',
    `customer_contact` VARCHAR(32) DEFAULT NULL COMMENT '客户联系人',
    `customer_phone` VARCHAR(20) DEFAULT NULL COMMENT '客户联系电话',
    `files` TEXT DEFAULT NULL COMMENT '合同附件（JSON数组）',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-草稿 1-生效中 2-即将到期 3-已到期 4-已作废',
    `remind_days` INT DEFAULT 30 COMMENT '到期提醒天数',
    `reminded` TINYINT DEFAULT 0 COMMENT '是否已提醒 0-否 1-是',
    `campus_id` BIGINT DEFAULT NULL COMMENT '所属校区ID',
    `campus_code` VARCHAR(32) DEFAULT NULL COMMENT '所属校区编码',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
    `version` INT DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_contract_no` (`contract_no`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_end_date` (`end_date`),
    KEY `idx_status` (`status`),
    KEY `idx_campus_id` (`campus_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同档案表';

-- =============================================
-- 订单表
-- =============================================
DROP TABLE IF EXISTS `biz_order`;
CREATE TABLE `biz_order` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
    `order_name` VARCHAR(255) NOT NULL COMMENT '订单名称',
    `customer_id` BIGINT NOT NULL COMMENT '客户ID',
    `customer_name` VARCHAR(128) DEFAULT NULL COMMENT '客户名称',
    `contact_person` VARCHAR(32) DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `order_type` TINYINT DEFAULT 1 COMMENT '订单类型 1-喷绘 2-写真 3-雕刻 4-印刷 5-标识标牌 6-其他',
    `order_source` TINYINT DEFAULT 1 COMMENT '订单来源 1-门店 2-电话 3-微信 4-线上 5-转介绍',
    `urgent` TINYINT DEFAULT 0 COMMENT '是否加急 0-否 1-是',
    `order_status` TINYINT DEFAULT 1 COMMENT '订单状态 1-待确认 2-已确认 3-制作中 4-待复核 5-已完成 6-已取件 7-已取消',
    `total_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '订单金额',
    `received_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '已收金额',
    `payment_status` TINYINT DEFAULT 0 COMMENT '付款状态 0-未付款 1-部分付款 2-已付清',
    `delivery_type` TINYINT DEFAULT 1 COMMENT '交付方式 1-自取 2-配送 3-邮寄',
    `delivery_address` VARCHAR(255) DEFAULT NULL COMMENT '配送地址',
    `receiver` VARCHAR(32) DEFAULT NULL COMMENT '收货人',
    `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货电话',
    `expect_date` DATE DEFAULT NULL COMMENT '预计交付日期',
    `actual_date` DATE DEFAULT NULL COMMENT '实际交付日期',
    `designer_id` BIGINT DEFAULT NULL COMMENT '设计师ID',
    `designer_name` VARCHAR(32) DEFAULT NULL COMMENT '设计师姓名',
    `producer_id` BIGINT DEFAULT NULL COMMENT '制作员ID',
    `producer_name` VARCHAR(32) DEFAULT NULL COMMENT '制作员姓名',
    `reviewer_id` BIGINT DEFAULT NULL COMMENT '复核员ID',
    `reviewer_name` VARCHAR(32) DEFAULT NULL COMMENT '复核员姓名',
    `review_time` DATETIME DEFAULT NULL COMMENT '复核时间',
    `review_remark` VARCHAR(500) DEFAULT NULL COMMENT '复核备注',
    `files` TEXT DEFAULT NULL COMMENT '订单文件（JSON数组）',
    `requirement` TEXT DEFAULT NULL COMMENT '制作要求',
    `campus_id` BIGINT DEFAULT NULL COMMENT '所属校区ID',
    `campus_code` VARCHAR(32) DEFAULT NULL COMMENT '所属校区编码',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
    `version` INT DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_urgent` (`urgent`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_campus_id` (`campus_id`),
    KEY `idx_order_type` (`order_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- =============================================
-- 订单明细表
-- =============================================
DROP TABLE IF EXISTS `biz_order_item`;
CREATE TABLE `biz_order_item` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(32) DEFAULT NULL COMMENT '订单编号',
    `item_name` VARCHAR(255) NOT NULL COMMENT '项目名称',
    `item_type` VARCHAR(64) DEFAULT NULL COMMENT '项目类型',
    `specification` VARCHAR(255) DEFAULT NULL COMMENT '规格',
    `material` VARCHAR(128) DEFAULT NULL COMMENT '材质',
    `quantity` DECIMAL(10,2) DEFAULT 1.00 COMMENT '数量',
    `unit` VARCHAR(16) DEFAULT NULL COMMENT '单位',
    `unit_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价',
    `amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '金额',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
    `version` INT DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- =============================================
-- 操作记录表
-- =============================================
DROP TABLE IF EXISTS `biz_operation_log`;
CREATE TABLE `biz_operation_log` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型 customer/contract/order',
    `biz_id` BIGINT NOT NULL COMMENT '业务ID',
    `biz_no` VARCHAR(64) DEFAULT NULL COMMENT '业务编号',
    `operation_type` VARCHAR(32) NOT NULL COMMENT '操作类型 create/update/delete/review/status_change',
    `operation_name` VARCHAR(64) NOT NULL COMMENT '操作名称',
    `operation_content` TEXT DEFAULT NULL COMMENT '操作内容/详情',
    `before_data` TEXT DEFAULT NULL COMMENT '变更前数据（JSON）',
    `after_data` TEXT DEFAULT NULL COMMENT '变更后数据（JSON）',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(64) DEFAULT NULL COMMENT '操作人姓名',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
    `campus_id` BIGINT DEFAULT NULL COMMENT '所属校区ID',
    `campus_code` VARCHAR(32) DEFAULT NULL COMMENT '所属校区编码',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_biz_type_id` (`biz_type`, `biz_id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_campus_id` (`campus_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作记录表';

-- =============================================
-- 客户消费记录表
-- =============================================
DROP TABLE IF EXISTS `biz_customer_consume`;
CREATE TABLE `biz_customer_consume` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `customer_id` BIGINT NOT NULL COMMENT '客户ID',
    `customer_name` VARCHAR(128) DEFAULT NULL COMMENT '客户名称',
    `order_id` BIGINT DEFAULT NULL COMMENT '订单ID',
    `order_no` VARCHAR(32) DEFAULT NULL COMMENT '订单编号',
    `consume_type` TINYINT DEFAULT 1 COMMENT '消费类型 1-订单消费 2-充值 3-退款 4-其他',
    `amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '金额',
    `balance` DECIMAL(12,2) DEFAULT 0.00 COMMENT '消费后余额',
    `consume_date` DATE DEFAULT NULL COMMENT '消费日期',
    `campus_id` BIGINT DEFAULT NULL COMMENT '所属校区ID',
    `campus_code` VARCHAR(32) DEFAULT NULL COMMENT '所属校区编码',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_consume_date` (`consume_date`),
    KEY `idx_campus_id` (`campus_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户消费记录表';

-- =============================================
-- 初始化业务权限数据
-- =============================================

-- 业务管理菜单
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `component`, `icon`, `sort_order`, `status`, `visible`) VALUES
(100, 0, 'business', '业务管理', 1, '/business', 'Layout', 'briefcase', 2, 1, 1),
(101, 100, 'business:customer', '客户管理', 1, '/business/customer', 'business/customer/index', 'user-friends', 1, 1, 1),
(102, 100, 'business:order', '订单管理', 1, '/business/order', 'business/order/index', 'file-text', 2, 1, 1),
(103, 100, 'business:contract', '合同管理', 1, '/business/contract', 'business/contract/index', 'file-contract', 3, 1, 1),
(104, 100, 'business:report', '数据报表', 1, '/business/report', 'business/report/index', 'chart-bar', 4, 1, 1);

-- 客户管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `component`, `icon`, `sort_order`, `status`, `visible`) VALUES
(110, 101, 'business:customer:list', '客户列表', 3, '', '', '', 1, 1, 1),
(111, 101, 'business:customer:add', '新增客户', 3, '', '', '', 2, 1, 1),
(112, 101, 'business:customer:edit', '编辑客户', 3, '', '', '', 3, 1, 1),
(113, 101, 'business:customer:delete', '删除客户', 3, '', '', '', 4, 1, 1),
(114, 101, 'business:customer:view', '查看详情', 3, '', '', '', 5, 1, 1),
(115, 101, 'business:customer:export', '导出客户', 3, '', '', '', 6, 1, 1);

-- 订单管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `component`, `icon`, `sort_order`, `status`, `visible`) VALUES
(120, 102, 'business:order:list', '订单列表', 3, '', '', '', 1, 1, 1),
(121, 102, 'business:order:add', '新增订单', 3, '', '', '', 2, 1, 1),
(122, 102, 'business:order:edit', '编辑订单', 3, '', '', '', 3, 1, 1),
(123, 102, 'business:order:delete', '删除订单', 3, '', '', '', 4, 1, 1),
(124, 102, 'business:order:view', '查看详情', 3, '', '', '', 5, 1, 1),
(125, 102, 'business:order:review', '订单复核', 3, '', '', '', 6, 1, 1),
(126, 102, 'business:order:status', '状态变更', 3, '', '', '', 7, 1, 1),
(127, 102, 'business:order:export', '导出订单', 3, '', '', '', 8, 1, 1),
(128, 102, 'business:order:urgent', '加急标记', 3, '', '', '', 9, 1, 1);

-- 合同管理按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `component`, `icon`, `sort_order`, `status`, `visible`) VALUES
(130, 103, 'business:contract:list', '合同列表', 3, '', '', '', 1, 1, 1),
(131, 103, 'business:contract:add', '新增合同', 3, '', '', '', 2, 1, 1),
(132, 103, 'business:contract:edit', '编辑合同', 3, '', '', '', 3, 1, 1),
(133, 103, 'business:contract:delete', '删除合同', 3, '', '', '', 4, 1, 1),
(134, 103, 'business:contract:view', '查看详情', 3, '', '', '', 5, 1, 1),
(135, 103, 'business:contract:upload', '上传附件', 3, '', '', '', 6, 1, 1),
(136, 103, 'business:contract:remind', '到期提醒', 3, '', '', '', 7, 1, 1);

-- 报表权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_code`, `permission_name`, `permission_type`, `path`, `component`, `icon`, `sort_order`, `status`, `visible`) VALUES
(140, 104, 'business:report:view', '查看报表', 3, '', '', '', 1, 1, 1),
(141, 104, 'business:report:export', '导出报表', 3, '', '', '', 2, 1, 1),
(142, 104, 'business:report:operationLog', '操作记录', 3, '', '', '', 3, 1, 1);

-- 给超级管理员分配业务权限
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES
(100, 1, 100),
(101, 1, 101),
(102, 1, 102),
(103, 1, 103),
(104, 1, 104),
(110, 1, 110),
(111, 1, 111),
(112, 1, 112),
(113, 1, 113),
(114, 1, 114),
(115, 1, 115),
(120, 1, 120),
(121, 1, 121),
(122, 1, 122),
(123, 1, 123),
(124, 1, 124),
(125, 1, 125),
(126, 1, 126),
(127, 1, 127),
(128, 1, 128),
(130, 1, 130),
(131, 1, 131),
(132, 1, 132),
(133, 1, 133),
(134, 1, 134),
(135, 1, 135),
(136, 1, 136),
(140, 1, 140),
(141, 1, 141),
(142, 1, 142);
