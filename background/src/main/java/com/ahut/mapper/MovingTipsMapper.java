package com.ahut.mapper;

import com.ahut.dto.MovingTipsPageQueryDTO;
import com.ahut.entity.MovingTips;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovingTipsMapper {
    Page<MovingTips> pageQuery(MovingTipsPageQueryDTO movingTipsPageQueryDTO);

}
