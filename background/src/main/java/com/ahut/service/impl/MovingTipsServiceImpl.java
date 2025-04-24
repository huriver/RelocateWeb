package com.***REMOVED***.service.impl;

import com.***REMOVED***.dto.MovingTipsPageQueryDTO;
import com.***REMOVED***.entity.MovingTips;
import com.***REMOVED***.mapper.MovingTipsMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.MovingTipsService;
import com.***REMOVED***.vo.MovingTipsVO;
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

    /**
     * 管理员分页查询搬家须知列表
     *
     * @param pageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQueryByAdmin(MovingTipsPageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<MovingTipsVO> page = movingTipsMapper.pageQueryByAdmin(pageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
