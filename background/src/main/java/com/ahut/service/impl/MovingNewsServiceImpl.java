package com.***REMOVED***.service.impl;

import com.***REMOVED***.dto.MovingNewsPageQueryDTO;
import com.***REMOVED***.entity.MovingNews;
import com.***REMOVED***.mapper.MovingNewsMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.MovingNewsService;
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
