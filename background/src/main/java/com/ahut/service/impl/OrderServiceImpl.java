package com.***REMOVED***.service.impl;


import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.dto.PriceEstimationDTO;
import com.***REMOVED***.entity.Configuration;
import com.***REMOVED***.entity.ServiceCategory;
import com.***REMOVED***.entity.TruckType;
import com.***REMOVED***.exception.*;
import com.***REMOVED***.mapper.ConfigurationMapper;
import com.***REMOVED***.mapper.ServiceCategoryMapper;
import com.***REMOVED***.mapper.ServiceMapper;
import com.***REMOVED***.mapper.TruckTypeMapper;
import com.***REMOVED***.result.PriceCalculationResult;
import com.***REMOVED***.service.OrderService;
import com.***REMOVED***.utils.HttpClientUtil;
import com.***REMOVED***.vo.PriceEstimationResultVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ServiceCategoryMapper serviceCategoryMapper;

    @Autowired
    private TruckTypeMapper truckTypeMapper;

    @Autowired
    private ConfigurationMapper configurationMapper;

    //    @Autowired(required = false) // 如果 WebSocketServer 不是必须的依赖，可以设置为 false
    //    private WebSocketServer webSocketServer;

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
        // 如果搬运工人数量大于0且提供了有效值
        helperCost = perHelperCost.multiply(new BigDecimal(numberOfHelpers));

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

    // --- 未来订单提交 (Submit Order) 接口方法框架 (调用 calculatePrice) ---
    /*
    @Transactional
    public OrderSubmitVO submitOrder(OrderSubmitDTO submitDTO) {
         // ... (调用 calculatePrice 并在 Order 中设置价格)
         // ...
         return submitResultVO;
    }
    */


    // --- 辅助方法 ---
    // generateUniqueOrderNumber(), Constants, Mapper methods (getById, getTruckTypeById, getServiceCategoryById, insert, update etc.) 需要自行实现


}