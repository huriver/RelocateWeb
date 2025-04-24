package com.***REMOVED***.mapper;

import com.***REMOVED***.dto.MovingTipsPageQueryDTO;
import com.***REMOVED***.entity.MovingTips;
import com.***REMOVED***.vo.MovingTipsVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovingTipsMapper {
    Page<MovingTips> pageQuery(MovingTipsPageQueryDTO movingTipsPageQueryDTO);

    // 管理员分页查询搬家须知列表 (带条件查询，SELECT 所有字段+关联管理员姓名)
    Page<MovingTipsVO> pageQueryByAdmin(MovingTipsPageQueryDTO pageQueryDTO);


}
