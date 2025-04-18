package com.ahut.service.impl;

import com.ahut.dto.MovingNewsPageQueryDTO;
import com.ahut.entity.MovingNews;
import com.ahut.mapper.MovingNewsMapper;
import com.ahut.result.PageResult;
import com.ahut.service.MovingNewsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovingNewsServiceImpl implements MovingNewsService {

    @Autowired
    private MovingNewsMapper movingNewsMapper;

    @Override
    public PageResult pageQuery(MovingNewsPageQueryDTO movingNewsPageQueryDTO) {
        PageHelper.startPage(movingNewsPageQueryDTO.getPage(), movingNewsPageQueryDTO.getPageSize());
        //下一条sql进行分页，自动加入limit关键字分页
        Page<MovingNews> page = movingNewsMapper.pageQuery(movingNewsPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
}
