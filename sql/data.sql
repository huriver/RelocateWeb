-- 创建数据库 (如果不存在则创建)
CREATE DATABASE IF NOT EXISTS relocateWeb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE relocateWeb;

-- 基础配置表
CREATE TABLE IF NOT EXISTS admin_user (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    username VARCHAR(100) DEFAULT NULL COMMENT '用户名',
    password VARCHAR(100) DEFAULT NULL COMMENT '密码',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='管理员用户表';

-- 服务相关表
CREATE TABLE IF NOT EXISTS service_category (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    type_name VARCHAR(200) NOT NULL COMMENT '服务类型名称',
    description VARCHAR(500) DEFAULT NULL COMMENT '服务类型描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='服务类型表';

CREATE TABLE IF NOT EXISTS truck_type (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    type_name VARCHAR(200) DEFAULT NULL COMMENT '货车类型名称',
    capacity VARCHAR(200) DEFAULT NULL COMMENT '车厢长宽高（例如：1.8*1.3*1.1m）',
    description VARCHAR(500) DEFAULT NULL COMMENT '货车类型描述',
    base_fare DECIMAL(10, 2) DEFAULT NULL COMMENT '起步价（5公里以内）',
    price_per_km_tier1 DECIMAL(10, 2) DEFAULT NULL COMMENT '5-25公里每公里价格',
    price_per_km_tier2 DECIMAL(10, 2) DEFAULT NULL COMMENT '25-30公里每公里价格',
    price_per_km_tier3 DECIMAL(10, 2) DEFAULT NULL COMMENT '30-50公里每公里价格',
    price_per_km_tier4 DECIMAL(10, 2) DEFAULT NULL COMMENT '50-80公里每公里价格',
    price_per_km_tier5 DECIMAL(10, 2) DEFAULT NULL COMMENT '超过80公里每公里价格',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='货车类型表';

CREATE TABLE IF NOT EXISTS service (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    category_id BIGINT(20) NOT NULL COMMENT '服务类型ID，外键，关联 service_category',
    truck_type_id BIGINT(20) NOT NULL COMMENT '货车类型ID，外键，关联 truck_type',
    service_name VARCHAR(200) NOT NULL COMMENT '服务项名称',
    short_description TEXT DEFAULT NULL COMMENT '服务项简短描述',
    loading_capacity_description TEXT DEFAULT NULL COMMENT '装载能力详细说明',
    default_price DECIMAL(10, 2) DEFAULT NULL COMMENT '起始价格（对应货车类型的5公里内价格）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='服务项表';

-- 用户表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    account VARCHAR(200) NOT NULL UNIQUE COMMENT '账号，唯一',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    name VARCHAR(200) DEFAULT NULL COMMENT '姓名',
    gender VARCHAR(20) DEFAULT NULL COMMENT '性别',
    phone VARCHAR(200) DEFAULT NULL COMMENT '手机号码',
    id_card VARCHAR(200) DEFAULT NULL COMMENT '身份证号码',
    email VARCHAR(200) DEFAULT NULL COMMENT '邮箱',
    family_phone VARCHAR(200) DEFAULT NULL COMMENT '家属电话',
    photo_url VARCHAR(200) DEFAULT NULL COMMENT '照片URL',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消费者表';

CREATE TABLE IF NOT EXISTS mover (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    account VARCHAR(200) NOT NULL UNIQUE COMMENT '账号，唯一',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    name VARCHAR(200) DEFAULT NULL COMMENT '姓名',
    gender VARCHAR(20) DEFAULT NULL COMMENT '性别',
    phone VARCHAR(200) DEFAULT NULL COMMENT '手机号码',
    id_card VARCHAR(200) DEFAULT NULL COMMENT '身份证号码',
    photo_url VARCHAR(200) DEFAULT NULL COMMENT '照片URL',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='搬运工人表';

CREATE TABLE IF NOT EXISTS driver (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    account VARCHAR(200) NOT NULL UNIQUE COMMENT '账号，唯一',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    name VARCHAR(200) DEFAULT NULL COMMENT '姓名',
    gender VARCHAR(20) DEFAULT NULL COMMENT '性别',
    phone VARCHAR(200) DEFAULT NULL COMMENT '手机号码',
    id_card VARCHAR(200) DEFAULT NULL COMMENT '身份证号码',
    driving_years INT DEFAULT NULL COMMENT '驾龄（年）',
    photo_url VARCHAR(200) DEFAULT NULL COMMENT '照片URL',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='司机表';

-- 车辆信息表
CREATE TABLE IF NOT EXISTS vehicle (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    driver_id BIGINT(20) NOT NULL COMMENT '司机ID，外键，关联 driver 表的 id',
    truck_type_id BIGINT(20) NOT NULL COMMENT '货车类型ID，外键，关联 truck_type 表的 id',
    license_plate_number VARCHAR(200) NOT NULL UNIQUE COMMENT '车牌号，唯一',
    vehicle_brand VARCHAR(200) DEFAULT NULL COMMENT '车辆品牌',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='车辆信息表';

-- 搬家订单表
CREATE TABLE IF NOT EXISTS moving_order (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    customer_id BIGINT(20) NOT NULL COMMENT '客户ID，外键',
    service_id BIGINT(20) NOT NULL COMMENT '服务项ID，外键，关联 service',
    truck_type_id BIGINT(20) DEFAULT NULL COMMENT '货车类型ID，外键',
    driver_id BIGINT(20) DEFAULT NULL COMMENT '司机ID，外键',
    vehicle_id BIGINT(20) DEFAULT NULL COMMENT '执行订单的车辆ID，外键，关联 vehicle',
    order_status INT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待接单，1-司机已接单，等待搬运工人，2-已接单，3-进行中，4-已完成，5-已取消',
    reservation_time DATETIME DEFAULT NULL COMMENT '预约时间',
    moving_origin VARCHAR(200) DEFAULT NULL COMMENT '搬家起点',
    moving_destination VARCHAR(200) DEFAULT NULL COMMENT '搬家目的地',
    moving_price DECIMAL(10, 2) DEFAULT NULL COMMENT '搬家价格',
    is_paid INT DEFAULT 0 COMMENT '是否支付：0-未支付，1-已支付，2-已退款',
    payment_time DATETIME DEFAULT NULL COMMENT '支付时间',
    moving_start_time DATETIME DEFAULT NULL COMMENT '搬家开始时间',
    moving_end_time DATETIME DEFAULT NULL COMMENT '搬家结束时间',
    number_of_helpers INT DEFAULT NULL COMMENT '用户选择的搬运工人数量',
    notes TEXT DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间，默认当前时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='搬家订单表';

-- 关联表
CREATE TABLE IF NOT EXISTS order_mover (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    order_id BIGINT(20) NOT NULL COMMENT '订单ID，外键，关联 moving_order',
    mover_id BIGINT(20) NOT NULL COMMENT '搬运工人ID，外键，关联 mover',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单搬运工人关联表';

CREATE TABLE IF NOT EXISTS driver_truck_type (
    driver_id BIGINT(20) NOT NULL COMMENT '司机ID，外键，关联 driver 表的 id',
    truck_type_id BIGINT(20) NOT NULL COMMENT '货车类型ID，外键，关联 truck_type 表的 id',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (driver_id, truck_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录司机可以驾驶的货车类型';

-- 支持表
CREATE TABLE IF NOT EXISTS rating (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    order_id BIGINT(20) NOT NULL COMMENT '订单ID，外键',
    rater_id BIGINT(20) NOT NULL COMMENT '评分者ID（通常是 customer.id）',
    ratee_id BIGINT(20) NOT NULL COMMENT '被评分者ID（可能是 driver.id 或 mover.id）',
    rating_type VARCHAR(50) NOT NULL COMMENT '评分类型（例如：司机，搬运工人，服务）',
    rating_value INT NOT NULL COMMENT '评分值（例如：1-5星）',
    comment TEXT DEFAULT NULL COMMENT '评价内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评分时间，默认当前时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='评分表';

CREATE TABLE IF NOT EXISTS configuration (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    name VARCHAR(100) DEFAULT NULL COMMENT '配置参数名称',
    value VARCHAR(100) DEFAULT NULL COMMENT '配置参数值',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置表';

CREATE TABLE IF NOT EXISTS system_log (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
    log_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志时间，默认当前时间',
    user_id BIGINT(20) DEFAULT NULL COMMENT '操作用户ID',
    user_type VARCHAR(50) DEFAULT NULL COMMENT '用户类型（例如：customer, mover, driver, admin）',
    operation VARCHAR(200) DEFAULT NULL COMMENT '操作内容',
    details TEXT DEFAULT NULL COMMENT '操作详情',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统日志表';

-- 插入模拟数据

-- 管理员用户
INSERT INTO admin_user (username, password, create_time) VALUES
('admin', 'password', NOW()),
('superadmin', 'securepassword', NOW());

-- 服务类型
INSERT INTO service_category (type_name, description) VALUES
('个人搬家', '适用于个人或家庭的搬家服务'),
('公司搬迁', '适用于公司或企业的整体搬迁服务');

-- 货车类型
INSERT INTO truck_type (type_name, capacity, base_fare, price_per_km_tier1, price_per_km_tier2, price_per_km_tier3, price_per_km_tier4, price_per_km_tier5, description) VALUES
('小型面包车', '1.8*1.3*1.1m', 80.00, 10.00, 9.00, 8.00, 7.00, 6.00, '适合少量物品的搬运'),
('中型厢式货车', '2.5*1.8*1.6m', 120.00, 15.00, 14.00, 13.00, 12.00, 11.00, '适合普通家庭的搬运'),
('大型厢式货车', '3.5*2.0*1.8m', 180.00, 20.00, 19.00, 18.00, 17.00, 16.00, '适合大型家庭或小型企业的搬运');

-- 服务项
INSERT INTO service (category_id, truck_type_id, service_name, short_description, loading_capacity_description, default_price) VALUES
(1, 1, '经济型搬家', '小型搬家，经济实惠', '可装载少量家具和行李', 80.00),
(1, 2, '标准型搬家', '家庭搬家，服务周到', '可装载普通家庭的家具和电器', 120.00),
(2, 3, '企业搬迁', '专业企业搬迁服务', '可装载办公设备和文件', 180.00);

-- 客户
INSERT INTO customer (account, password, name, gender, phone, email) VALUES
('zhangsan', '123456', '张三', '男', '13812345678', 'zhangsan@example.com'),
('lisi', 'abcdef', '李四', '女', '13987654321', 'lisi@example.com');

-- 搬运工人
INSERT INTO mover (account, password, name, gender, phone) VALUES
('wangwu', 'qwerty', '王五', '男', '13511112222'),
('zhaoliu', 'asdfgh', '赵六', '男', '13633334444');

-- 司机
INSERT INTO driver (account, password, name, gender, phone, driving_years) VALUES
('xiaoming', 'zxcvbn', '小明', '男', '13755556666', 5),
('xiaohong', 'poiuyt', '小红', '女', '13477778888', 10);

-- 车辆信息
INSERT INTO vehicle (driver_id, truck_type_id, license_plate_number, vehicle_brand) VALUES
(1, 2, '苏A12345', '东风'),
(2, 3, '苏B67890', '解放');

-- 搬家订单
INSERT INTO moving_order (customer_id, service_id, truck_type_id, driver_id, vehicle_id, order_status, reservation_time, moving_origin, moving_destination, moving_price, is_paid, moving_start_time, moving_end_time, number_of_helpers, notes) VALUES
(1, 2, 2, 1, 1, 4, '2025-04-20 10:00:00', '江苏省南京市鼓楼区某小区', '江苏省南京市雨花台区某小区', 200.00, 1, '2025-04-20 10:30:00', '2025-04-20 12:30:00', 2, '无特殊要求'),
(2, 1, 1, NULL, NULL, 0, '2025-04-25 14:00:00', '安徽省合肥市蜀山区某小区', '安徽省合肥市包河区某小区', 100.00, 0, NULL, NULL, 1, '需要搬运冰箱');

-- 订单搬运工人关联
INSERT INTO order_mover (order_id, mover_id) VALUES
(1, 1),
(1, 2);

-- 司机可以驾驶的货车类型
INSERT INTO driver_truck_type (driver_id, truck_type_id) VALUES
(1, 2),
(2, 3);

-- 评分
INSERT INTO rating (order_id, rater_id, ratee_id, rating_type, rating_value, comment) VALUES
(1, 1, 1, '司机', 5, '司机师傅很专业'),
(1, 1, 1, '搬运工人', 4, '搬运师傅很给力');

-- 配置
INSERT INTO configuration (name, value) VALUES
('base_distance', '5'),
('price_unit', '元');

-- 系统日志
INSERT INTO system_log (user_type, operation, details, ip_address) VALUES
('admin', '创建管理员用户', '创建了用户名为admin的管理员', '127.0.0.1'),
('customer', '提交订单', '用户张三提交了一个搬家订单', '192.168.1.100');