package com.ahut.service.impl;

import com.ahut.dto.MovingTipsPageQueryDTO;
import com.ahut.entity.MovingTips;
import com.ahut.mapper.MovingTipsMapper;
import com.ahut.result.PageResult;
import com.ahut.service.MovingTipsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovingTipsServiceImpl implements MovingTipsService {

    @Autowired
    private MovingTipsMapper movingTipsMapper;

    @Override
    public PageResult pageQuery(MovingTipsPageQueryDTO movingTipsPageQueryDTO) {
        PageHelper.startPage(movingTipsPageQueryDTO.getPage(), movingTipsPageQueryDTO.getPageSize());
        //下一条sql进行分页，自动加入limit关键字分页
        Page<MovingTips> page = movingTipsMapper.pageQuery(movingTipsPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
}
