package com.***REMOVED***.mapper;

import com.***REMOVED***.dto.MovingNewsPageQueryDTO;
import com.***REMOVED***.entity.MovingNews;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovingNewsMapper {
    Page<MovingNews> pageQuery(MovingNewsPageQueryDTO movingNewsPageQueryDTO);

}
