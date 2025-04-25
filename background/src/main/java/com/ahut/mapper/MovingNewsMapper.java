package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.MovingNewsPageQueryDTO;
import com.***REMOVED***.entity.MovingNews;
import com.***REMOVED***.enumeration.OperationType;
import com.***REMOVED***.vo.MovingNewsVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MovingNewsMapper {
    Page<MovingNews> pageQuery(MovingNewsPageQueryDTO movingNewsPageQueryDTO);

    // 公共-根据ID查询搬家新闻详情
    @Select("SELECT id, title, content, publish_date, is_published, create_time, update_time, create_user, " +
            "update_user FROM moving_news WHERE id = #{id}")
    MovingNews getById(Long id);

    // 管理员分页查询搬家新闻列表 (带条件查询，SELECT 所有字段+关联管理员姓名)
    Page<MovingNewsVO> pageQueryByAdmin(MovingNewsPageQueryDTO pageQueryDTO);

    // 新增搬家新闻
    @Insert("insert into moving_news (title, content, publish_date, is_published, create_time, update_time, " +
            "create_user, update_user) " +
            "values (#{title}, #{content}, #{publishDate}, #{isPublished}, #{createTime}, #{updateTime}, " +
            "#{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(MovingNews movingNews);


    // 根据ID查询搬家新闻详情 (SELECT 所有字段 + 关联管理员姓名)
    MovingNewsVO getByIdByAdmin(Long id);

    // 根据ID更新搬家新闻信息
    @AutoFill(value = OperationType.UPDATE)
    void update(MovingNews movingNews);

    // 根据ID删除搬家新闻数据
    @Delete("DELETE FROM moving_news WHERE id = #{id}")
    void deleteById(Long id);

}
