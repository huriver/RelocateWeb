package com.***REMOVED***.service;

import com.***REMOVED***.dto.MovingNewsDTO;
import com.***REMOVED***.dto.MovingNewsPageQueryDTO;
import com.***REMOVED***.entity.MovingNews;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.vo.MovingNewsVO;

public interface MovingNewsService {
    PageResult pageQuery(MovingNewsPageQueryDTO movingNewsPageQueryDTO);

    // 管理员分页查询搬家新闻列表
    PageResult pageQueryByAdmin(MovingNewsPageQueryDTO pageQueryDTO);

    // 新增搬家新闻
    void save(MovingNewsDTO movingNewsDTO);

    // 公共-根据ID查询搬家新闻详情
    MovingNews getById(Long id);

    // 根据ID查询搬家新闻详情
    MovingNewsVO getByIdByAdmin(Long id);

    // 修改搬家新闻
    void update(MovingNewsDTO movingNewsDTO);

    // 根据ID删除搬家新闻
    void deleteById(Long id);

    // 发布/取消发布 搬家新闻
    void startOrStop(Long id, Integer isPublished);

}
