-- 创建数据库
CREATE DATABASE `relocateWeb`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `relocateWeb`;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `username` varchar(100) NOT NULL COMMENT '用户名，唯一',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `name` varchar(200) NOT NULL COMMENT '管理员姓名',
  `photo_url` varchar(200) COMMENT '照片URL',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '修改时间',
  `create_user` bigint(20) COMMENT '创建用户ID',
  `update_user` bigint(20) COMMENT '更新用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员';

-- ----------------------------
-- Table structure for configuration
-- ----------------------------
DROP TABLE IF EXISTS `configuration`;
CREATE TABLE `configuration` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `name` varchar(100) NOT NULL COMMENT '配置参数名称',
  `value` varchar(100) NOT NULL COMMENT '配置参数值',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '修改时间',
  `create_user` bigint(20) COMMENT '创建用户ID',
  `update_user` bigint(20) COMMENT '更新用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='配置表';

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `username` varchar(200) NOT NULL COMMENT '用户名，唯一',
  `password` varchar(200) NOT NULL COMMENT '密码',
  `name` varchar(200) NOT NULL COMMENT '姓名',
  `gender` varchar(20) COMMENT '性别',
  `phone` varchar(200) COMMENT '手机号码',
  `email` varchar(200) COMMENT '邮箱',
  `id_card` varchar(200) COMMENT '身份证号码',
  `family_phone` varchar(200) COMMENT '家属电话',
  `photo_url` varchar(200) COMMENT '照片URL',
  `is_banned` tinyint(1) DEFAULT 0 COMMENT '是否被封禁：0-否，1-是',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消费者表';

-- ----------------------------
-- Table structure for driver
-- ----------------------------
DROP TABLE IF EXISTS `driver`;
CREATE TABLE `driver` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `username` varchar(200) NOT NULL COMMENT '用户名，唯一',
  `password` varchar(200) NOT NULL COMMENT '密码',
  `name` varchar(200) NOT NULL COMMENT '姓名',
  `gender` varchar(20) COMMENT '性别',
  `phone` varchar(200) COMMENT '手机号码',
  `id_card` varchar(200) COMMENT '身份证号码',
  `driving_years` int(11) COMMENT '驾龄（年）',
  `photo_url` varchar(200) COMMENT '照片URL',
  `is_banned` tinyint(1) DEFAULT 0 COMMENT '是否被封禁：0-否，1-是',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='司机表';

-- ----------------------------
-- Table structure for driver_truck_type
-- ----------------------------
DROP TABLE IF EXISTS `driver_truck_type`;
CREATE TABLE `driver_truck_type` (
  `driver_id` bigint(20) NOT NULL COMMENT '司机ID，外键，关联 driver 表的 id',
  `truck_type_id` bigint(20) NOT NULL COMMENT '货车类型ID，外键，关联 truck_type 表的 id',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '修改时间',
  PRIMARY KEY (`driver_id`,`truck_type_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记录司机可以驾驶的货车类型';

-- ----------------------------
-- Table structure for mover
-- ----------------------------
DROP TABLE IF EXISTS `mover`;
CREATE TABLE `mover` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `username` varchar(200) NOT NULL COMMENT '用户名，唯一',
  `password` varchar(200) NOT NULL COMMENT '密码',
  `name` varchar(200) NOT NULL COMMENT '姓名',
  `gender` varchar(20) COMMENT '性别',
  `phone` varchar(200) COMMENT '手机号码',
  `id_card` varchar(200) COMMENT '身份证号码',
  `photo_url` varchar(200) COMMENT '照片URL',
  `is_banned` tinyint(1) DEFAULT 0 COMMENT '是否被封禁：0-否，1-是',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搬运工人表';

-- ----------------------------
-- Table structure for moving_order
-- ----------------------------
DROP TABLE IF EXISTS `moving_order`;
CREATE TABLE `moving_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID，外键',
  `order_number` varchar(50) NOT NULL COMMENT '订单号，唯一',
  `service_id` bigint(20) NOT NULL COMMENT '服务项ID，外键，关联 service',
  `truck_type_id` bigint(20) COMMENT '货车类型ID，外键',
  `driver_id` bigint(20) COMMENT '司机ID，外键',
  `vehicle_id` bigint(20) COMMENT '执行订单的车辆ID，外键，关联 vehicle',
  `order_status` int(11) NOT NULL DEFAULT '0' COMMENT '订单状态：0-待接单，1-司机已接单，等待搬运工人，2-已接单，3-进行中，4-已完成，5-已取消',
  `reservation_time` datetime COMMENT '预约时间',
  `moving_origin` varchar(200) NOT NULL COMMENT '搬家起点',
  `moving_destination` varchar(200) NOT NULL COMMENT '搬家目的地',
  `moving_price` decimal(10,2) COMMENT '搬家价格',
  `is_paid` int(11) DEFAULT '0' COMMENT '是否支付：0-未支付，1-已支付，2-已退款',
  `payment_time` datetime COMMENT '支付时间',
  `moving_start_time` datetime COMMENT '搬家开始时间',
  `moving_end_time` datetime COMMENT '搬家结束时间',
  `number_of_helpers` int(11) COMMENT '用户选择的搬运工人数量',
  `notes` text COMMENT '备注',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `order_number` (`order_number`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搬家订单表';

-- ----------------------------
-- Table structure for order_mover
-- ----------------------------
DROP TABLE IF EXISTS `order_mover`;
CREATE TABLE `order_mover` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID，外键，关联 moving_order',
  `mover_id` bigint(20) NOT NULL COMMENT '搬运工人ID，外键，关联 mover',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单搬运工人关联表';

-- ----------------------------
-- Table structure for rating
-- ----------------------------
DROP TABLE IF EXISTS `rating`;
CREATE TABLE `rating` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID，外键',
  `customer_id` bigint(20) NOT NULL COMMENT '消费者ID，外键，关联 customer 表的 id',
  `ratee_id` bigint(20) NOT NULL COMMENT '被评分者ID（可能是 driver.id、mover.id 或 service.id）',
  `rating_type` varchar(50) NOT NULL COMMENT '评分类型（例如：司机，搬运工人，服务）',
  `rating_value` int(11) NOT NULL COMMENT '评分值（例如：1-5星）',
  `comment` text COMMENT '评价内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评分表';

-- ----------------------------
-- Table structure for service
-- ----------------------------
DROP TABLE IF EXISTS `service`;
CREATE TABLE `service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `category_id` bigint(20) NOT NULL COMMENT '服务类型ID，外键，关联 service_category',
  `truck_type_id` bigint(20) NOT NULL COMMENT '货车类型ID，外键，关联 truck_type',
  `service_name` varchar(200) NOT NULL COMMENT '服务项名称',
  `short_description` text COMMENT '服务项简短描述',
  `loading_capacity_description` text COMMENT '装载能力详细说明',
  `default_price` decimal(10,2) NOT NULL COMMENT '起始价格（对应货车类型的5公里内价格）',
  `average_rating` decimal(3,2) DEFAULT 0.00 COMMENT '平均评分值',
  `rating_count` int(11) DEFAULT 0 COMMENT '评分数量',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '修改时间',
  `create_user` bigint(20) COMMENT '创建用户ID',
  `update_user` bigint(20) COMMENT '更新用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务项表';

-- ----------------------------
-- Table structure for service_category
-- ----------------------------
DROP TABLE IF EXISTS `service_category`;
CREATE TABLE `service_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `type_name` varchar(200) NOT NULL COMMENT '服务类型名称',
  `description` varchar(500) COMMENT '服务类型描述',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '修改时间',
  `create_user` bigint(20) COMMENT '创建用户ID',
  `update_user` bigint(20) COMMENT '更新用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务类型表';

-- ----------------------------
-- Table structure for system_log
-- ----------------------------
DROP TABLE IF EXISTS `system_log`;
CREATE TABLE `system_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `log_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志时间，默认当前时间',
  `user_id` bigint(20) COMMENT '操作用户ID',
  `user_type` varchar(50) COMMENT '用户类型（例如：customer, mover, driver, admin）',
  `operation` varchar(200) COMMENT '操作内容',
  `details` text COMMENT '操作详情',
  `ip_address` varchar(50) COMMENT 'IP地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- ----------------------------
-- Table structure for truck_type
-- ----------------------------
DROP TABLE IF EXISTS `truck_type`;
CREATE TABLE `truck_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `type_name` varchar(200) NOT NULL COMMENT '货车类型名称',
  `capacity` varchar(200) NOT NULL COMMENT '车厢长宽高（例如：1.8*1.3*1.1m）',
  `description` varchar(500) NOT NULL COMMENT '货车类型描述',
  `base_fare` decimal(10,2) NOT NULL COMMENT '起步价（5公里以内）',
  `price_per_km_tier1` decimal(10,2) NOT NULL COMMENT '5-25公里每公里价格',
  `price_per_km_tier2` decimal(10,2) NOT NULL COMMENT '25-30公里每公里价格',
  `price_per_km_tier3` decimal(10,2) NOT NULL COMMENT '30-50公里每公里价格',
  `price_per_km_tier4` decimal(10,2) NOT NULL COMMENT '50-80公里每公里价格',
  `price_per_km_tier5` decimal(10,2) NOT NULL COMMENT '超过80公里每公里价格',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '修改时间',
  `create_user` bigint(20) COMMENT '创建用户ID',
  `update_user` bigint(20) COMMENT '更新用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='货车类型表';

-- ----------------------------
-- Table structure for vehicle
-- ----------------------------
DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE `vehicle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `driver_id` bigint(20) NOT NULL COMMENT '司机ID，外键，关联 driver 表的 id',
  `truck_type_id` bigint(20) NOT NULL COMMENT '货车类型ID，外键，关联 truck_type 表的 id',
  `license_plate_number` varchar(200) NOT NULL COMMENT '车牌号，唯一',
  `vehicle_brand` varchar(200) COMMENT '车辆品牌',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '修改时间',
  `create_user` bigint(20) COMMENT '创建用户ID',
  `update_user` bigint(20) COMMENT '更新用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `license_plate_number` (`license_plate_number`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆信息表';


-- ----------------------------
-- Table structure for moving_news
-- ----------------------------
DROP TABLE IF EXISTS `moving_news`;
CREATE TABLE `moving_news` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `title` varchar(255) NOT NULL COMMENT '新闻标题',
  `content` text NOT NULL COMMENT '新闻内容',
  `publish_date` date NOT NULL COMMENT '发布日期',
  `is_published` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已发布：0-否，1-是',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建用户ID（可以关联管理员表）',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新用户ID（可以关联管理员表）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搬家新闻表';

-- ----------------------------
-- Table structure for moving_tips
-- ----------------------------
DROP TABLE IF EXISTS `moving_tips`;
CREATE TABLE `moving_tips` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `title` varchar(255) NOT NULL COMMENT '须知标题',
  `content` text NOT NULL COMMENT '须知内容',
  `category` varchar(100) DEFAULT NULL COMMENT '须知分类（例如：打包技巧、注意事项等）',
  `is_published` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已发布：0-否，1-是',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建用户ID（可以关联管理员表）',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新用户ID（可以关联管理员表）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搬家须知表';

-- ----------------------------
-- Data for table admin
-- ----------------------------
INSERT INTO `admin` (`id`, `username`, `password`, `name`, `photo_url`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, 'admin1', '81dc9bdb52d04dc20036dbd8313ed055', '超级管理员', '/uploads/admin/admin1_avatar.jpg', NOW(), NOW(), NULL, NULL),
(2, 'manager2', '81dc9bdb52d04dc20036dbd8313ed055', '运营经理', '/uploads/admin/manager2_avatar.png', NOW(), NOW(), 1, 1);

-- ----------------------------
-- Data for table configuration
-- ----------------------------
INSERT INTO `configuration` (`id`, `name`, `value`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, 'website_name', '轻松搬家网', NOW(), NOW(), 1, 1),
(2, 'default_currency', 'CNY', NOW(), NOW(), 1, 1);

-- ----------------------------
-- Data for table customer
-- ----------------------------
INSERT INTO `customer` (`id`, `username`, `password`, `name`, `gender`, `phone`, `email`, `id_card`, `family_phone`, `photo_url`, `is_banned`, `create_time`, `update_time`) VALUES
(1, 'customer1', '81dc9bdb52d04dc20036dbd8313ed055', '张三', '男', '13812345678', 'zhangsan@example.com', '310101199001011234', '021-12345678', '/uploads/customer/customer1_photo.jpg', 0, NOW(), NOW()),
(2, 'customer2', '81dc9bdb52d04dc20036dbd8313ed055', '李四', '女', '13987654321', 'lisi@example.com', '440301198505054321', '010-98765432', '/uploads/customer/customer2_photo.png', 0, NOW(), NOW());

-- ----------------------------
-- Data for table truck_type
-- ----------------------------
INSERT INTO `truck_type` (`id`, `type_name`, `capacity`, `description`, `base_fare`, `price_per_km_tier1`, `price_per_km_tier2`, `price_per_km_tier3`, `price_per_km_tier4`, `price_per_km_tier5`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, '小型面包车', '1.8*1.3*1.1m', '灵活便捷，适合小件搬运或城市短驳', 80.00, 5.00, 4.50, 4.00, 3.50, 3.00, NOW(), NOW(), 1, 1),
(2, '中型厢式货车', '2.5*1.8*1.6m', '空间适中，能满足普通家庭或一居室搬家需求', 120.00, 7.00, 6.50, 6.00, 5.50, 5.00, NOW(), NOW(), 1, 1),
(3, '大型厢式货车', '4.2*2.0*2.0m', '载货量大，适合物品较多的家庭或小型企业搬迁', 200.00, 10.00, 9.00, 8.00, 7.00, 6.00, NOW(), NOW(), 1, 1);

-- ----------------------------
-- Data for table service_category
-- ----------------------------
INSERT INTO `service_category` (`id`, `type_name`, `description`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, '标准搬家', '提供基础的物品搬运和运输服务', NOW(), NOW(), 1, 1),
(2, '精品搬家', '在标准搬家基础上，提供物品打包、还原等增值服务', NOW(), NOW(), 1, 1),
(3, '单件快送', '针对少量物品或单件家具的快速搬运服务', NOW(), NOW(), 1, 1),
(4, '企业搬迁', '专业的办公室、库房等企业单位搬迁服务', NOW(), NOW(), 1, 1);

-- ----------------------------
-- Data for table service
-- ----------------------------
INSERT INTO `service` (`id`, `category_id`, `truck_type_id`, `service_name`, `short_description`, `loading_capacity_description`, `default_price`, `average_rating`, `rating_count`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, 1, 1, '标准搬家 (小型面包车)', '经济型小型搬家服务', '约可装载10-15个大纸箱、一台洗衣机', 80.00, 4.80, 10, NOW(), NOW(), 1, 1),
(2, 1, 2, '标准搬家 (中型厢式货车)', '适合普通家庭的标准搬家服务', '约可装载床、沙发、冰箱、洗衣机及20个纸箱', 120.00, 4.50, 25, NOW(), NOW(), 1, 1),
(3, 2, 2, '精品搬家 (中型厢式货车)', '提供打包和基础还原服务的家庭搬家', '同中型厢式货车容量，含打包材料和人工', 120.00, 4.90, 15, NOW(), NOW(), 1, 1);

-- ----------------------------
-- Data for table driver
-- ----------------------------
INSERT INTO `driver` (`id`, `username`, `password`, `name`, `gender`, `phone`, `id_card`, `driving_years`, `photo_url`, `is_banned`, `create_time`, `update_time`) VALUES
(1, 'driver1', '81dc9bdb52d04dc20036dbd8313ed055', '王五', '男', '13555556666', '320501198808087890', 5, '/uploads/driver/driver1_photo.jpg', 0, NOW(), NOW()),
(2, 'driver2', '81dc9bdb52d04dc20036dbd8313ed055', '赵六', '男', '13666667777', '510701199209091234', 10, '/uploads/driver/driver2_photo.png', 0, NOW(), NOW());

-- ----------------------------
-- Data for table mover
-- ----------------------------
INSERT INTO `mover` (`id`, `username`, `password`, `name`, `gender`, `phone`, `id_card`, `photo_url`, `is_banned`, `create_time`, `update_time`) VALUES
(1, 'mover1', '81dc9bdb52d04dc20036dbd8313ed055', '孙七', '男', '13777778888', '420101199510105678', '/uploads/mover/mover1_photo.jpg', 0, NOW(), NOW()),
(2, 'mover2', '81dc9bdb52d04dc20036dbd8313ed055', '周八', '男', '13000009999', '610801199811112345', '/uploads/mover/mover2_photo.png', 0, NOW(), NOW());

-- ----------------------------
-- Data for table driver_truck_type
-- ----------------------------
INSERT INTO `driver_truck_type` (`driver_id`, `truck_type_id`, `create_time`, `update_time`) VALUES
(1, 1, NOW(), NOW()),
(1, 2, NOW(), NOW()), -- 司机1可以开小型和中型
(2, 2, NOW(), NOW()),
(2, 3, NOW(), NOW()); -- 司机2可以开中型和大型

-- ----------------------------
-- Data for table vehicle
-- ----------------------------
INSERT INTO `vehicle` (`id`, `driver_id`, `truck_type_id`, `license_plate_number`, `vehicle_brand`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, 1, 1, '沪A12345', '五菱宏光', NOW(), NOW(), 1, 1),
(2, 2, 2, '京B98765', '福田时代', NOW(), NOW(), 1, 1);

-- ----------------------------
-- Data for table moving_order
-- ----------------------------
INSERT INTO `moving_order` (`id`, `customer_id`, `order_number`, `service_id`, `truck_type_id`, `driver_id`, `vehicle_id`, `order_status`, `reservation_time`, `moving_origin`, `moving_destination`, `moving_price`, `is_paid`, `payment_time`, `moving_start_time`, `moving_end_time`, `number_of_helpers`, `notes`, `create_time`, `update_time`) VALUES
(1, 1, 'MO20250418001', 1, 1, 1, 1, 4, '2025-04-18 09:00:00', '上海市浦东新区', '上海市徐汇区', 180.00, 1, '2025-04-18 08:30:00', '2025-04-18 09:30:00', '2025-04-18 11:30:00', 1, '物品不多', NOW(), NOW()),
(2, 2, 'MO20250418002', 2, 2, 2, 2, 4, '2025-04-18 14:00:00', '北京市朝阳区', '北京市海淀区', 320.00, 1, '2025-04-18 13:30:00', '2025-04-18 14:30:00', '2025-04-18 17:00:00', 2, '有钢琴', NOW(), NOW());

-- ----------------------------
-- Data for table order_mover
-- ----------------------------
INSERT INTO `order_mover` (`id`, `order_id`, `mover_id`) VALUES
(1, 1, 1), -- 订单1由搬运工1协助
(2, 2, 1), -- 订单2由搬运工1协助
(3, 2, 2); -- 订单2由搬运工2协助

-- ----------------------------
-- Data for table rating
-- ----------------------------
-- 评价订单1 (服务1, 司机1)
INSERT INTO `rating` (`id`, `order_id`, `customer_id`, `ratee_id`, `rating_type`, `rating_value`, `comment`) VALUES
(1, 1, 1, 1, 'service', 5, '服务非常满意，师傅很准时！'), -- 评价服务项1
(2, 1, 1, 1, 'driver', 5, '司机王师傅态度很好，开车稳当。'), -- 评价司机1
(3, 1, 1, 1, 'mover', 4, '搬运工孙七很辛苦，动作麻利。'); -- 评价搬运工1

-- 评价订单2 (服务2, 司机2)
INSERT INTO `rating` (`id`, `order_id`, `customer_id`, `ratee_id`, `rating_type`, `rating_value`, `comment`) VALUES
(4, 2, 2, 2, 'service', 4, '标准搬家服务符合预期，钢琴搬运也顺利。'), -- 评价服务项2
(5, 2, 2, 2, 'driver', 4, '司机赵师傅路线熟悉，安全送达。'); -- 评价司机2
-- 注意：这里只模拟了对服务和司机的评分，实际可能还会对多个搬运工进行评分

-- ----------------------------
-- Data for table system_log
-- ----------------------------
INSERT INTO `system_log` (`id`, `log_time`, `user_id`, `user_type`, `operation`, `details`, `ip_address`) VALUES
(1, '2025-04-18 10:00:00', 1, 'customer', '创建订单', '用户张三创建订单 MO20250418001', '192.168.1.100'),
(2, '2025-04-18 11:00:00', 1, 'driver', '接单', '司机王五接单 MO20250418001', '192.168.1.101'),
(3, '2025-04-18 12:00:00', 1, 'admin', '修改配置', '管理员 admin1 修改网站名称为 轻松搬家网', '172.16.0.50');

-- ----------------------------
-- Data for table moving_news
-- ----------------------------
INSERT INTO `moving_news` (`id`, `title`, `content`, `publish_date`, `is_published`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, '搬家小贴士：如何打包易碎物品', '详细讲解了玻璃、陶瓷等易碎品的打包技巧...', '2025-04-15', 1, NOW(), NOW(), 1, 1),
(2, '平台服务升级公告', '我们更新了司机端App，提升接单效率...', '2025-04-16', 1, NOW(), NOW(), 1, 1);

-- ----------------------------
-- Data for table moving_tips
-- ----------------------------
INSERT INTO `moving_tips` (`id`, `title`, `content`, `category`, `is_published`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, '家具拆装注意事项', '关于如何安全拆卸和安装常见家具的指南...', '打包技巧', 1, NOW(), NOW(), 1, 1),
(2, '搬家当天准备清单', '搬家当天需要检查和携带的重要物品清单...', '注意事项', 1, NOW(), NOW(), 1, 1);