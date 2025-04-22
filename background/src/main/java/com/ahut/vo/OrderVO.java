package com.***REMOVED***.vo;

import com.***REMOVED***.entity.MovingOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


/**
 * 用户订单列表项/详情VO
 * 继承 MovingOrder，并添加用于列表/详情展示的关联信息字段 (包括搬运工人列表)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO extends MovingOrder {
    private String orderStatusDescription; // 订单状态文字描述
    private String isPaidDescription;      // 支付状态文字描述
    private String payMethodDescription;   // 支付方式文字描述


    private String serviceName; // 服务项名称 (用于列表显示)
    private String truckTypeName; // 货车类型名称 (用于列表显示)
    private String serviceCategoryName; // 服务分类名称 (用于解释乘数)

    // 司机和车辆显示信息
    private String driverName;  // 司机姓名
    private String driverPhone; // 司机电话 (如果驱动表包含)
    private String vehiclePlateNumber; // 车牌号

    private List<MoverVO> moverList; // 分配给该订单的搬运工人列表
}