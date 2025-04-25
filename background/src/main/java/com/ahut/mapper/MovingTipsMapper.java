package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.MovingTipsPageQueryDTO;
import com.***REMOVED***.entity.MovingTips;
import com.***REMOVED***.enumeration.OperationType;
import com.***REMOVED***.vo.MovingTipsVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MovingTipsMapper {
    Page<MovingTips> pageQuery(MovingTipsPageQueryDTO movingTipsPageQueryDTO);

    // 公共-根据ID查询搬家须知详情
    @Select("SELECT id, title, content, category, publish_date, is_published, create_time, update_time, " +
            "create_user, update_user FROM moving_tips WHERE id = #{id}")
    MovingTips getById(Long id);

    // 管理员分页查询搬家须知列表 (带条件查询，SELECT 所有字段+关联管理员姓名)
    Page<MovingTipsVO> pageQueryByAdmin(MovingTipsPageQueryDTO pageQueryDTO);

    // 新增搬家须知
    @Insert("insert into moving_tips (title, content, category, publish_date, is_published, create_time, " +
            "update_time, create_user, update_user) " +
            "values (#{title}, #{content}, #{category}, #{publishDate}, #{isPublished}, #{createTime}, " +
            "#{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(MovingTips movingTips);

    // 根据ID查询搬家须知详情 (SELECT 所有字段 + 关联管理员姓名)
    MovingTipsVO getByIdByAdmin(Long id);

    // 根据ID更新搬家须知信息
    @AutoFill(value = OperationType.UPDATE)
    void update(MovingTips movingTips);

    // 根据ID删除搬家须知数据
    @Delete("DELETE FROM moving_tips WHERE id = #{id}")
    void deleteById(Long id);


}
