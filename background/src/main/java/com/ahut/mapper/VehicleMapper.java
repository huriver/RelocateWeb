package com.***REMOVED***.mapper;

import com.***REMOVED***.dto.VehiclePageQueryDTO;
import com.***REMOVED***.vo.VehicleVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VehicleMapper {

    // 管理员分页查询车辆列表 (带条件查询，SELECT 所有字段+关联司机/货车/管理员姓名)
    Page<VehicleVO> pageQuery(VehiclePageQueryDTO pageQueryDTO);

}