package com.***REMOVED***.mapper;

import com.***REMOVED***.dto.MovingNewsPageQueryDTO;
import com.***REMOVED***.entity.MovingNews;
import com.***REMOVED***.vo.MovingNewsVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovingNewsMapper {
    Page<MovingNews> pageQuery(MovingNewsPageQueryDTO movingNewsPageQueryDTO);

    // 管理员分页查询搬家新闻列表 (带条件查询，SELECT 所有字段+关联管理员姓名)
    Page<MovingNewsVO> pageQueryByAdmin(MovingNewsPageQueryDTO pageQueryDTO);

}
