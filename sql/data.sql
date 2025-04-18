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

INSERT INTO `moving_news` (`id`, `title`, `content`, `publish_date`, `is_published`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, 'XX搬家公司正式成立五周年！感恩回馈新老客户', '尊敬的客户，感谢您五年来的支持与信任！XX搬家公司今日迎来成立五周年纪念日。为回馈广大新老客户，我们特别推出一系列优惠活动，包括搬家费用折扣、免费包装材料赠送等。未来，我们将继续秉承“专业、高效、安全”的服务理念，为您提供更优质的搬家体验。', '2025-03-15', 1, '2025-03-15 09:00:00', '2025-03-15 09:00:00', 1, 1),
(2, 'XX搬家公司荣获“年度最佳服务商”称号', '热烈祝贺！在近日举行的[行业名称]年度评选中，XX搬家公司凭借卓越的服务质量和良好的用户口碑，荣获“年度最佳服务商”称号。这是对我们团队辛勤付出的肯定，也是我们不断前进的动力。我们将以此为契机，继续提升服务水平，为客户创造更大的价值。', '2024-11-20', 1, '2024-11-20 10:30:00', '2024-11-20 11:00:00', 1, 1),
(3, 'XX搬家公司推出全新“无忧打包”服务', '为了更好地满足客户的搬家需求，XX搬家公司正式推出全新“无忧打包”服务。该服务由我们专业的打包团队负责，提供从物品分类、包装、到运输的全程服务，让您无需为打包烦恼，轻松实现搬家。详情请咨询我们的客服人员。', '2025-01-10', 1, '2025-01-10 14:00:00', '2025-01-10 14:30:00', 1, 1),
(4, 'XX搬家公司扩大服务范围，新增[新服务城市]线路', '好消息！XX搬家公司为了更好地服务更多客户，即日起正式开通[新服务城市]的搬家线路。无论您是个人搬家还是企业搬迁，我们都将以专业的服务和合理的价格，为您提供便捷高效的搬家解决方案。欢迎致电咨询或在线预约。', '2024-09-01', 1, '2024-09-01 16:00:00', '2024-09-02 08:00:00', 1, 1),
(5, 'XX搬家公司积极响应环保号召，启用环保包装材料', 'XX搬家公司一直致力于可持续发展。为了减少搬家过程中对环境的影响，我们已全面启用环保、可回收的包装材料。我们希望通过自身的努力，为绿色环保贡献一份力量，同时也为客户提供更健康、更放心的搬家服务。', '2025-04-05', 1, '2025-04-05 11:30:00', '2025-04-05 12:00:00', 1, 1);

INSERT INTO `moving_tips` (`id`, `title`, `content`, `category`, `is_published`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, '搬家前一周必备清单，让您有条不紊', '搬家前一周是准备工作的关键时期。建议您：\n1. 开始整理和打包非必需品。\n2. 联系物业或房东办理相关手续。\n3. 预约搬家公司并确认搬家时间。\n4. 清理冰箱，处理不需要的物品。\n5. 准备好搬家当天的必需品（如水、食物、常用药品等）。', '搬家准备', 1, '2025-03-20 10:00:00', '2025-03-20 10:00:00', 1, 1),
(2, '打包技巧：易碎物品如何安全包装？', '打包易碎物品需要格外小心。以下是一些建议：\n1. 使用气泡膜或泡沫板将物品包裹严实。\n2. 在纸箱底部铺上一层缓冲材料（如旧报纸、泡沫颗粒）。\n3. 将包裹好的易碎物品放入纸箱，周围用填充物填满，防止晃动。\n4. 在纸箱外清楚标注“易碎品，小心轻放”。', '打包技巧', 1, '2025-03-22 14:30:00', '2025-03-22 14:30:00', 1, 1),
(3, '搬家当天注意事项，确保顺利进行', '搬家当天需要注意以下事项：\n1. 提前到达搬家地点，与搬家师傅做好沟通。\n2. 清点搬运物品，确认数量和状态。\n3. 在新家指挥物品摆放，确保符合您的要求。\n4. 检查所有物品是否安全送达，如有遗失或损坏及时与搬家公司沟通。\n5. 支付搬家费用并索要发票。', '搬家当天', 1, '2025-03-25 09:30:00', '2025-03-25 09:30:00', 1, 1),
(4, '如何选择合适的搬家公司？这几点很重要', '选择一家靠谱的搬家公司至关重要。建议您考虑以下几点：\n1. 查看搬家公司的资质和口碑。\n2. 比较不同公司的报价和服务内容。\n3. 确认是否有正规的搬家合同。\n4. 了解搬家公司的保险条款。\n5. 可以咨询身边的朋友或同事的推荐。', '选择搬家公司', 1, '2025-03-28 16:00:00', '2025-03-28 16:00:00', 1, 1),
(5, '搬家后别忘了做这几件事，快速适应新家', '搬家完成后，还有一些事情需要您处理：\n1. 清理新家，进行全面的清洁。\n2. 检查水电煤气等是否正常。\n3. 整理 unpacked 的物品，将它们归位。\n4. 更换门锁（如果需要）。\n5. 熟悉新家的周边环境。', '搬家后', 1, '2025-04-01 11:00:00', '2025-04-01 11:00:00', 1, 1);

-- 插入管理员数据
-- 插入模拟数据
INSERT INTO `admin` (`username`, `password`, `name`, `photo_url`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('superadmin', '81dc9bdb52d04dc20036dbd8313ed055', '超级管理员', '/uploads/admin/superadmin_avatar.jpg', NOW(), NOW(), 1, 1),
('admin_zhang', '81dc9bdb52d04dc20036dbd8313ed055', '张伟', '/uploads/admin/zhangwei_photo.png', NOW(), NOW(), 1, 1),
('admin_li', '81dc9bdb52d04dc20036dbd8313ed055', '李娜', '/uploads/admin/lina_pic.jpeg', NOW(), NOW(), 1, 1),
('content_editor', '81dc9bdb52d04dc20036dbd8313ed055', '王编辑', '/uploads/admin/wangeditor_img.webp', NOW(), NOW(), 1, 1);

-- 您可以根据需要添加更多数据
-- 插入配置数据
INSERT INTO `configuration` (`name`, `value`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
('website_name', '轻松搬家网', '2025-04-17 10:10:00', '2025-04-17 10:10:00', 1, 1),
('default_currency', 'CNY', '2025-04-17 10:12:00', '2025-04-17 10:12:00', 1, 1);

-- 插入消费者数据
INSERT INTO `customer` (`username`, `password`, `name`, `gender`, `phone`, `email`, `id_card`, `family_phone`, `photo_url`, `is_banned`, `create_time`, `update_time`) VALUES
('customer1', '81dc9bdb52d04dc20036dbd8313ed055', '张三', '男', '13812345678', 'zhangsan@example.com', '310101199001011234', '021-1234567', NULL, 0, '2025-04-17 10:15:00', '2025-04-17 10:15:00'),
('customer2', '81dc9bdb52d04dc20036dbd8313ed055', '李四', '女', '13987654321', 'lisi@example.com', '440301198505054321', '010-9876543', NULL, 0, '2025-04-17 10:18:00', '2025-04-17 10:18:00');

-- 插入司机数据
INSERT INTO `driver` (`username`, `password`, `name`, `gender`, `phone`, `id_card`, `driving_years`, `photo_url`, `is_banned`, `create_time`, `update_time`) VALUES
('driver1', '81dc9bdb52d04dc20036dbd8313ed055', '王五', '男', '13555556666', '320501198808087890', 5, NULL, 0, '2025-04-17 10:22:00', '2025-04-17 10:22:00'),
('driver2', '81dc9bdb52d04dc20036dbd8313ed055', '赵六', '男', '13666667777', '510701199209091234', 10, NULL, 0, '2025-04-17 10:25:00', '2025-04-17 10:25:00');

-- 插入搬运工人数据
INSERT INTO `mover` (`username`, `password`, `name`, `gender`, `phone`, `id_card`, `photo_url`, `is_banned`, `create_time`, `update_time`) VALUES
('mover1', '81dc9bdb52d04dc20036dbd8313ed055', '孙七', '男', '13777778888', '420101199510105678', NULL, 0, '2025-04-17 10:28:00', '2025-04-17 10:28:00'),
('mover2', '81dc9bdb52d04dc20036dbd8313ed055', '周八', '男', '13000009999', '610801199811112345', NULL, 0, '2025-04-17 10:31:00', '2025-04-17 10:31:00');

-- 插入服务类型数据
INSERT INTO `service_category` (`type_name`, `description`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
('标准搬家', '适用于普通家庭或个人搬家', '2025-04-17 10:35:00', '2025-04-17 10:35:00', 1, 1),
('长途搬家', '跨城市或地区的搬家服务', '2025-04-17 10:38:00', '2025-04-17 10:38:00', 1, 1);

-- 插入货车类型数据
INSERT INTO `truck_type` (`type_name`, `capacity`, `description`, `base_fare`, `price_per_km_tier1`, `price_per_km_tier2`, `price_per_km_tier3`, `price_per_km_tier4`, `price_per_km_tier5`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
('小型面包车', '1.8*1.3*1.1m', '适合少量物品搬运', 80.00, 5.00, 4.50, 4.00, 3.50, 3.00, '2025-04-17 10:42:00', '2025-04-17 10:42:00', 1, 1),
('中型厢式货车', '2.5*1.8*1.6m', '适合普通家庭搬运', 120.00, 7.00, 6.50, 6.00, 5.50, 5.00, '2025-04-17 10:45:00', '2025-04-17 10:45:00', 1, 1);

-- 插入服务项数据
INSERT INTO `service` (`category_id`, `truck_type_id`, `service_name`, `short_description`, `loading_capacity_description`, `default_price`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, 1, '小型搬家', '经济型小型搬家服务', '可装载少量家具和行李', 150.00, '2025-04-17 10:50:00', '2025-04-17 10:50:00', 1, 1),
(1, 2, '标准搬家', '适合2-3人家庭的搬家服务', '可装载普通家庭的家具和电器', 250.00, '2025-04-17 10:53:00', '2025-04-17 10:53:00', 1, 1),
(2, 2, '同城长途搬家', '同城市内的较远距离搬家', '根据实际距离和物品数量报价', 300.00, '2025-04-17 10:56:00', '2025-04-17 10:56:00', 1, 1);

-- 插入车辆信息数据
INSERT INTO `vehicle` (`driver_id`, `truck_type_id`, `license_plate_number`, `vehicle_brand`, `create_time`, `update_time`, `create_user`, `update_user`) VALUES
(1, 1, '沪A12345', '五菱宏光', '2025-04-17 11:00:00', '2025-04-17 11:00:00', 1, 1),
(2, 2, '京B98765', '福田时代', '2025-04-17 11:03:00', '2025-04-17 11:03:00', 1, 1);

-- 插入搬家订单数据
INSERT INTO `moving_order` (`customer_id`, `order_number`, `service_id`, `truck_type_id`, `driver_id`, `vehicle_id`, `order_status`, `reservation_time`, `moving_origin`, `moving_destination`, `moving_price`, `is_paid`, `payment_time`, `moving_start_time`, `moving_end_time`, `number_of_helpers`, `notes`, `create_time`, `update_time`) VALUES
(1, 'MO20250417001', 1, 1, 1, 1, 4, '2025-04-18 09:00:00', '上海市浦东新区', '上海市徐汇区', 180.00, 1, '2025-04-17 16:00:00', '2025-04-18 09:30:00', '2025-04-18 11:30:00', 1, '无', '2025-04-17 11:10:00', '2025-04-17 11:10:00'),
(2, 'MO20250417002', 2, 2, 2, 2, 3, '2025-04-19 14:00:00', '北京市朝阳区', '北京市海淀区', 320.00, 0, NULL, '2025-04-19 14:30:00', NULL, 2, '需要搬运钢琴', '2025-04-17 11:15:00', '2025-04-17 11:15:00');

-- 插入订单搬运工人关联数据
INSERT INTO `order_mover` (`order_id`, `mover_id`) VALUES
(1, 1),
(2, 1),
(2, 2);

-- 插入评分数据
INSERT INTO `rating` (`order_id`, `customer_id`, `ratee_id`, `rating_type`, `rating_value`, `comment`) VALUES
(1, 1, 1, '司机', 5, '司机师傅很专业，服务态度好'),
(1, 1, 1, '搬运工人', 4, '搬运速度很快，物品保护得不错'),
(2, 2, 2, '司机', 4, '司机准时到达，路线熟悉'),
(2, 2, 1, '搬运工人', 5, '非常给力，很细心');

-- 插入司机货车类型关联数据
INSERT INTO `driver_truck_type` (`driver_id`, `truck_type_id`) VALUES
(1, 1),
(2, 2),
(2, 1);

-- 插入系统日志数据
INSERT INTO `system_log` (`log_time`, `user_id`, `user_type`, `operation`, `details`, `ip_address`) VALUES
('2025-04-17 11:20:00', 1, 'customer', '创建订单', '用户张三创建了一个从上海市浦东新区到上海市徐汇区的搬家订单，订单号：MO20250417001', '192.168.1.100'),
('2025-04-17 11:25:00', 1, 'driver', '接单', '司机王五接了订单号为MO20250417001的搬家订单', '192.168.1.101'),
('2025-04-17 11:30:00', 2, 'customer', '创建订单', '用户李四创建了一个从北京市朝阳区到北京市海淀区的搬家订单，订单号：MO20250417002', '10.0.1.50');