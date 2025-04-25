package com.***REMOVED***.service.impl;

import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.MovingNewsDTO;
import com.***REMOVED***.dto.MovingNewsPageQueryDTO;
import com.***REMOVED***.entity.MovingNews;
import com.***REMOVED***.mapper.MovingNewsMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.MovingNewsService;
import com.***REMOVED***.vo.MovingNewsVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
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

    // 公共-根据ID查询搬家新闻详情
    @Override
    public MovingNews getById(Long id) {
        return movingNewsMapper.getById(id);
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

    /**
     * 新增搬家新闻
     *
     * @param movingNewsDTO
     */
    @Override
    public void save(MovingNewsDTO movingNewsDTO) {
        // 将 DTO 对象属性拷贝到实体类对象
        MovingNews movingNews = new MovingNews();
        BeanUtils.copyProperties(movingNewsDTO, movingNews);

        movingNews.setCreateUser(BaseContext.getCurrentId());
        movingNews.setUpdateUser(BaseContext.getCurrentId());
        movingNewsMapper.insert(movingNews);
    }

    /**
     * 根据ID查询搬家新闻详情
     *
     * @param id
     * @return
     */
    @Override
    public MovingNewsVO getByIdByAdmin(Long id) {
        return movingNewsMapper.getByIdByAdmin(id);
    }

    /**
     * 修改搬家新闻
     *
     * @param movingNewsDTO
     */
    @Override
    public void update(MovingNewsDTO movingNewsDTO) {
        // 将 DTO 对象属性拷贝到实体类对象
        MovingNews movingNews = new MovingNews();
        BeanUtils.copyProperties(movingNewsDTO, movingNews);

        movingNews.setUpdateUser(BaseContext.getCurrentId());
        movingNewsMapper.update(movingNews);
    }

    /**
     * 根据ID删除搬家新闻
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        movingNewsMapper.deleteById(id);
    }

}
