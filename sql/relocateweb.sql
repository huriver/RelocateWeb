/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80012 (8.0.12)
 Source Host           : localhost:3306
 Source Schema         : relocateweb

 Target Server Type    : MySQL
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 15/04/2025 13:44:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `username` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '管理员姓名',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '管理员用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES (1, 'admin', 'password', NULL, '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `admin_user` VALUES (2, 'superadmin', 'securepassword', NULL, '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for configuration
-- ----------------------------
DROP TABLE IF EXISTS `configuration`;
CREATE TABLE `configuration`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置参数名称',
  `value` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置参数值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of configuration
-- ----------------------------
INSERT INTO `configuration` VALUES (1, 'base_distance', '5', '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `configuration` VALUES (2, 'price_unit', '元', '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `username` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户账号，唯一',
  `password` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `gender` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `id_card` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号码',
  `email` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `family_phone` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '家属电话',
  `photo_url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '照片URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account`(`username` ASC) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '消费者表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer
-- ----------------------------
INSERT INTO `customer` VALUES (1, 'zhangsan', '123456', '张三', '男', '13812345678', NULL, 'zhangsan@example.com', NULL, NULL, '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `customer` VALUES (2, 'lisi', 'abcdef', '李四', '女', '13987654321', NULL, 'lisi@example.com', NULL, NULL, '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for driver
-- ----------------------------
DROP TABLE IF EXISTS `driver`;
CREATE TABLE `driver`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `username` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户账号，唯一',
  `password` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `gender` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `id_card` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号码',
  `driving_years` int(11) NULL DEFAULT NULL COMMENT '驾龄（年）',
  `photo_url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '照片URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account`(`username` ASC) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '司机表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of driver
-- ----------------------------
INSERT INTO `driver` VALUES (1, 'xiaoming', 'zxcvbn', '小明', '男', '13755556666', NULL, 5, NULL, '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `driver` VALUES (2, 'xiaohong', 'poiuyt', '小红', '女', '13477778888', NULL, 10, NULL, '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for driver_truck_type
-- ----------------------------
DROP TABLE IF EXISTS `driver_truck_type`;
CREATE TABLE `driver_truck_type`  (
  `driver_id` bigint(20) NOT NULL COMMENT '司机ID，外键，关联 driver 表的 id',
  `truck_type_id` bigint(20) NOT NULL COMMENT '货车类型ID，外键，关联 truck_type 表的 id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`driver_id`, `truck_type_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '记录司机可以驾驶的货车类型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of driver_truck_type
-- ----------------------------
INSERT INTO `driver_truck_type` VALUES (1, 2, '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `driver_truck_type` VALUES (2, 3, '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for mover
-- ----------------------------
DROP TABLE IF EXISTS `mover`;
CREATE TABLE `mover`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `username` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户账号，唯一',
  `password` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `gender` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `id_card` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号码',
  `photo_url` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '照片URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account`(`username` ASC) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '搬运工人表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mover
-- ----------------------------
INSERT INTO `mover` VALUES (1, 'wangwu', 'qwerty', '王五', '男', '13511112222', NULL, NULL, '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `mover` VALUES (2, 'zhaoliu', 'asdfgh', '赵六', '男', '13633334444', NULL, NULL, '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for moving_order
-- ----------------------------
DROP TABLE IF EXISTS `moving_order`;
CREATE TABLE `moving_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID，外键',
  `service_id` bigint(20) NOT NULL COMMENT '服务项ID，外键，关联 service',
  `truck_type_id` bigint(20) NULL DEFAULT NULL COMMENT '货车类型ID，外键',
  `driver_id` bigint(20) NULL DEFAULT NULL COMMENT '司机ID，外键',
  `vehicle_id` bigint(20) NULL DEFAULT NULL COMMENT '执行订单的车辆ID，外键，关联 vehicle',
  `order_status` int(11) NOT NULL DEFAULT 0 COMMENT '订单状态：0-待接单，1-司机已接单，等待搬运工人，2-已接单，3-进行中，4-已完成，5-已取消',
  `reservation_time` datetime NULL DEFAULT NULL COMMENT '预约时间',
  `moving_origin` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '搬家起点',
  `moving_destination` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '搬家目的地',
  `moving_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '搬家价格',
  `is_paid` int(11) NULL DEFAULT 0 COMMENT '是否支付：0-未支付，1-已支付，2-已退款',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `moving_start_time` datetime NULL DEFAULT NULL COMMENT '搬家开始时间',
  `moving_end_time` datetime NULL DEFAULT NULL COMMENT '搬家结束时间',
  `number_of_helpers` int(11) NULL DEFAULT NULL COMMENT '用户选择的搬运工人数量',
  `notes` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '搬家订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of moving_order
-- ----------------------------
INSERT INTO `moving_order` VALUES (1, 1, 2, 2, 1, 1, 4, '2025-04-20 10:00:00', '江苏省南京市鼓楼区某小区', '江苏省南京市雨花台区某小区', 200.00, 1, NULL, '2025-04-20 10:30:00', '2025-04-20 12:30:00', 2, '无特殊要求', '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `moving_order` VALUES (2, 2, 1, 1, NULL, NULL, 0, '2025-04-25 14:00:00', '安徽省合肥市蜀山区某小区', '安徽省合肥市包河区某小区', 100.00, 0, NULL, NULL, NULL, 1, '需要搬运冰箱', '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for order_mover
-- ----------------------------
DROP TABLE IF EXISTS `order_mover`;
CREATE TABLE `order_mover`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID，外键，关联 moving_order',
  `mover_id` bigint(20) NOT NULL COMMENT '搬运工人ID，外键，关联 mover',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单搬运工人关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_mover
-- ----------------------------
INSERT INTO `order_mover` VALUES (1, 1, 1, '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `order_mover` VALUES (2, 1, 2, '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for rating
-- ----------------------------
DROP TABLE IF EXISTS `rating`;
CREATE TABLE `rating`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID，外键',
  `rater_id` bigint(20) NOT NULL COMMENT '评分者ID（通常是 customer.id）',
  `ratee_id` bigint(20) NOT NULL COMMENT '被评分者ID（可能是 driver.id 或 mover.id）',
  `rating_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '评分类型（例如：司机，搬运工人，服务）',
  `rating_value` int(11) NOT NULL COMMENT '评分值（例如：1-5星）',
  `comment` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '评价内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评分时间，默认当前时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '评分表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rating
-- ----------------------------
INSERT INTO `rating` VALUES (1, 1, 1, 1, '司机', 5, '司机师傅很专业', '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `rating` VALUES (2, 1, 1, 1, '搬运工人', 4, '搬运师傅很给力', '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for service
-- ----------------------------
DROP TABLE IF EXISTS `service`;
CREATE TABLE `service`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `category_id` bigint(20) NOT NULL COMMENT '服务类型ID，外键，关联 service_category',
  `truck_type_id` bigint(20) NOT NULL COMMENT '货车类型ID，外键，关联 truck_type',
  `service_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务项名称',
  `short_description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '服务项简短描述',
  `loading_capacity_description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '装载能力详细说明',
  `default_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '起始价格（对应货车类型的5公里内价格）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '服务项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service
-- ----------------------------
INSERT INTO `service` VALUES (1, 1, 1, '经济型搬家', '小型搬家，经济实惠', '可装载少量家具和行李', 80.00, '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `service` VALUES (2, 1, 2, '标准型搬家', '家庭搬家，服务周到', '可装载普通家庭的家具和电器', 120.00, '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `service` VALUES (3, 2, 3, '企业搬迁', '专业企业搬迁服务', '可装载办公设备和文件', 180.00, '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for service_category
-- ----------------------------
DROP TABLE IF EXISTS `service_category`;
CREATE TABLE `service_category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `type_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务类型名称',
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务类型描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '服务类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_category
-- ----------------------------
INSERT INTO `service_category` VALUES (1, '个人搬家', '适用于个人或家庭的搬家服务', '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `service_category` VALUES (2, '公司搬迁', '适用于公司或企业的整体搬迁服务', '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for system_log
-- ----------------------------
DROP TABLE IF EXISTS `system_log`;
CREATE TABLE `system_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `log_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志时间，默认当前时间',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '操作用户ID',
  `user_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户类型（例如：customer, mover, driver, admin）',
  `operation` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作内容',
  `details` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '操作详情',
  `ip_address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_log
-- ----------------------------
INSERT INTO `system_log` VALUES (1, '2025-04-14 20:42:37', NULL, 'admin', '创建管理员用户', '创建了用户名为admin的管理员', '127.0.0.1');
INSERT INTO `system_log` VALUES (2, '2025-04-14 20:42:37', NULL, 'customer', '提交订单', '用户张三提交了一个搬家订单', '192.168.1.100');

-- ----------------------------
-- Table structure for truck_type
-- ----------------------------
DROP TABLE IF EXISTS `truck_type`;
CREATE TABLE `truck_type`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `type_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '货车类型名称',
  `capacity` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车厢长宽高（例如：1.8*1.3*1.1m）',
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '货车类型描述',
  `base_fare` decimal(10, 2) NULL DEFAULT NULL COMMENT '起步价（5公里以内）',
  `price_per_km_tier1` decimal(10, 2) NULL DEFAULT NULL COMMENT '5-25公里每公里价格',
  `price_per_km_tier2` decimal(10, 2) NULL DEFAULT NULL COMMENT '25-30公里每公里价格',
  `price_per_km_tier3` decimal(10, 2) NULL DEFAULT NULL COMMENT '30-50公里每公里价格',
  `price_per_km_tier4` decimal(10, 2) NULL DEFAULT NULL COMMENT '50-80公里每公里价格',
  `price_per_km_tier5` decimal(10, 2) NULL DEFAULT NULL COMMENT '超过80公里每公里价格',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '货车类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of truck_type
-- ----------------------------
INSERT INTO `truck_type` VALUES (1, '小型面包车', '1.8*1.3*1.1m', '适合少量物品的搬运', 80.00, 10.00, 9.00, 8.00, 7.00, 6.00, '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `truck_type` VALUES (2, '中型厢式货车', '2.5*1.8*1.6m', '适合普通家庭的搬运', 120.00, 15.00, 14.00, 13.00, 12.00, 11.00, '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `truck_type` VALUES (3, '大型厢式货车', '3.5*2.0*1.8m', '适合大型家庭或小型企业的搬运', 180.00, 20.00, 19.00, 18.00, 17.00, 16.00, '2025-04-14 20:42:37', '2025-04-14 20:42:37');

-- ----------------------------
-- Table structure for vehicle
-- ----------------------------
DROP TABLE IF EXISTS `vehicle`;
CREATE TABLE `vehicle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
  `driver_id` bigint(20) NOT NULL COMMENT '司机ID，外键，关联 driver 表的 id',
  `truck_type_id` bigint(20) NOT NULL COMMENT '货车类型ID，外键，关联 truck_type 表的 id',
  `license_plate_number` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '车牌号，唯一',
  `vehicle_brand` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '车辆品牌',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `license_plate_number`(`license_plate_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '车辆信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vehicle
-- ----------------------------
INSERT INTO `vehicle` VALUES (1, 1, 2, '苏A12345', '东风', '2025-04-14 20:42:37', '2025-04-14 20:42:37');
INSERT INTO `vehicle` VALUES (2, 2, 3, '苏B67890', '解放', '2025-04-14 20:42:37', '2025-04-14 20:42:37');

SET FOREIGN_KEY_CHECKS = 1;
