package com.***REMOVED***.service.impl;


import com.***REMOVED***.constant.*;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.*;
import com.***REMOVED***.entity.*;
import com.***REMOVED***.exception.*;
import com.***REMOVED***.mapper.*;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.PriceCalculationResult;
import com.***REMOVED***.service.CustomerService;
import com.***REMOVED***.service.EmailService;
import com.***REMOVED***.service.OrderService;
import com.***REMOVED***.utils.HttpClientUtil;
import com.***REMOVED***.vo.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderMoverMapper orderMoverMapper;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ServiceCategoryMapper serviceCategoryMapper;

    @Autowired
    private TruckTypeMapper truckTypeMapper;

    @Autowired
    private ConfigurationMapper configurationMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DriverMapper driverMapper;

    @Autowired
    private DriverTruckTypeMapper driverTruckTypeMapper;

    @Autowired
    private VehicleMapper vehicleMapper;

    @Autowired
    private RatingMapper ratingMapper;

    @Autowired
    private MoverMapper moverMapper;

    @Value("${relocate.baidu.ak}")
    private String baiduAk;

    /**
     * 估算搬家订单价格
     *
     * @param estimationDTO
     * @return
     */
    @Override
    public PriceEstimationResultVO estimatePrice(PriceEstimationDTO estimationDTO) {
        log.info("用户端估算搬家订单价格，参数：{}", estimationDTO);

        // 1. 基本输入参数校验 (保持不变)
        if (estimationDTO.getServiceId() == null ||
                estimationDTO.getOriginAddress() == null || estimationDTO.getOriginAddress().isEmpty() ||
                estimationDTO.getDestinationAddress() == null || estimationDTO.getDestinationAddress().isEmpty()) {
            throw new OrderBusinessException(MessageConstant.SERVICE_ITEM_OR_ADDRESS_EMPTY);
        }

        // 2. 调用核心计算方法进行价格估算，接收包含总价、明细和乘数的结果对象
        PriceCalculationResult priceCalculationResult = calculatePrice(
                estimationDTO.getServiceId(),
                estimationDTO.getOriginAddress(),
                estimationDTO.getDestinationAddress(),
                estimationDTO.getNumberOfHelpers()
        );


        // 3. 封装估算结果VO，设置总价、明细和乘数
        PriceEstimationResultVO priceEstimationResultVO = PriceEstimationResultVO.builder()
                .estimatedPrice(priceCalculationResult.getTotalEstimatedPrice())
                .mileageCost(priceCalculationResult.getMileageCost())
                .helperCost(priceCalculationResult.getHelperCost())
                .categoryPriceMultiplier(priceCalculationResult.getCategoryPriceMultiplier())
                .build();

        log.info("估算价格结果：{}", priceEstimationResultVO);
        return priceEstimationResultVO;
    }

    /**
     * 计算订单估算价格的核心方法
     *
     * @param serviceId
     * @param originAddress
     * @param destinationAddress
     * @param numberOfHelpers
     * @return
     */
    private PriceCalculationResult calculatePrice(Long serviceId, String originAddress, String destinationAddress, Integer numberOfHelpers) {
        // --- 1. 获取定价所需数据 ---

        // 1.1 获取服务项、货车类型和服务分类数据
        com.***REMOVED***.entity.Service service = serviceMapper.getById(serviceId);
        if (service == null) {
            throw new ServiceNotFoundException(MessageConstant.SERVICE_ITEM_NOT_EXIST);
        }

        TruckType truckType = truckTypeMapper.getById(service.getTruckTypeId());
        if (truckType == null) {
            log.error("服务项 {} 关联的货车类型 {} 不存在", serviceId, service.getTruckTypeId());
            throw new TruckTypeException(MessageConstant.TRUCK_TYPE_NOT_EXIST);
        }

        ServiceCategory serviceCategory = serviceCategoryMapper.getById(service.getCategoryId());
        if (serviceCategory == null) {
            log.error("服务项 {} 关联的服务分类 {} 不存在", serviceId, service.getCategoryId());
            throw new ServiceCategoryException(MessageConstant.SERVICE_CATEGORY_NOT_EXIST);
        }

        // 1.2 获取分类价格乘数
        BigDecimal categoryPriceMultiplier = serviceCategory.getPriceMultiplier();

        // 1.3 获取每个搬运工人的费用标准配置项
        Configuration perHelperCostConfig = configurationMapper.getByName(MessageConstant.PER_HELPER_FEE_LABEL);
        BigDecimal perHelperCost;
        try {
            perHelperCost = new BigDecimal(perHelperCostConfig.getValue());
        } catch (NumberFormatException e) {
            log.error("配置项 'per_helper_cost' 的值 '{}' 不是有效的数字格式", perHelperCostConfig.getValue(), e);
            throw new ConfigurationNotFoundException(MessageConstant.SYSTEM_MOVER_FEE_CONFIG_INVALID_VALUE_WHILE_ESTIMATING_PRICE);
        }


        // --- 2. 计算距离 ---
        BigDecimal distanceKM;
        try {
            // 调用百度地图API计算距离，返回公里数 (BigDecimal)
            distanceKM = calculateDistanceByBaiduApi(originAddress, destinationAddress);
            log.info("计算距离：{} KM", distanceKM);
        } catch (Exception e) { // 捕获其他可能的异常
            log.error("调用地图API计算距离发生未知异常", e);
            throw new OrderBusinessException(MessageConstant.UNKNOWN_ERROR_WHILE_CALCULATING_MOVING_DISTANCE);
        }


        // --- 3. 计算路程费用 ---
        BigDecimal mileageCost = BigDecimal.ZERO;
        BigDecimal startDistance = new BigDecimal("5"); // 起步距离 5km

        if (distanceKM.compareTo(startDistance) <= 0) {
            // 距离小于等于起步距离
            mileageCost = truckType.getBaseFare();
        } else {
            // 距离大于起步距离，按分段计价
            mileageCost = truckType.getBaseFare(); // 从起步价开始累加
            BigDecimal remainingDistance = distanceKM.subtract(startDistance); // 超过起步距离的部分

            // 定义分段里程和对应的单价
            // 注意：这里需要核对 TruckType 字段名是否正确，之前的 XML 中价格字段名有tt_前缀
            BigDecimal[] tiers = {new BigDecimal("20"), new BigDecimal("5"), new BigDecimal("20"), new BigDecimal("30")}; // 5-25km (20km), 25-30km (5km), 30-50km (20km), 50-80km (30km)
            BigDecimal[] prices = {truckType.getPricePerKmTier1(), truckType.getPricePerKmTier2(), truckType.getPricePerKmTier3(), truckType.getPricePerKmTier4()};

            for (int i = 0; i < tiers.length; i++) {
                if (remainingDistance.compareTo(BigDecimal.ZERO) <= 0) break; // 没有剩余距离了

                BigDecimal distanceInTier = remainingDistance.min(tiers[i]); // 当前里程段内的距离
                // 确保分段价格不为 null
                BigDecimal priceInTier = prices[i] != null ? prices[i] : BigDecimal.ZERO;
                mileageCost = mileageCost.add(distanceInTier.multiply(priceInTier));
                remainingDistance = remainingDistance.subtract(distanceInTier);
            }

            // 剩余距离 falls into the last tier (超过80km)
            if (remainingDistance.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal priceTier5 = truckType.getPricePerKmTier5();
                mileageCost = mileageCost.add(remainingDistance.multiply(priceTier5));
            }
        }


        // --- 4. 计算搬运工人费用 ---
        BigDecimal helperCost = BigDecimal.ZERO;
        // 必须检查 numberOfHelpers 是否为 null 且是否大于 0
        if (numberOfHelpers != null && numberOfHelpers > 0) {
            helperCost = perHelperCost.multiply(new BigDecimal(numberOfHelpers));
        } else {
            // 如果 numberOfHelpers 是 null 或 0，搬运工人费用就是 0，这是正常情况
            log.debug("搬运工人数量为 {}，helperCost 计算为 0", numberOfHelpers); // 可选日志
        }

        // --- 5. 计算基础总费用 (路程费用 + 搬运工人费用) ---
        BigDecimal baseTotalBeforeMultiplier = mileageCost.add(helperCost);

        // --- 6. 应用服务类型的价格乘数 ---
        BigDecimal totalEstimatedPrice = baseTotalBeforeMultiplier.multiply(categoryPriceMultiplier);

        // --- 7. 确保所有金额结果为两位小数 ---
        totalEstimatedPrice = totalEstimatedPrice.setScale(2, RoundingMode.HALF_UP);
        mileageCost = mileageCost.setScale(2, RoundingMode.HALF_UP);
        helperCost = helperCost.setScale(2, RoundingMode.HALF_UP);

        // --- 8. 返回包含所有结果的结果对象 ---
        return new PriceCalculationResult(totalEstimatedPrice, mileageCost, helperCost, categoryPriceMultiplier);
    }

    /**
     * 调用百度地图API计算距离 (公里)
     * 借鉴外卖示例代码的逻辑
     *
     * @param originAddress      起点地址
     * @param destinationAddress 终点地址
     * @return 距离（公里），使用BigDecimal，保留2位小数
     * @throws BaseException 如果API调用失败或解析结果失败
     */
    private BigDecimal calculateDistanceByBaiduApi(String originAddress, String destinationAddress) {
        log.info("调用百度地图计算距离，起点：{}，终点：{}", originAddress, destinationAddress);

        // 1. 获取起点经纬度
        String originLngLat = getCoordinates(originAddress); // 调用获取经纬度的方法

        // 2. 获取终点经纬度
        String destinationLngLat = getCoordinates(destinationAddress);

        // 3. 调用百度地图方向规划API获取距离
        Map<String, String> map = new HashMap<>();
        map.put("origin", originLngLat); // 起点经纬度
        map.put("destination", destinationLngLat); // 终点经纬度
        map.put("steps_info", "0"); // 不需要路线详情
        map.put("ak", baiduAk); // 使用注入的 AK

        String json;
        try {
            json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);
        } catch (Exception e) {
            log.error("调用百度地图方向规划API失败: {}", e.getMessage());
            throw new OrderBusinessException("计算搬家距离失败，请检查地址或稍后再试");
        }

        log.debug("百度地图方向规划API返回结果: {}", json);
        JSONObject jsonObject = JSON.parseObject(json);

        // 校验API返回状态码
        if (jsonObject == null || !jsonObject.containsKey("status") || !jsonObject.getString("status").equals("0")) {
            log.error("百度地图方向规划API返回错误或无效结果: {}", json);
            // TODO: 可以根据 Baidu API 错误码进行更精细的处理和用户提示
            throw new OrderBusinessException("计算搬家距离失败，地图服务异常");
        }

        try {
            JSONObject result = jsonObject.getJSONObject("result");
            if (result == null || !result.containsKey("routes")) {
                log.error("百度地图方向规划API结果中未找到routes: {}", json);
                throw new OrderBusinessException("计算搬家距离失败，解析地图结果异常");
            }
            JSONArray jsonArray = result.getJSONArray("routes");
            if (jsonArray == null || jsonArray.isEmpty()) {
                log.error("百度地图方向规划API未返回有效路线: {}", json);
                throw new OrderBusinessException("未找到有效的搬家路线，请检查地址");
            }
            // 获取距离，API返回的距离单位是米
            Integer distanceMeters = jsonArray.getJSONObject(0).getInteger("distance");
            if (distanceMeters == null) {
                log.error("百度地图方向规划API结果中未找到距离信息: {}", json);
                throw new OrderBusinessException("获取搬家距离信息失败");
            }

            // 将米转换为公里，并保留2位小数
            return new BigDecimal(distanceMeters).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("解析百度地图方向规划API结果失败: {}", e.getMessage());
            throw new OrderBusinessException("计算搬家距离失败，解析地图结果异常");
        }
    }

    /**
     * 调用百度地图API获取地址的经纬度
     * 借鉴外卖示例代码的逻辑
     *
     * @param address 地址字符串
     * @return 经纬度字符串，格式通常为 "纬度,经度" 或 "经度,纬度" (取决于下游API需求，需核对)
     * @throws BaseException 如果API调用失败或解析结果失败
     */
    private String getCoordinates(String address) {
        log.info("调用百度地图获取经纬度，地址：{}", address);

        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("output", "json");
        map.put("ak", baiduAk); // 使用注入的 AK

        String json;
        try {
            json = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
        } catch (Exception e) {
            log.error("调用百度地图Geocoding API失败: {}", e.getMessage());
            throw new OrderBusinessException("解析地址失败，请检查地址或稍后再试");
        }

        log.debug("百度地图Geocoding API返回结果: {}", json);
        JSONObject jsonObject = JSON.parseObject(json);

        // 校验API返回状态码
        if (jsonObject == null || !jsonObject.containsKey("status") || !jsonObject.getString("status").equals("0")) {
            log.error("百度地图Geocoding API返回错误或无效结果: {}", json);
            // TODO: 可以根据 Baidu API 错误码进行更精细的处理和用户提示
            throw new OrderBusinessException("解析地址失败，地图服务异常");
        }

        try {
            JSONObject result = jsonObject.getJSONObject("result");
            if (result == null || !result.containsKey("location")) {
                log.error("百度地图Geocoding API结果中未找到位置信息: {}", json);
                throw new OrderBusinessException("解析地址失败，未找到有效位置");
            }
            JSONObject location = result.getJSONObject("location");
            String lat = location.getString("lat"); // 纬度
            String lng = location.getString("lng"); // 经度

            // Baidu Direction API 的 origin/destination 参数通常需要 "latitude,longitude" 格式
            // **IMPORTANT:** 务必核对最新的百度地图路线规划 API 文档来确认坐标格式
            // 示例返回 "纬度,经度" 格式
            return lat + "," + lng;
        } catch (Exception e) {
            log.error("解析百度地图Geocoding API结果失败: {}", e.getMessage());
            throw new OrderBusinessException("解析地址失败，解析地图结果异常");
        }
    }

    /**
     * 用户提交订单
     *
     * @param orderSubmitDTO 订单提交数据
     * @return 订单提交结果 VO
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO) {
        log.info("用户提交订单，参数：{}", orderSubmitDTO);

        // --- 1. 严格校验订单信息 DTO 的完整性和有效性 ---
        if (orderSubmitDTO.getServiceId() == null ||
                orderSubmitDTO.getReservationTime() == null ||
                orderSubmitDTO.getMovingOrigin() == null || orderSubmitDTO.getMovingOrigin().isEmpty() ||
                orderSubmitDTO.getMovingDestination() == null || orderSubmitDTO.getMovingDestination().isEmpty() ||
                orderSubmitDTO.getNumberOfHelpers() == null || orderSubmitDTO.getNumberOfHelpers() < 0) {
            // 抛出业务异常，告知前端缺少必要参数
            throw new OrderBusinessException(MessageConstant.ORDER_INFO_INCOMPLETE);
        }

        // - 预约时间是否在有效范围内 (不能是过去的时间，不能是太遥远的未来) 有效预约时间是未来的2周内
        if (orderSubmitDTO.getReservationTime().isBefore(LocalDateTime.now()) ||
                orderSubmitDTO.getReservationTime().isAfter(LocalDateTime.now().plusWeeks(2))) {
            throw new OrderBusinessException(MessageConstant.RESERVATION_TIME_INVALID);
        }

        // --- 1.5 校验客户邮箱是否存在和基本格式 ---
        Long currentUserId = BaseContext.getCurrentId();
        Customer customer = customerService.getById(currentUserId);
        if (customer == null || customer.getEmail() == null || customer.getEmail().isEmpty()) {
            log.error("用户ID {} 无效或邮箱为空，无法发送订单通知邮件。", currentUserId);
            // 根据你的业务需求决定是否抛出异常阻止下单
            throw new OrderBusinessException(MessageConstant.CANNOT_SEND_ORDER_NOTIFICATION_EMAIL);
        }

        // --- 2. 后端**再次计算**订单最终价格 ---
        // 这是为了防止前端篡改价格，必须使用后端权威的计算逻辑和数据
        PriceCalculationResult calculationResult = calculatePrice(
                orderSubmitDTO.getServiceId(),
                orderSubmitDTO.getMovingOrigin(),
                orderSubmitDTO.getMovingDestination(),
                orderSubmitDTO.getNumberOfHelpers()
        );
        BigDecimal finalOrderPrice = calculationResult.getTotalEstimatedPrice(); // 使用后端计算的最终总价
        log.info("订单最终计算价格：{}", finalOrderPrice);

        // --- 3. 构建 MovingOrder 实体对象 ---
        MovingOrder order = new MovingOrder();
        BeanUtils.copyProperties(orderSubmitDTO, order);
        order.setCustomerId(BaseContext.getCurrentId());
        // 生成唯一订单号
        order.setOrderNumber(generateUniqueOrderNumber());

        // 设置服务项ID和关联的货车类型ID
        // 假设 serviceMapper.getById 方法存在
        com.***REMOVED***.entity.Service service = serviceMapper.getById(orderSubmitDTO.getServiceId());
        order.setTruckTypeId(service.getTruckTypeId()); // 设置关联的货车类型ID

        // 设置初始订单状态和支付状态
        order.setOrderStatus(OrderStatusConstant.PENDING_ACCEPTANCE); // 初始状态：待接单 (0)
        order.setIsPaid(PaymentStatusConstant.UN_PAID); // 初始支付状态：未支付 (0)
        order.setIsReviewed(false); // 初始评价状态：未评价

        // 设置计算出的最终价格和费用明细
        order.setMovingPrice(finalOrderPrice);
        order.setMileageCost(calculationResult.getMileageCost()); // 设置路程费用明细
        order.setHelperCost(calculationResult.getHelperCost()); // 设置搬运工人费用明细
        order.setCategoryPriceMultiplier(calculationResult.getCategoryPriceMultiplier()); // 设置服务类型乘数

        // --- 4. 插入订单主表 ---
        orderMapper.insert(order);

        log.info("订单创建成功，订单号：{}，ID：{}", order.getOrderNumber(), order.getId());

        // --- 5. 调用邮件服务发送订单提交成功通知 ---
        // 新状态是待接单 (0),  不需要传递搬运工列表，传递一个空的搬运工列表
        List<MoverVO> assignedMovers = new ArrayList<>(); // 创建一个空的搬运工列表
        // 调用统一的邮件发送方法，传递刚刚创建并插入数据库的 order 对象，新状态，和空列表
        emailService.sendOrderStatusEmailToCustomer(order, OrderStatusConstant.PENDING_ACCEPTANCE, assignedMovers);

        // --- 6. 封装返回结果 OrderSubmitVO ---
        OrderSubmitVO submitResultVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderAmount(finalOrderPrice)
                .orderTime(order.getCreateTime()) // 返回创建时间
                .build();

        return submitResultVO; // 返回结果 VO
    }

    /**
     * 生成唯一订单号
     *
     * @return
     */
    private String generateUniqueOrderNumber() {
        String timestampPart = String.valueOf(System.currentTimeMillis());
        String randomPart = String.valueOf((int) (Math.random() * 100000)); // 5位随机数
        // 确保随机数是5位，不足前面补0
        while (randomPart.length() < 5) {
            randomPart = "0" + randomPart;
        }

        // MO -- Moving Order
        String orderNumber = "MO" + timestampPart + randomPart; // MO前缀 + 时间戳 + 随机数
        log.debug("生成的订单号：{}", orderNumber);
        return orderNumber;
    }

    /**
     * 订单支付 (模拟)
     *
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    // @Transactional // 如果 paySuccess 内部有事务，这里可以不需要事务，否则加上
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        log.info("用户发起订单支付，参数：{}", ordersPaymentDTO);

        // 1. 参数校验
        if (ordersPaymentDTO == null || ordersPaymentDTO.getOrderNumber() == null || ordersPaymentDTO.getOrderNumber().isEmpty() || ordersPaymentDTO.getPayMethod() == null) {
            throw new OrderBusinessException(MessageConstant.PAYMENT_INFO_INCOMPLETE);
        }

        // 2. 查找订单
        MovingOrder order = orderMapper.getByNumber(ordersPaymentDTO.getOrderNumber());
        if (order == null) {
            log.error("支付失败，订单不存在：{}", ordersPaymentDTO.getOrderNumber());
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 3. 校验订单状态
        // 订单必须是待支付状态 (PENDING_PAYMENT)
        if (!order.getOrderStatus().equals(OrderStatusConstant.PENDING_ACCEPTANCE) || !order.getIsPaid().equals(PaymentStatusConstant.UN_PAID)) {
            log.error("支付失败，订单状态错误或已支付：订单号 {}，订单状态 {}，支付状态 {}", ordersPaymentDTO.getOrderNumber(), order.getOrderStatus(), order.getIsPaid());
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 4. 模拟支付过程和支付成功通知
        log.info("模拟支付，订单号：{}，支付方式：{}", ordersPaymentDTO.getOrderNumber(), ordersPaymentDTO.getPayMethod());

        // 在模拟场景下，我们直接调用支付成功处理逻辑
        // 这模拟了支付平台**立即**回调通知支付成功
        try {
            paySuccess(ordersPaymentDTO.getOrderNumber(), ordersPaymentDTO.getPayMethod()); // 调用支付成功处理方法
            log.info("模拟支付成功处理完成，订单号：{}", ordersPaymentDTO.getOrderNumber());
        } catch (Exception e) {
            // 捕获其他意外异常
            log.error("模拟支付成功处理发生未知错误", e);
            throw new OrderBusinessException(MessageConstant.UNKNOWN_ERROR_WHILE_MOCK_PAYMENT);
        }


        // 5. 封装模拟的支付结果VO
        // 在模拟场景下，返回一个表示支付成功的 VO

        return OrderPaymentVO.builder()
                .orderNumber(ordersPaymentDTO.getOrderNumber())
                .payStatus(PaymentStatusConstant.PAID) // 直接返回已支付状态
                .build();
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param orderNumber
     */
    @Transactional // 支付成功更新订单状态是核心事务
    @Override
    public void paySuccess(String orderNumber, Integer payMethod) {
        log.info("处理订单支付成功，订单号：{}，支付方式：{}", orderNumber, payMethod);

        // 1. 根据订单号查询订单
        MovingOrder order = orderMapper.getByNumber(orderNumber);
        if (order == null) {
            log.error("支付成功处理失败，订单不存在：{}", orderNumber);
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND_WHILE_PAY_SUCCESS);
        }

        // 2. 校验订单状态
        // 只有未支付状态的订单才能被设置为已支付
        if (!order.getIsPaid().equals(PaymentStatusConstant.UN_PAID)) {
            log.warn("订单已处理支付成功或已退款，无需重复处理：订单号 {}", orderNumber);
            return;
        }

        // 3. 更新支付方式、支付状态、支付时间等
        MovingOrder updateOrder = MovingOrder.builder()
                .id(order.getId())
                .isPaid(PaymentStatusConstant.PAID)
                .paymentTime(LocalDateTime.now())
                .payMethod(payMethod)
                .build();

        // 4. 调用 Mapper 更新数据库
        try {
            orderMapper.update(updateOrder);
            log.info("订单支付状态更新成功，订单号：{}", orderNumber);
        } catch (Exception e) {
            log.error("更新订单支付状态数据库失败：订单号 {}", orderNumber, e);
            // 支付已成功，但更新数据库失败是严重问题，需要报警和人工干预
            throw new OrderBusinessException(MessageConstant.UPDATE_ORDER_PAY_STATUS_FAILED);
        }

        log.info("订单支付成功处理完成，订单号：{}", orderNumber);
    }

    /**
     * 用户端历史订单分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("用户端历史订单查询，参数：{}", ordersPageQueryDTO);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());

        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrderVO> page = orderMapper.pageQuery(ordersPageQueryDTO);

        // 对查询结果列表进行后处理：将状态码转换为文字描述
        if (page != null && page.getResult() != null) {
            for (OrderVO orderVO : page.getResult()) {
                if (orderVO.getOrderStatus() != null) {
                    orderVO.setOrderStatusDescription(OrderStatusConstant.getDescription(orderVO.getOrderStatus()));
                }
                if (orderVO.getIsPaid() != null) {
                    orderVO.setIsPaidDescription(PaymentStatusConstant.getDescription(orderVO.getIsPaid()));
                }
                if (orderVO.getPayMethod() != null) {
                    orderVO.setPayMethodDescription(PayMethodConstant.getDescription(orderVO.getPayMethod()));
                }
            }
        }
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 用户端/管理端根据订单id查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetail(Long id) {
        // 1. 查找订单详情
        OrderVO orderVO = orderMapper.getById(id);

        // 2. 校验订单是否存在
        if (orderVO == null) {
            log.error("订单详情查询失败，订单不存在：ID {}", id);
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 4. 进行数据后处理和格式化：将状态码转换为文字描述
        if (orderVO.getOrderStatus() != null) {
            orderVO.setOrderStatusDescription(OrderStatusConstant.getDescription(orderVO.getOrderStatus()));
        }
        if (orderVO.getIsPaid() != null) {
            orderVO.setIsPaidDescription(PaymentStatusConstant.getDescription(orderVO.getIsPaid()));
        }
        if (orderVO.getPayMethod() != null) {
            orderVO.setPayMethodDescription(PayMethodConstant.getDescription(orderVO.getPayMethod()));
        }

        return orderVO;
    }

    /**
     * 用户端取消订单
     *
     * @param id        订单ID
     * @param cancelDTO 包含取消原因的DTO
     */
    @Override
    @Transactional
    public void cancelOrder(Long id, OrderCancelDTO cancelDTO) {
        log.info("用户端取消订单，订单ID：{}，原因：{}", id, cancelDTO != null ? cancelDTO.getCancelReason() : "无原因");

        // 1. 查找订单
        MovingOrder order = orderMapper.getMovingOrderById(id);

        // 2. 校验订单是否存在
        if (order == null) {
            log.error("取消订单失败，订单不存在：ID {}", id);
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 3. 校验订单是否属于当前用户
        Long currentUserId = BaseContext.getCurrentId();
        if (!order.getCustomerId().equals(currentUserId)) {
            log.error("取消订单失败，订单 {} 不属于当前用户 {}", id, currentUserId);
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_BELONG_TO_CURRENT_USER);
        }

        // 4. 校验订单状态是否允许用户取消
        // 假设只有在待接单 (0)、司机已接单等待搬运工人 (1)、已接单 (2) 状态下用户可以自主取消
        Integer currentOrderStatus = order.getOrderStatus();
        if (currentOrderStatus.equals(OrderStatusConstant.IN_PROGRESS) ||
                currentOrderStatus.equals(OrderStatusConstant.COMPLETED) ||
                currentOrderStatus.equals(OrderStatusConstant.CANCELLED)) {
            log.error("取消订单失败，订单状态 {} 不允许取消：订单ID {}", currentOrderStatus, id);
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_NOT_ALLOW_CANCEL);
        }

        // 5. 处理资源解除关联 (先处理这些，因为它们修改了数据库)
        // 只有在状态 1 或 2 时才需要解除分配
        if (OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS.equals(currentOrderStatus) ||
                OrderStatusConstant.ACCEPTED.equals(currentOrderStatus)) {
            log.info("订单已分配，解除司机和搬运工人关联：订单ID {}", id);
            // 5.1 删除 order_mover 表中关联该订单的记录
            orderMoverMapper.deleteByOrderId(id);
            log.info("已删除订单 {} 的搬运工人关联记录", id);

            // 5.2 清空 moving_order 表中的 driver_id 和 vehicle_id
            orderMapper.clearOrderDriverVehicle(id);
            log.info("已清除订单 {} 的司机和车辆关联 (通过单独方法)", id);
        }

        // 6. 创建最小化的 updateOrder 实体，包含要更新的字段和 ID
        // 获取原始取消原因
        String originalReason = cancelDTO != null ? cancelDTO.getCancelReason() : null;
        String finalCancelReason = originalReason;

        // *** 在原因前面加上 "消费者：" 前缀，如果原因不为空 ***
        if (originalReason != null && !originalReason.isEmpty()) {
            finalCancelReason = "消费者：" + originalReason;
        }

        MovingOrder updateOrder = MovingOrder.builder()
                .id(id)
                .orderStatus(OrderStatusConstant.CANCELLED) // 状态设为已取消 (5)
                .cancelReason(finalCancelReason) // 设置取消原因 (从DTO获取)
                .cancelTime(LocalDateTime.now()) // 设置取消时间为当前时间
                .build();

        // 6.1 处理支付状态：如果订单已支付，在 updateOrder 中标记为已退款
        if (order.getIsPaid() != null && order.getIsPaid().equals(PaymentStatusConstant.PAID)) {
            updateOrder.setIsPaid(PaymentStatusConstant.REFUNDED); // 在 updateOrder 中设置支付状态
            // !!! 实际应用中，这里需要调用第三方支付平台的退款接口 !!!
            // !!! 模拟场景下，直接更新状态即可 !!!
            log.info("订单已支付，模拟标记为已退款：订单ID {}", id);
        }

        // 7. 调用 Mapper 更新 moving_order 表 (使用 updateOrder)
        orderMapper.update(updateOrder);
        log.info("订单主要信息更新成功，订单ID：{}，新状态：{}", id, updateOrder.getOrderStatus());

        // 8. 将 updateOrder 中的更新字段值手动复制到原始的 order 对象上
        order.setOrderStatus(updateOrder.getOrderStatus()); // 复制状态 (5)
        order.setCancelTime(updateOrder.getCancelTime()); // 复制取消时间
        order.setCancelReason(updateOrder.getCancelReason()); // 复制取消原因
        order.setUpdateTime(updateOrder.getUpdateTime()); // 复制 updateTime
        // 如果更新了 isPaid，也要复制：
        if (updateOrder.getIsPaid() != null) {
            order.setIsPaid(updateOrder.getIsPaid());
        }

        // 9. 在内存中更新 order 对象，反映 driver_id 和 vehicle_id 已清除
        if (OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS.equals(currentOrderStatus) ||
                OrderStatusConstant.ACCEPTED.equals(currentOrderStatus)) {
            order.setDriverId(null);
            order.setVehicleId(null);
            log.info("订单对象内存更新完成，反映司机和车辆关联已清除");
        }

        // 10. 调用邮件服务发送通知
        // 状态 5 (已取消)，不需要传递搬运工列表，传递一个空列表
        List<MoverVO> assignedMovers = new ArrayList<>(); // 创建一个空的搬运工列表
        // 调用统一的邮件发送方法，传递 修改后并在内存中反映了所有更新的 order 对象，新状态，和空列表
        // 注意：这里传递的 order 对象应包含最终的状态、取消信息、支付状态、以及设置为 null 的 driverId/vehicleId
        emailService.sendOrderStatusEmailToCustomer(order, OrderStatusConstant.CANCELLED, assignedMovers);

        log.info("订单取消处理完成，订单ID：{}", id);
    }

    /**
     * 获取所有订单状态列表
     * 用于前端管理端订单筛选下拉框或Tab展示
     *
     * @return 订单状态VO列表
     */
    @Override
    public List<OrderStatusVO> getOrderStatusList() {
        List<OrderStatusVO> statusList = new ArrayList<>();

        // 直接从 OrderStatusConstant 中获取所有状态码和描述
        // 按照 OrderStatusConstant 中定义的顺序或逻辑顺序添加
        statusList.add(new OrderStatusVO(OrderStatusConstant.PENDING_ACCEPTANCE, OrderStatusConstant.getDescription(OrderStatusConstant.PENDING_ACCEPTANCE)));
        statusList.add(new OrderStatusVO(OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS, OrderStatusConstant.getDescription(OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS)));
        statusList.add(new OrderStatusVO(OrderStatusConstant.ACCEPTED, OrderStatusConstant.getDescription(OrderStatusConstant.ACCEPTED)));
        statusList.add(new OrderStatusVO(OrderStatusConstant.IN_PROGRESS, OrderStatusConstant.getDescription(OrderStatusConstant.IN_PROGRESS)));
        statusList.add(new OrderStatusVO(OrderStatusConstant.COMPLETED, OrderStatusConstant.getDescription(OrderStatusConstant.COMPLETED)));
        statusList.add(new OrderStatusVO(OrderStatusConstant.CANCELLED, OrderStatusConstant.getDescription(OrderStatusConstant.CANCELLED)));

        log.info("获取到 {} 个订单状态", statusList.size());
        return statusList;
    }

    /**
     * 获取所有支付状态列表
     * 用于前端下拉框或Tab展示
     *
     * @return 支付状态VO列表
     */
    @Override
    public List<PaymentStatusVO> getPaymentStatusList() {
        List<PaymentStatusVO> statusList = new ArrayList<>();

        // 直接从 PaymentStatusConstant 中获取所有状态码和描述
        // 按照 Constant 中定义的顺序或逻辑顺序添加
        statusList.add(new PaymentStatusVO(PaymentStatusConstant.UN_PAID, PaymentStatusConstant.getDescription(PaymentStatusConstant.UN_PAID)));
        statusList.add(new PaymentStatusVO(PaymentStatusConstant.PAID, PaymentStatusConstant.getDescription(PaymentStatusConstant.PAID)));
        statusList.add(new PaymentStatusVO(PaymentStatusConstant.REFUNDED, PaymentStatusConstant.getDescription(PaymentStatusConstant.REFUNDED)));

        log.info("获取到 {} 个支付状态", statusList.size());
        return statusList;
    }

    /**
     * 管理端分页查询订单列表
     * 返回所有状态的订单，并支持多种筛选条件
     *
     * @param ordersPageQueryDTO 查询条件 (包含管理端筛选字段)
     * @return 分页结果 (PageResult<OrderVO>)
     */
    @Override
    public PageResult pageQueryByAdmin(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrderVO> page = orderMapper.pageQueryByAdmin(ordersPageQueryDTO);

        // 对查询结果列表进行后处理：将状态码转换为文字描述
        // 虽然 OrderVO 中有描述字段，Mapper SQL 选择了原始码，这里进行转换填充
        if (page != null && page.getResult() != null) {
            for (OrderVO orderVO : page.getResult()) {
                if (orderVO.getOrderStatus() != null) {
                    orderVO.setOrderStatusDescription(OrderStatusConstant.getDescription(orderVO.getOrderStatus()));
                }
                if (orderVO.getIsPaid() != null) {
                    orderVO.setIsPaidDescription(PaymentStatusConstant.getDescription(orderVO.getIsPaid()));
                }
                if (orderVO.getPayMethod() != null) {
                    orderVO.setPayMethodDescription(PayMethodConstant.getDescription(orderVO.getPayMethod()));
                }
            }
        }

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 管理员取消订单
     *
     * @param id        订单ID
     * @param cancelDTO 包含取消原因的DTO
     */
    @Override
    @Transactional
    public void adminCancelOrder(Long id, AdminOrderCancelDTO cancelDTO) {
        String cancelReason = cancelDTO.getCancelReason();
        // *** 在原因前面加上 "消费者：" 前缀，如果原因不为空 ***
        if (cancelReason != null && !cancelReason.isEmpty()) {
            cancelReason = "管理员工号" + BaseContext.getCurrentId() + "：" + cancelReason;
        }

        // 1. 查找订单
        MovingOrder order = orderMapper.getMovingOrderById(id);

        // 2. 校验订单是否存在
        if (order == null) {
            log.error("管理员取消订单失败，订单不存在：ID {}", id);
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 3. 校验订单当前状态是否允许管理员取消 (通常允许从所有非终态取消: 0, 1, 2, 3)
        Integer currentOrderStatus = order.getOrderStatus();
        if (OrderStatusConstant.COMPLETED.equals(currentOrderStatus) ||
                OrderStatusConstant.CANCELLED.equals(currentOrderStatus)) {
            log.error("管理员取消订单失败，订单状态 {} 已是终态，不允许取消：订单ID {}", currentOrderStatus, id);
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_NOT_ALLOW_CANCEL);
        }

        // 4. 处理资源解除关联 (先处理这些，因为它们修改了数据库) ---
        // 订单在状态 1, 2, 3 时可能关联有司机和搬运工，取消时需要解除
        if (OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS.equals(currentOrderStatus) ||
                OrderStatusConstant.ACCEPTED.equals(currentOrderStatus) ||
                OrderStatusConstant.IN_PROGRESS.equals(currentOrderStatus)) {

            log.info("订单在状态 {} 已关联资源，解除司机和搬运工人关联：订单ID {}", currentOrderStatus, id);

            // 4.1 删除 order_mover 表中关联该订单的记录
            orderMoverMapper.deleteByOrderId(id);
            log.info("已删除订单 {} 的搬运工人关联记录", id);

            // 4.2 清空 moving_order 表中的 driver_id 和 vehicle_id
            // 假设 orderMapper 有 clearOrderDriverVehicle 方法将 driver_id 和 vehicle_id 设为 NULL
            orderMapper.clearOrderDriverVehicle(id); // 这个方法直接更新数据库
            log.info("已清除订单 {} 的司机和车辆关联 (通过单独方法)", id);
        }

        // 5. 处理状态特定的复杂业务逻辑 (重点在状态 3 进行中) ---
        if (OrderStatusConstant.IN_PROGRESS.equals(currentOrderStatus)) {
            log.info("订单在进行中状态被管理员取消，触发复杂业务处理：订单ID {}", id);
            // !!! 在这里实现复杂的业务判断和处理逻辑 !!!
        }

        // 6. 创建最小化的 updateOrder 实体，包含要更新的字段和 ID (不包含 driverId 和 vehicleId) ---
        MovingOrder updateOrder = MovingOrder.builder()
                .id(id)
                .orderStatus(OrderStatusConstant.CANCELLED) // 新状态设为 5 (已取消)
                .cancelReason(cancelReason) // 设置管理员提供的取消原因
                .cancelTime(LocalDateTime.now()) // 设置取消时间为当前时间
                .build();

        // 6.1 处理支付状态：如果订单已支付，在 updateOrder 中标记为已退款
        if (order.getIsPaid() != null && order.getIsPaid().equals(PaymentStatusConstant.PAID)) {
            updateOrder.setIsPaid(PaymentStatusConstant.REFUNDED); // 在 updateOrder 中设置支付状态
            // !!! 实际应用中，这里需要调用第三方支付平台的退款接口 !!!
            // !!! 模拟场景下，直接更新状态即可 !!!
            log.info("订单已支付，模拟标记为已退款：订单ID {}", id);
        }

        // 7. 调用 Mapper 更新 moving_order 表 (使用 updateOrder) ---
        orderMapper.update(updateOrder);
        log.info("订单主要信息更新成功，订单ID：{}，新状态：{}", id, updateOrder.getOrderStatus());

        // 8. 将 updateOrder 中的更新字段值手动复制到原始的 order 对象上 ---
        order.setOrderStatus(updateOrder.getOrderStatus()); // 复制状态 (5)
        order.setCancelTime(updateOrder.getCancelTime()); // 复制取消时间
        order.setCancelReason(updateOrder.getCancelReason()); // 复制取消原因
        order.setUpdateTime(updateOrder.getUpdateTime()); // 复制 updateTime
        // 如果更新了 isPaid，也要复制：
        if (updateOrder.getIsPaid() != null) {
            order.setIsPaid(updateOrder.getIsPaid());
        }

        // 9. 在内存中更新 order 对象，反映 driver_id 和 vehicle_id 已清除 ***
        // 这是因为 driver_id 和 vehicle_id 是通过单独的方法清空的 (步骤 4.2)，不在 updateOrder 中
        // 必须在内存中的 order 对象上反映这个变化，否则 emailService 拿到的 order 对象 driverId/vehicleId 还是旧值
        order.setDriverId(null);
        order.setVehicleId(null);
        log.info("订单对象内存更新完成，反映司机和车辆关联已清除");

        // 10. 调用邮件服务发送通知 ---
        // 状态 5 (已取消)，不需要传递搬运工列表，传递一个空列表
        List<MoverVO> assignedMovers = new ArrayList<>(); // 创建一个空的搬运工列表
        // 调用统一的邮件发送方法，传递 修改后并在内存中反映了所有更新的 order 对象，新状态，和空列表
        emailService.sendOrderStatusEmailToCustomer(order, OrderStatusConstant.CANCELLED, assignedMovers);

        log.info("管理员取消订单处理完成，订单ID：{}", id);
    }

    /**
     * 管理员强制完成订单
     *
     * @param id 订单ID
     */
    @Transactional
    @Override
    public void forceComplete(Long id) {
        // 1. 校验订单是否存在
        MovingOrder order = orderMapper.getMovingOrderById(id);
        if (order == null) {
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 2. 校验订单状态是否为进行中 (3)
        if (!OrderStatusConstant.IN_PROGRESS.equals(order.getOrderStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_NOT_ALLOW_FORCE_COMPLETE);
        }

        // 3. 创建最小化的 updateOrder 实体，只包含要更新的字段和 ID
        MovingOrder updateOrder = MovingOrder.builder()
                .id(id)
                .orderStatus(OrderStatusConstant.COMPLETED) // 新状态
                .movingEndTime(LocalDateTime.now()) // 结束时间
                .build();

        // 4. 更新数据库，使用最小化的 updateOrder
        orderMapper.update(updateOrder);

        // 5. 将 updateOrder 中的更新字段值手动复制到原始的 order 对象上
        order.setOrderStatus(OrderStatusConstant.COMPLETED); // 复制状态
        order.setMovingEndTime(LocalDateTime.now()); // 复制结束时间
        order.setUpdateTime(LocalDateTime.now()); // 复制 updateTime

        // 6. 调用邮件服务发送通知
        // 对于状态 4 (已完成)，不需要传递搬运工列表，传递一个空列表
        List<MoverVO> assignedMovers = new ArrayList<>(); // 创建一个空的搬运工列表
        // 调用统一的邮件发送方法，传递修改后的 order 对象，新状态，和空列表
        emailService.sendOrderStatusEmailToCustomer(order, OrderStatusConstant.COMPLETED, assignedMovers);
    }

    /**
     * 处理订单自动取消 (支付超时)
     *
     * @param orderId 订单ID
     * @param reason  取消原因
     */
    @Transactional
    @Override
    public void processPaymentTimeoutCancellation(Long orderId, String reason) {
        // 1. 获取订单信息，用于后续校验和更新
        MovingOrder order = orderMapper.getMovingOrderById(orderId);

        // 2. 校验订单当前状态是否满足【支付超时自动取消】的条件
        // 条件：订单存在，状态为待接单(0)，未支付(0)，且未被手动取消(5)或已完成(4)
        if (order == null ||
                !OrderStatusConstant.PENDING_ACCEPTANCE.equals(order.getOrderStatus()) || // 确保状态仍是待接单(0)
                (order.getIsPaid() != null && order.getIsPaid().equals(PaymentStatusConstant.PAID)) || // 确保未支付(0)
                // 以下两项确保订单没有被其他流程先处理掉
                OrderStatusConstant.CANCELLED.equals(order.getOrderStatus()) || // 确保不是已取消(5)
                OrderStatusConstant.COMPLETED.equals(order.getOrderStatus()) // 确保不是已完成(4)
        ) {
            // 如果不满足支付超时自动取消条件，记录信息并返回
            log.info("订单不满足支付超时自动取消条件：订单ID={}, 当前状态={}, 支付状态={}",
                    orderId, order != null ? order.getOrderStatus() : "null", order != null ? order.getIsPaid() : "null");
            return; // 不符合自动取消条件，直接返回
        }

        // 3. 创建最小化的 updateOrder 实体，只包含要更新的字段和 ID
        MovingOrder updateOrder = MovingOrder.builder()
                .id(orderId)
                .orderStatus(OrderStatusConstant.CANCELLED) // 新状态设为 5 (已取消)
                .cancelTime(LocalDateTime.now()) // 设置取消时间为当前时间
                .cancelReason(reason) // 设置取消原因 (由调用方传入，如"支付超时自动取消")
                .build();

        // 4. 更新数据库，使用最小化的 updateOrder
        orderMapper.update(updateOrder);

        // 5. 将 updateOrder 中的更新字段值手动复制到原始的 order 对象上
        order.setOrderStatus(updateOrder.getOrderStatus()); // 复制状态
        order.setCancelTime(updateOrder.getCancelTime()); // 复制取消时间
        order.setCancelReason(updateOrder.getCancelReason()); // 复制取消原因
        order.setUpdateTime(updateOrder.getUpdateTime()); // 复制 updateTime

        // 6. 调用邮件服务发送通知
        // 对于状态 5 (已取消)，不需要传递搬运工列表，传递一个空列表
        List<MoverVO> assignedMovers = new ArrayList<>(); // 创建一个空的搬运工列表
        // 调用统一的邮件发送方法，传递修改后的 order 对象，新状态，和空列表
        emailService.sendOrderStatusEmailToCustomer(order, OrderStatusConstant.CANCELLED, assignedMovers);
    }

    // --- 司机相关订单查询方法实现 ---

    /**
     * 司机端分页查询待接订单列表
     *
     * @param pageQueryDTO 分页及筛选条件
     * @return 待接订单分页结果
     */
    @Override
    @Transactional(readOnly = true) // 只读事务
    public PageResult driverPageQueryAvailable(DriverAvailableOrderPageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<DriverAvailableOrderSummaryVO> page = orderMapper.driverPageQueryAvailable(pageQueryDTO, BaseContext.getCurrentId());
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 司机端根据订单ID查询待接订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情 VO
     */
    @Override
    @Transactional(readOnly = true) // 只读事务
    public DriverAvailableOrderDetailVO driverGetAvailableDetail(Long orderId) {
        Long currentDriverId = BaseContext.getCurrentId(); // 获取当前司机ID

        // OrderMapper 的 SQL 会强制过滤 order_status=0, is_paid=1, 并校验司机能力
        // 如果 Mapper 返回 null，表示该订单不存在、非待接单、未支付、或司机无权限查看
        DriverAvailableOrderDetailVO orderDetail = orderMapper.driverGetAvailableDetail(orderId, currentDriverId);

        if (orderDetail == null) {
            log.warn("司机{} 查询订单详情失败，订单ID：{}，订单不存在、非待接单、未支付或无权限", currentDriverId, orderId);
            throw new BusinessException("订单不存在或无权限");
        }

        // --- 在Service层设置状态和支付标签 ---
        orderDetail.setOrderStatusLabel(OrderStatusConstant.getDescription(orderDetail.getOrderStatus()));
        orderDetail.setIsPaidLabel(orderDetail.getIsPaid() == 1 ? "已支付" : "未支付");

        log.info("成功查询待接订单详情，订单ID：{}", orderId);
        return orderDetail;
    }

    /**
     * 获取适用于司机、搬家工人端“我的订单”列表筛选的状态列表
     *
     * @return 包含状态码和描述的 OrderStatusVO 列表
     */
    @Override
    @Transactional(readOnly = true) // 只读事务
    public List<OrderStatusVO> driverMoverGetMyOrderStatuses() {
        List<OrderStatusVO> statusList = new ArrayList<>();
        // 遍历 OrderStatusConstant 中的司机、搬家工人状态列表常量，并使用getDescription方法
        for (Integer status : OrderStatusConstant.DRIVER_Mover_MY_ORDER_STATUSES) {
            statusList.add(new OrderStatusVO(status, OrderStatusConstant.getDescription(status)));
        }
        return statusList; // 返回列表
    }

    /**
     * 司机端分页查询我的订单列表
     *
     * @param pageQueryDTO 分页及筛选条件
     * @return 我的订单分页结果
     */
    @Override
    @Transactional(readOnly = true) // 只读事务
    public PageResult driverPageQueryMy(DriverMyOrderPageQueryDTO pageQueryDTO) {
        Long currentDriverId = BaseContext.getCurrentId(); // 获取当前司机ID

        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<DriverMyOrderSummaryVO> page = orderMapper.driverPageQueryMy(pageQueryDTO, currentDriverId);

        // --- 在Service层处理 orderStatus 到 orderStatusLabel 的转换 ---
        List<DriverMyOrderSummaryVO> resultList = page.getResult();
        if (resultList != null && !resultList.isEmpty()) {
            for (DriverMyOrderSummaryVO order : resultList) {
                order.setOrderStatusLabel(OrderStatusConstant.getDescription(order.getOrderStatus()));
            }
        }

        return new PageResult(page.getTotal(), resultList);
    }

    /**
     * 根据订单ID查询司机端我的订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情 VO
     */
    @Override
    @Transactional(readOnly = true) // 只读事务
    public DriverMyOrderDetailVO driverGetMyDetail(Long orderId) {
        Long currentDriverId = BaseContext.getCurrentId(); // 获取当前司机ID

        // OrderMapper 的 SQL 会进行司机归属和状态过滤
        // 如果 Mapper 返回 null，表示订单不存在、不属于该司机或状态不符
        DriverMyOrderDetailVO orderDetail = orderMapper.driverGetMyDetail(orderId, currentDriverId);

        // 进行结果校验
        if (orderDetail == null) {
            log.warn("司机{} 查询订单详情失败，订单ID：{}，订单不存在、不属于该司机或状态不符", currentDriverId, orderId);
            throw new BusinessException("订单不存在、不属于该司机或状态不符。");
        }

        // --- 在Service层设置状态和支付标签 ---
        orderDetail.setOrderStatusLabel(OrderStatusConstant.getDescription(orderDetail.getOrderStatus()));
        orderDetail.setIsPaidLabel(orderDetail.getIsPaid() == 1 ? "已支付" : "未支付");

        return orderDetail;
    }

    /**
     * 获取后台司机、搬家工人端历史订单可筛选的状态列表
     *
     * @return 订单状态VO列表
     */
    @Override
    public List<OrderStatusVO> getHistoricalOrderStatusOptions() {
        List<OrderStatusVO> statusOptions = new ArrayList<>();
        // 历史订单通常指已完成或已取消的状态
        List<Integer> historicalStatuses = Arrays.asList(
                OrderStatusConstant.COMPLETED,  // 4
                OrderStatusConstant.CANCELLED   // 5
        );

        for (Integer status : historicalStatuses) {
            statusOptions.add(new OrderStatusVO(status, OrderStatusConstant.getDescription(status)));
        }

        return statusOptions;
    }

    /**
     * 后台司机端历史订单分页查询
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult driverPageQueryHistoricalOrders(DriverHistoricalOrderPageQueryDTO queryDTO) {
        Long currentDriverId = BaseContext.getCurrentId();
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Page<DriverHistoricalOrderSummaryVO> page = orderMapper
                .driverPageQueryHistoricalOrders(queryDTO, currentDriverId);

        // 在这里进行数据转换，填充 orderStatusLabel 和 isPaidLabel
        if (page != null && page.getResult() != null) {
            for (DriverHistoricalOrderSummaryVO vo : page.getResult()) {
                // 使用常量类的方法获取文字描述
                vo.setOrderStatusLabel(OrderStatusConstant.getDescription(vo.getOrderStatus()));
                vo.setIsPaidLabel(PaymentStatusConstant.getDescription(vo.getIsPaid()));
            }
        }

        // 封装分页结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 司机端根据订单ID查询后台历史订单详情
     *
     * @param orderId 订单ID
     * @return 历史订单详情VO
     */
    @Override
    public DriverHistoricalOrderDetailVO driverGetHistoricalOrderDetail(Long orderId) {
        Long currentDriverId = BaseContext.getCurrentId();

        // 1. 查询订单主体信息和关联的一对一/多对一信息 (包括 vehicle 字段)
        DriverHistoricalOrderDetailVO detailVO = orderMapper.driverGetHistoricalOrderDetail(orderId, currentDriverId);

        if (detailVO == null) {
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 2. 查询关联的列表信息 (搬家工人、评价)
        List<MoverVO> assignedMovers = orderMoverMapper.getAssignedMoversByOrderId(orderId);
        // 调用 RatingMapper，传递评分类型常量值
        List<RatingVO> ratings = ratingMapper.getRatingsByOrderId(
                orderId,
                RatingTypeConstant.RATING_TYPE_DRIVER,
                RatingTypeConstant.RATING_TYPE_MOVER,
                RatingTypeConstant.RATING_TYPE_SERVICE
        );

        // 3. 数据转换和组装
        // 转换状态码和支付方式为文字描述
        detailVO.setOrderStatusLabel(OrderStatusConstant.getDescription(detailVO.getOrderStatus()));
        detailVO.setIsPaidLabel(PaymentStatusConstant.getDescription(detailVO.getIsPaid()));
        detailVO.setPayMethodLabel(PayMethodConstant.getDescription(detailVO.getPayMethod()));

        // 填充 assignedMovers 列表
        detailVO.setAssignedMovers(assignedMovers);

        // 填充 ratings 列表，并处理特殊的 rateeName (例如 "您")
        if (ratings != null) {
            ratings.forEach(rating -> {
                // rateeName 和 ratingTypeLabel 已在 Mapper 中填充
                // 处理司机评价中特殊情况 "您"
                if (RatingTypeConstant.RATING_TYPE_DRIVER.equals(rating.getRatingType()) &&
                        rating.getRateeId() != null && rating.getRateeId().equals(currentDriverId)) {
                    rating.setRateeName("您");
                }
            });
        }
        detailVO.setRatings(ratings);

        return detailVO;
    }

    /**
     * 后台司机端接单
     *
     * @param driverAcceptOrderDTO 包含订单ID的请求DTO
     */
    @Transactional // 事务管理
    @Override
    public void driverAcceptOrder(DriverAcceptOrderDTO driverAcceptOrderDTO) {
        Long orderId = driverAcceptOrderDTO.getOrderId();
        Long currentDriverId = BaseContext.getCurrentId();

        // 1. 获取并锁定订单记录 (在事务中)
        // 使用 getMovingOrderById 查询，支持 SELECT ... FOR UPDATE
        MovingOrder order = orderMapper.getMovingOrderById(orderId);

        // 2. 基础业务检查
        // 检查订单是否存在
        if (order == null) {
            log.warn("接单失败: 订单不存在或已删除, orderId={}", orderId);
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 检查订单状态是否可接 (必须是待接单 - 0)
        if (!order.getOrderStatus().equals(OrderStatusConstant.PENDING_ACCEPTANCE)) {
            log.warn("接单失败: 订单状态错误, orderId={}, currentStatus={}", orderId, order.getOrderStatus());
            throw new BusinessException(MessageConstant.ORDER_STATUS_ERROR + ", 订单已被接走或已取消");
        }

        // 检查司机账号状态 (是否被禁用)
        Integer isBanned = driverMapper.getIsBannedById(currentDriverId);
        if (isBanned != null && isBanned == 1) {
            log.warn("接单失败: 司机账号被禁用, driverId={}", currentDriverId);
            throw new BusinessException(MessageConstant.ACCOUNT_DISABLED);
        }

        // 检查司机是否有资格驾驶该订单所需车型
        Integer countDriverTruckType = driverTruckTypeMapper.countByDriverIdAndTruckTypeId(currentDriverId, order.getTruckTypeId());
        if (countDriverTruckType == null || countDriverTruckType == 0) {
            log.warn("接单失败: 司机没有订单所需车型权限, driverId={}, truckTypeId={}", currentDriverId, order.getTruckTypeId());
            throw new BusinessException(MessageConstant.DRIVER_TRUCK_TYPE_PERMISSION_MISMATCH);
        }

        // 4. 分配车辆
        // 根据司机ID和订单车型ID查询车辆ID (根据“每个汽车类型下，司机只有一个汽车”的规则)
        Long assignedVehicleId = vehicleMapper.getIdByDriverAndTruckType(currentDriverId, order.getTruckTypeId()); // 假设 VehicleMapper 有此方法
        if (assignedVehicleId == null) {
            log.warn("接单失败: 未找到司机符合车型要求的车辆, driverId={}, truckTypeId={}", currentDriverId, order.getTruckTypeId());
            throw new BusinessException(MessageConstant.DRIVER_TRUCK_TYPE_NOT_AVAILABLE);
        }

        // 根据所需搬运工数量确定订单状态
        Integer newOrderStatus;
        if (order.getNumberOfHelpers() == null || order.getNumberOfHelpers() == 0) {
            // 如果不需要搬运工，则直接将状态更新为“团队已就绪”
            newOrderStatus = OrderStatusConstant.ACCEPTED; // 已接单 (团队确认)
            log.info("订单 {} 无需搬运工，司机接单后状态直接变更为 {}", orderId, newOrderStatus);
        } else {
            // 如果需要搬运工，则更新为“司机已接单，等待搬运工接单”
            newOrderStatus = OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS; // 司机已接单，等待搬运工人
            log.info("订单 {} 需要搬运工，司机接单后状态变更为 {}", orderId, newOrderStatus);
        }

        // 5. 更新订单记录 (在事务中)
        // 设置要更新的字段值
        MovingOrder updateOrder = MovingOrder.builder()
                .id(orderId)
                .orderStatus(newOrderStatus)
                .driverId(currentDriverId)
                .vehicleId(assignedVehicleId)
                .build();

        orderMapper.update(updateOrder);
        log.info("订单 {} 已成功分配给司机 {}, 车辆 {}", orderId, currentDriverId, assignedVehicleId);

        order.setOrderStatus(newOrderStatus);
        order.setDriverId(currentDriverId);
        order.setVehicleId(assignedVehicleId);
        order.setUpdateTime(LocalDateTime.now());

        // 7. 后续操作 (在事务提交后触发，例如发送通知，启动搬运工分配流程)
        // **发送客户通知邮件**
        // 调用 EmailService 发送通知。传递更新后的 order 对象，新的状态，以及搬运工列表 (状态1不需要，传 null)
        emailService.sendOrderStatusEmailToCustomer(order, newOrderStatus, null);
        log.info("已触发订单 {} 状态变更邮件通知 (新状态: {})", orderId, newOrderStatus);
    }

    /**
     * 后台司机端取消已接订单
     * 业务规则：司机只能取消状态为 1 且没有搬运工接单的订单，取消后状态回退为 0。
     *
     * @param driverCancelOrderDTO 包含订单ID和取消原因的请求DTO
     */
    @Transactional
    @Override
    public void driverCancelOrder(DriverCancelOrderDTO driverCancelOrderDTO) {
        // 从 DTO 中获取订单ID和取消原因
        Long orderId = driverCancelOrderDTO.getOrderId();
        String cancelReason = driverCancelOrderDTO.getCancelReason();
        // *** 在原因前面加上 "消费者：" 前缀，如果原因不为空 ***
        if (cancelReason != null && !cancelReason.isEmpty()) {
            cancelReason = "司机工号" + BaseContext.getCurrentId() + "：" + cancelReason;
        }


        // 获取当前司机ID
        Long currentDriverId = BaseContext.getCurrentId();

        // 1. 获取并锁定订单记录 (在事务中)
        // 使用 getMovingOrderById 查询，支持 SELECT ... FOR UPDATE
        MovingOrder order = orderMapper.getMovingOrderById(orderId);

        // 2. 业务检查 (严格按照业务规则)
        // 检查订单是否存在
        if (order == null) {
            log.warn("取消订单失败: 订单不存在或已删除, orderId={}", orderId);
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 检查订单是否分配给当前司机
        if (order.getDriverId() == null || !order.getDriverId().equals(currentDriverId)) {
            log.warn("取消订单失败: 订单未分配给当前司机, orderId={}, currentDriverId={}, assignedDriverId={}",
                    orderId, currentDriverId, order.getDriverId());
            throw new BusinessException(MessageConstant.NO_PERMISSION + ", 您无权取消此订单");
        }
        // 检查订单状态是否可取消 (必须是状态 1)
        if (!order.getOrderStatus().equals(OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS)) {
            log.warn("取消订单失败: 订单状态错误，非司机已接单状态, orderId={}, currentStatus={}", orderId, order.getOrderStatus());
            throw new BusinessException(MessageConstant.ORDER_STATUS_ERROR + ", 订单状态错误，无法取消");
        }
        // 检查是否有搬运工人接单 (必须没有搬运工人)
        int moverCount = orderMoverMapper.countByOrderId(orderId);
        if (moverCount > 0) {
            log.warn("取消订单失败: 已有搬运工人接单, orderId={}, moverCount={}", orderId, moverCount);
            throw new BusinessException(MessageConstant.MOVER_ALREADY_ASSIGNED + ", 已有搬运工人接单，无法取消");
        }

        // 3. 更新订单记录 (在事务中)
        // 在查询到的 order 对象上设置要更新的字段
        order.setOrderStatus(OrderStatusConstant.PENDING_ACCEPTANCE); // 状态回退为 0
        order.setCancelReason(cancelReason); // 设置取消原因
        order.setCancelTime(LocalDateTime.now()); // 记录取消时间
        order.setDriverId(null); // 解除司机分配
        order.setVehicleId(null); // 解除车辆分配
        order.setUpdateTime(LocalDateTime.now());
        // AutoFill 会自动为 order 对象填充 update_time 和 update_user

        MovingOrder updateOrder = MovingOrder.builder()
                .id(orderId)
                .orderStatus(OrderStatusConstant.PENDING_ACCEPTANCE)
                .cancelReason(cancelReason)
                .cancelTime(LocalDateTime.now())
                .driverId(null)
                .vehicleId(null)
                .build();

        orderMapper.update(updateOrder);
        orderMapper.clearOrderDriverVehicle(orderId);
        log.info("订单 {} 已成功被司机 {} 取消，状态回退至 {}", orderId, currentDriverId, OrderStatusConstant.PENDING_ACCEPTANCE);

        // 5. 后续操作 (在事务提交后触发)
        // 通知客户订单状态变更
        // 取消状态是 0，emailService 的 generateEmailBody 对于状态 0 有相应内容
        emailService.sendOrderStatusEmailToCustomer(order, OrderStatusConstant.PENDING_ACCEPTANCE, null); // 传递更新后的 order 对象和新状态
        log.info("已触发订单 {} 状态变更邮件通知 (新状态: {})", orderId, OrderStatusConstant.PENDING_ACCEPTANCE);
    }

    // --- 司机开始搬运服务 ---

    /**
     * 后台司机端开始搬运服务
     * 业务规则：司机标记搬家服务正式开始，记录开始时间并将订单状态更新为“进行中”。
     * 订单必须是状态 2 (已接单，团队确认)，并且如果需要搬运工，实际分配数量要匹配。
     *
     * @param orderId 要开始搬运的订单ID
     */
    @Transactional
    @Override
    public void driverStartMoving(Long orderId) {
        // 获取当前司机ID
        Long currentDriverId = BaseContext.getCurrentId();

        // 1. 获取并锁定订单记录 (在事务中)
        // 使用 getMovingOrderById 查询，支持 SELECT ... FOR UPDATE
        MovingOrder order = orderMapper.getMovingOrderById(orderId); // 查询完整的订单对象

        // 2. 业务检查
        // 检查订单是否存在
        if (order == null) {
            log.warn("开始搬运失败: 订单不存在或已删除, orderId={}", orderId);
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 检查订单是否分配给当前司机
        if (order.getDriverId() == null || !order.getDriverId().equals(currentDriverId)) {
            log.warn("开始搬运失败: 订单未分配给当前司机, orderId={}, currentDriverId={}, assignedDriverId={}",
                    orderId, currentDriverId, order.getDriverId());
            throw new BusinessException(MessageConstant.NO_PERMISSION + ", 您无权操作此订单");
        }
        // 检查订单状态是否可开始 (必须是状态 2)
        if (!order.getOrderStatus().equals(OrderStatusConstant.ACCEPTED)) {
            log.warn("开始搬运失败: 订单状态错误，非已接单状态, orderId={}, currentStatus={}", orderId, order.getOrderStatus());
            throw new BusinessException(MessageConstant.ORDER_STATUS_ERROR + ", 订单状态错误，无法开始搬运");
        }
        // （可选）检查搬运工是否已分配到位（如果订单需要搬运工）
        if (order.getNumberOfHelpers() != null && order.getNumberOfHelpers() > 0) {
            int assignedMoverCount = orderMoverMapper.countByOrderId(orderId); // 假设 OrderMoverMapper 有 countByOrderId 方法
            if (assignedMoverCount != order.getNumberOfHelpers()) {
                log.warn("开始搬运失败: 搬运工人数量不匹配, orderId={}, required={}, assigned={}",
                        orderId, order.getNumberOfHelpers(), assignedMoverCount);
                throw new BusinessException(MessageConstant.MOVER_COUNT_MISMATCH + ", 搬运工人团队尚未就位，无法开始");
            }
        }

        // 3. 更新订单记录 (在事务中)
        // 在查询到的 order 对象上设置要更新的字段
        order.setOrderStatus(OrderStatusConstant.IN_PROGRESS); // 状态修改为 3 (进行中)
        order.setMovingStartTime(LocalDateTime.now()); // 设置搬家开始时间
        order.setUpdateTime(LocalDateTime.now());
        // AutoFill 会自动为 order 对象填充 update_time 和 update_user

        MovingOrder updateOrder = MovingOrder.builder()
                .id(orderId)
                .orderStatus(OrderStatusConstant.IN_PROGRESS)
                .movingStartTime(LocalDateTime.now())
                .build();

        orderMapper.update(updateOrder);
        log.info("订单 {} 已成功开始搬运，状态更新为 {}", orderId, OrderStatusConstant.IN_PROGRESS);

        // 5. 后续操作 (在事务提交后触发)
        // 通知客户订单状态变更，传递更新后的 order 对象 和订单的新状态 (3) 给 EmailService
        // 由于方法第三个参数在底层实现中，没有用到，所以传 null
        emailService.sendOrderStatusEmailToCustomer(order, OrderStatusConstant.IN_PROGRESS, null); // 传递 order 对象，新状态，搬运工列表传 null (状态3不需要)
        log.info("已触发订单 {} 状态变更邮件通知 (新状态: {})", orderId, OrderStatusConstant.IN_PROGRESS);
    }

    /**
     * 后台司机端完成搬运服务
     * 业务规则：司机标记搬家服务正式结束，记录结束时间并将订单状态更新为“已完成”。
     * 订单必须是状态 3 (进行中)，且开始时间已记录。
     *
     * @param orderId 要完成搬运的订单ID
     */
    @Transactional
    @Override
    public void driverCompleteMoving(Long orderId) {
        // 获取当前司机ID
        Long currentDriverId = BaseContext.getCurrentId();

        // 1. 获取并锁定订单记录 (在事务中)
        // 使用 getMovingOrderById 查询，支持 SELECT ... FOR UPDATE
        MovingOrder order = orderMapper.getMovingOrderById(orderId); // 查询完整的订单对象

        // 2. 业务检查
        // 检查订单是否存在
        if (order == null) {
            log.warn("完成搬运失败: 订单不存在或已删除, orderId={}", orderId);
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 检查订单是否分配给当前司机
        if (order.getDriverId() == null || !order.getDriverId().equals(currentDriverId)) {
            log.warn("完成搬运失败: 订单未分配给当前司机, orderId={}, currentDriverId={}, assignedDriverId={}",
                    orderId, currentDriverId, order.getDriverId());
            throw new RuntimeException(MessageConstant.NO_PERMISSION + ", 您无权操作此订单");
        }
        // 检查订单状态是否可完成 (必须是状态 3)
        if (!order.getOrderStatus().equals(OrderStatusConstant.IN_PROGRESS)) {
            log.warn("完成搬运失败: 订单状态错误，非进行中状态, orderId={}, currentStatus={}", orderId, order.getOrderStatus());
            throw new RuntimeException(MessageConstant.ORDER_STATUS_ERROR + ", 订单状态错误，无法完成搬运");
        }
        // 检查搬家开始时间是否已记录
        if (order.getMovingStartTime() == null) {
            log.warn("完成搬运失败: 搬家开始时间未记录, orderId={}", orderId);
            throw new RuntimeException(MessageConstant.MOVING_START_TIME_NOT_RECORDED + ", 请先标记开始搬运");
        }

        // 3. 更新订单记录 (在事务中)
        // 在查询到的 order 对象上设置要更新的字段
        order.setOrderStatus(OrderStatusConstant.COMPLETED); // 状态修改为 4 (已完成)
        order.setMovingEndTime(LocalDateTime.now()); // 设置搬家结束时间
        order.setUpdateTime(LocalDateTime.now());
        // AutoFill 会自动为 order 对象填充 update_time 和 update_user

        MovingOrder updateOrder = MovingOrder.builder()
                .id(orderId)
                .orderStatus(OrderStatusConstant.COMPLETED)
                .movingEndTime(LocalDateTime.now())
                .build();

        orderMapper.update(updateOrder);

        log.info("订单 {} 已成功完成搬运，状态更新为 {}", orderId, OrderStatusConstant.COMPLETED);

        // 5. 后续操作 (在事务提交后触发)
        // 通知客户订单状态变更，传递更新后的 order 对象 和订单的新状态 (4) 给 EmailService
        // 由于方法第三个参数在底层实现中，没有用到，所以传 null
        emailService.sendOrderStatusEmailToCustomer(order, OrderStatusConstant.COMPLETED, null); // 传递 order 对象，新状态，搬运工列表传 null (状态4不需要)
        log.info("已触发订单 {} 状态变更邮件通知 (新状态: {})", orderId, OrderStatusConstant.COMPLETED);
    }

    /**
     * 搬家工人端分页查询待接订单列表
     *
     * @param pageQueryDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult moverPageQueryAvailable(MoverAvailableOrderPageQueryDTO pageQueryDTO) {
        Long currentId = BaseContext.getCurrentId();
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<MoverAvailableOrderSummaryVO> page = orderMapper.moverPageQueryAvailable(pageQueryDTO, currentId);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 搬家工人端获取待接订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @Override
    public MoverAvailableOrderDetailVO moverGetAvailableDetail(Long orderId) {
        return orderMapper.moverGetAvailableDetail(orderId);
    }

    /**
     * 搬家工人端分页查询我的订单列表
     *
     * @param dto 分页查询条件
     * @return 分页结果
     */
    @Override
    public PageResult moverPageQueryMy(MoverMyOrderPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<MoverMyOrderSummaryVO> page = orderMapper.moverPageQueryMy(BaseContext.getCurrentId(), dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 搬家工人端获取我的订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @Override
    public MoverMyOrderDetailVO moverGetMyDetail(Long orderId) {
        // 1. 获取当前登录的搬家工人ID
        Long currentMoverId = BaseContext.getCurrentId();

        // 2. 权限校验：检查当前搬家工人是否已分配到该订单
        Integer assignmentCount = orderMapper.checkMoverOrderAssignment(orderId, currentMoverId);
        if (assignmentCount == null || assignmentCount == 0) {
            throw new BusinessException("您无权查看此订单详情或订单不存在");
        }

        // 3. 调用Mapper查询订单详情
        MoverMyOrderDetailVO detail = orderMapper.moverGetMyDetail(orderId);

        // 4. 校验查询结果，防止订单被删除或不符合其他条件
        if (detail == null) {
            // 理论上，如果权限校验通过，这里不应该为空，除非订单在权限校验后被删除
            throw new BusinessException("订单不存在或已失效");
        }

        return detail;
    }

    /**
     * 搬家工人端分页查询历史订单列表
     *
     * @param dto 分页查询条件
     * @return 分页结果
     */
    @Override
    public PageResult moverPageQueryHistoricalOrders(MoverHistoricalOrderPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<MoverHistoricalOrderSummaryVO> page = orderMapper
                .moverPageQueryHistoricalOrders(BaseContext.getCurrentId(), dto);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 搬家工人端获取历史订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @Override
    public MoverHistoricalOrderDetailVO moverGetHistoricalOrderDetail(Long orderId) {
        // 1. 获取当前登录的搬家工人ID
        Long currentMoverId = BaseContext.getCurrentId();

        // 2. 调用Mapper查询订单详情，包含权限校验
        MoverHistoricalOrderDetailVO orderDetail = orderMapper.moverGetHistoricalOrderDetail(orderId, currentMoverId);

        // 3. 检查查询结果
        if (orderDetail == null) {
            log.warn("搬家工人ID:{} 查询订单ID:{} 详情失败，订单不存在或无权限", currentMoverId, orderId);
            throw new OrderBusinessException("订单不存在或您无权查看该订单详情");
        }

        // 4. 填充 RatingVO 中的 rateeName 字段 (业务逻辑处理)
        List<RatingVO> ratings = orderDetail.getRatings();
        if (ratings != null && !ratings.isEmpty()) {
            for (RatingVO rating : ratings) {
                if (Objects.equals(rating.getRatingType(), "SERVICE")) {
                    rating.setRateeName(serviceMapper.getById(rating.getRateeId()).getServiceName());
                } else if (Objects.equals(rating.getRatingType(), "DRIVER")) {
                    rating.setRateeName(driverMapper.getById(rating.getRateeId()).getName());
                } else if (Objects.equals(rating.getRatingType(), "MOVER")) {
                    rating.setRateeName(moverMapper.getById(rating.getRateeId()).getName());
                }
            }
        }

        log.info("搬家工人ID:{} 成功查询订单ID:{} 详情", currentMoverId, orderId);
        return orderDetail;
    }

    /**
     * 搬家工人接单
     *
     * @param moverAcceptOrderDTO 接单参数
     */
    @Transactional // 确保原子性
    @Override
    public void moverAcceptOrder(MoverAcceptOrderDTO moverAcceptOrderDTO) {
        Long orderId = moverAcceptOrderDTO.getOrderId();
        Long currentMoverId = BaseContext.getCurrentId();

        // 1. 获取并锁定订单记录 (防止并发抢单和数据不一致)
        // 使用 getMovingOrderById 查询，支持 SELECT ... FOR UPDATE
        MovingOrder order = orderMapper.getMovingOrderById(orderId);

        // 2. 业务检查 (确保订单存在且处于可接单状态，工人资格等)
        if (order == null) {
            log.warn("工人接单失败: 订单不存在, orderId={}", orderId);
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 检查工人账号状态 (是否被禁用等)
        Boolean isBanned = moverMapper.getById(currentMoverId).getIsBanned();
        if (isBanned == null || isBanned) {
            log.warn("工人接单失败: 获取搬家工人账号状态失败, moverId={}", currentMoverId);
            throw new BusinessException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 检查订单状态是否允许搬运工接单 ，订单必须是“司机已接单，等待搬运工接单”状态
        if (!order.getOrderStatus().equals(OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS)) {
            log.warn("工人接单失败: 订单状态不允许接单, orderId={}, currentStatus={}", orderId, order.getOrderStatus());
            throw new BusinessException(MessageConstant.ORDER_STATUS_ERROR + ", 订单当前状态无法接单");
        }

        // 检查工人是否已经接了这个订单 (防止重复接单)
        boolean hasAccepted = orderMoverMapper.existsByOrderIdAndMoverId(orderId, currentMoverId);
        if (hasAccepted) {
            log.warn("工人接单失败: 搬家工人{}已接此订单{}, 无法重复接单", currentMoverId, orderId);
            throw new BusinessException(MessageConstant.MOVER_ALREADY_ASSIGNED_TO_THIS_ORDER);
        }

        // 检查订单是否已满员 (已达到所需搬运工数量)
        int currentMoverCount = orderMoverMapper.countAssignedMoversByOrderId(orderId);
        if (currentMoverCount >= order.getNumberOfHelpers()) {
            log.warn("工人接单失败: 订单已满员, orderId={}, currentMoverCount={}, requiredMovers={}",
                    orderId, currentMoverCount, order.getNumberOfHelpers());
            throw new BusinessException(MessageConstant.ORDER_FULL);
        }

        // 3. 插入搬运工与订单的关联记录
        orderMoverMapper.insert(orderId, currentMoverId);

        // 4. 更新订单主表状态 (如果满员，则更新订单状态)
        int updatedMoverCount = currentMoverCount + 1;
        if (updatedMoverCount == order.getNumberOfHelpers()) {
            // 所有所需搬运工都已到位，更新订单状态
            MovingOrder updateOrder = MovingOrder.builder()
                    .id(orderId)
                    .orderStatus(OrderStatusConstant.ACCEPTED)
                    .build();
            orderMapper.update(updateOrder);
            log.info("订单{}所有搬运工已分配到位，状态变更为{}", orderId, OrderStatusConstant.ACCEPTED);

            order.setOrderStatus(OrderStatusConstant.ACCEPTED);
            order.setUpdateTime(LocalDateTime.now());

            // 5. 后续操作 (在事务提交后触发，发送通知)
            // 获取所有已分配的搬运工列表，以便发送邮件通知
            List<MoverVO> assignedMovers = orderMoverMapper.getAssignedMoversByOrderId(orderId);
            emailService.sendOrderStatusEmailToCustomer(order, OrderStatusConstant.ACCEPTED, assignedMovers);
            log.info("已触发订单 {} 状态变更邮件通知 (新状态: {})", orderId, OrderStatusConstant.ACCEPTED);
        }

        log.info("搬家工人{}，成功接单{}", currentMoverId, orderId);
    }

    /**
     * 搬家工人取消订单
     *
     * @param moverCancelOrderDTO 取消订单分配参数
     */
    @Transactional // 确保原子性
    @Override
    public void moverCancelOrder(MoverCancelOrderDTO moverCancelOrderDTO) {
        Long currentMoverId = BaseContext.getCurrentId();
        Long orderId = moverCancelOrderDTO.getOrderId();
        String cancelReason = moverCancelOrderDTO.getCancelReason(); // 获取取消原因
        // *** 在原因前面加上 "消费者：" 前缀，如果原因不为空 ***
        if (cancelReason != null && !cancelReason.isEmpty()) {
            cancelReason = "搬家工人工号" + currentMoverId + "：" + cancelReason;
        }

        // 1. 获取并锁定订单记录
        MovingOrder order = orderMapper.getMovingOrderById(orderId);

        // 2. 业务检查
        if (order == null) {
            log.warn("工人取消订单分配失败: 订单不存在, orderId={}", orderId);
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 检查搬运工是否确实被分配到此订单
        boolean isAssigned = orderMoverMapper.existsByOrderIdAndMoverId(orderId, currentMoverId);
        if (!isAssigned) {
            log.warn("工人取消订单分配失败: 搬家工人{}未分配到此订单{}, 无法取消", currentMoverId, orderId);
            throw new BusinessException(MessageConstant.MOVER_NOT_ASSIGNED_TO_THIS_ORDER);
        }

        // 检查订单状态是否允许取消分配
        // 只有在 DRIVER_ACCEPTED_WAITING_MOVERS (司机已接，等待搬运工) 或 ACCEPTED (团队已就绪) 状态下才允许取消。
        // 如果订单已在进行中 (IN_PROGRESS) 或已完成 (COMPLETED) 或已取消 (CANCELLED), 则不允许工人取消分配。
        if (order.getOrderStatus().equals(OrderStatusConstant.IN_PROGRESS) ||
                order.getOrderStatus().equals(OrderStatusConstant.COMPLETED) ||
                order.getOrderStatus().equals(OrderStatusConstant.CANCELLED)) {
            log.warn("工人取消订单分配失败: 订单状态{}不允许取消分配, orderId={}, moverId={}", order.getOrderStatus(), orderId, currentMoverId);
            throw new BusinessException(MessageConstant.ORDER_STATUS_ERROR + ", 订单当前状态不允许取消分配");
        }

        // 3. 删除搬运工与订单的关联记录 (代表取消分配)
        int deletedRows = orderMoverMapper.deleteByOrderIdAndMoverId(orderId, currentMoverId);
        if (deletedRows == 0) {
            log.warn("工人取消订单分配失败: 未找到对应的关联记录删除 (理论上不应发生), orderId={}, moverId={}", orderId, currentMoverId);
            throw new BusinessException(MessageConstant.SYSTEM_ERROR); // 确保删除成功，否则可能是内部错误
        }
        log.info("搬家工人{}已从订单{}中取消分配，原因: {}", currentMoverId, orderId, cancelReason != null ? cancelReason : "未提供");

        // 4. 更新订单主表状态 (如果需要)
        // 重新计算当前已分配的搬运工数量
        int currentAssignedMoverCount = orderMoverMapper.countAssignedMoversByOrderId(orderId);

        // 如果订单之前是 "团队已就绪" (ACCEPTED) 状态，且现在搬运工数量不足，则回退状态
        if (order.getOrderStatus().equals(OrderStatusConstant.ACCEPTED) &&
                currentAssignedMoverCount < order.getNumberOfHelpers()) {
            MovingOrder updateOrder = MovingOrder.builder()
                    .id(orderId)
                    .orderStatus(OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS) // 回退到等待搬运工状态
                    .cancelReason(cancelReason)
                    .cancelTime(LocalDateTime.now())
                    .build();
            orderMapper.update(updateOrder);
            log.info("订单{}因搬运工{}取消分配而回退状态至{}", orderId, currentMoverId, OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS);

            order.setOrderStatus(OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS);
            order.setCancelReason(cancelReason);
            order.setCancelTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            // 5. 通知消费者等 (订单状态回退通知)，邮件通知
            emailService.sendOrderStatusEmailToCustomer(order, OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS, null);
            log.info("已触发订单 {} 状态变更邮件通知 (新状态: {})", orderId, OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS);
        } else if (order.getOrderStatus().equals(OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS)) {
            // 如果订单本身就在等待搬运工状态，则状态不变
            log.info("订单{}搬运工{}取消分配，订单状态保持为{}", orderId, currentMoverId, OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS);
        }

    }

}