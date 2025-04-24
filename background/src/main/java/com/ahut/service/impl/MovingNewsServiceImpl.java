package com.***REMOVED***.service.impl;

import com.***REMOVED***.dto.MovingNewsPageQueryDTO;
import com.***REMOVED***.entity.MovingNews;
import com.***REMOVED***.mapper.MovingNewsMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.MovingNewsService;
import com.***REMOVED***.vo.MovingNewsVO;
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

    /**
     * 管理员分页查询搬家新闻列表
     *
     * @param pageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQueryByAdmin(MovingNewsPageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<MovingNewsVO> page = movingNewsMapper.pageQueryByAdmin(pageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
