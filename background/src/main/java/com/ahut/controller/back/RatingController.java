package com.***REMOVED***.controller.back;


import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.RatingPageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.RatingService;
import com.***REMOVED***.vo.RatingDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端评分接口
 */
@RestController("backRatingController")
@RequestMapping("/back/rating")
@Slf4j
public class RatingController {

    @Autowired
    private RatingService ratingService;

    /**
     * 分页查询评分列表
     *
     * @param queryDTO 包含分页和过滤条件
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(RatingPageQueryDTO queryDTO) {
        log.info("分页查询评分列表：由管理员 {} 操作，参数：{}", BaseContext.getCurrentId(), queryDTO);
        PageResult pageResult = ratingService.pageQuery(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据 ID 查询单个评分详细信息
     *
     * @param id 评分记录ID (从路径变量获取)
     * @return 包含评分详细信息的 Result<RatingDetailVO>
     */
    @GetMapping("/{id}")
    public Result<RatingDetailVO> getRatingDetailsById(@PathVariable Long id) {
        log.info("查询单个评分详细信息，由管理员 {} 操作，查询ID：{}", BaseContext.getCurrentId(), id);
        RatingDetailVO detailVO = ratingService.getById(id);
        return Result.success(detailVO);
    }

}