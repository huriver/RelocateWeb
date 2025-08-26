package com.ahut.service;

import com.ahut.dto.MovingTipsDTO;
import com.ahut.dto.MovingTipsPageQueryDTO;
import com.ahut.entity.MovingTips;
import com.ahut.result.PageResult;
import com.ahut.vo.MovingTipsVO;

public interface MovingTipsService {
    PageResult pageQuery(MovingTipsPageQueryDTO movingTipsPageQueryDTO);

    // 管理员分页查询搬家须知列表
    PageResult pageQueryByAdmin(MovingTipsPageQueryDTO pageQueryDTO);

    // 新增搬家须知
    void save(MovingTipsDTO movingTipsDTO);

    // 公共-根据ID查询搬家须知详情
    MovingTips getById(Long id);

    // 根据ID查询搬家须知详情
    MovingTipsVO getByIdByAdmin(Long id);

    // 修改搬家须知
    void update(MovingTipsDTO movingTipsDTO);

    // 根据ID删除搬家须知
    void deleteById(Long id);

    // 发布/取消发布 搬家须知
    void startOrStop(Long id, Integer isPublished);

}
