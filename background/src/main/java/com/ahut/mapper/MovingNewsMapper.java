package com.ahut.mapper;

import com.ahut.dto.MovingNewsPageQueryDTO;
import com.ahut.entity.MovingNews;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovingNewsMapper {
    Page<MovingNews> pageQuery(MovingNewsPageQueryDTO movingNewsPageQueryDTO);

}
