package com.***REMOVED***.mapper;

import com.***REMOVED***.dto.MovingTipsPageQueryDTO;
import com.***REMOVED***.entity.MovingTips;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovingTipsMapper {
    Page<MovingTips> pageQuery(MovingTipsPageQueryDTO movingTipsPageQueryDTO);

}
