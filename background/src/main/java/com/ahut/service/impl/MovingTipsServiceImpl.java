package com.***REMOVED***.service.impl;

import com.***REMOVED***.constant.isPublishedConstant;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.MovingTipsDTO;
import com.***REMOVED***.dto.MovingTipsPageQueryDTO;
import com.***REMOVED***.entity.MovingTips;
import com.***REMOVED***.mapper.MovingTipsMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.MovingTipsService;
import com.***REMOVED***.vo.MovingTipsVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
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
     * 公共-根据ID查询搬家须知详情
     *
     * @param id
     * @return
     */
    @Override
    public MovingTips getById(Long id) {
        return movingTipsMapper.getById(id);
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

    /**
     * 新增搬家须知
     *
     * @param movingTipsDTO
     */
    @Override
    public void save(MovingTipsDTO movingTipsDTO) {
        // 将 DTO 对象属性拷贝到实体类对象
        MovingTips movingTips = new MovingTips();
        BeanUtils.copyProperties(movingTipsDTO, movingTips);

        movingTips.setIsPublished(false);   // 默认为未发布
        movingTips.setCreateUser(BaseContext.getCurrentId());
        movingTips.setUpdateUser(BaseContext.getCurrentId());
        movingTipsMapper.insert(movingTips);
    }

    /**
     * 根据ID查询搬家须知详情
     *
     * @param id
     * @return
     */
    @Override
    public MovingTipsVO getByIdByAdmin(Long id) {
        return movingTipsMapper.getByIdByAdmin(id);
    }

    /**
     * 修改搬家须知
     *
     * @param movingTipsDTO
     */
    @Override
    public void update(MovingTipsDTO movingTipsDTO) {
        // 将 DTO 对象属性拷贝到实体类对象
        MovingTips movingTips = new MovingTips();
        BeanUtils.copyProperties(movingTipsDTO, movingTips);

        movingTips.setUpdateUser(BaseContext.getCurrentId());
        movingTipsMapper.update(movingTips);
    }

    /**
     * 根据ID删除搬家须知
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        movingTipsMapper.deleteById(id);
    }

    /**
     * 发布/取消发布 搬家须知
     *
     * @param id
     * @param isPublished
     */
    @Override
    public void startOrStop(Long id, Integer isPublished) {
        MovingTips movingTips = MovingTips.builder()
                .id(id)
                .isPublished(isPublished == isPublishedConstant.ENABLE)
                .updateUser(BaseContext.getCurrentId())
                .build();

        movingTipsMapper.update(movingTips);
    }

}
