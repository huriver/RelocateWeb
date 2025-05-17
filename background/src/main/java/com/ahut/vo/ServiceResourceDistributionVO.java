package com.***REMOVED***.vo; // 请替换为实际包名

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 服务/货车类型使用分布数据VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResourceDistributionVO implements Serializable {

    // 注意：以下字段已按照逻辑和常见的展示顺序进行了排序。

    private String name;    // 服务项名称 或 货车类型名称
    private Long count;     // 使用次数 (订单数量)

}