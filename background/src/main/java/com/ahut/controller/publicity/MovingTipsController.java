package com.***REMOVED***.controller.publicity;

import com.***REMOVED***.dto.MovingTipsPageQueryDTO;
import com.***REMOVED***.entity.MovingTips;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.MovingTipsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController("publicMovingTipsController")
@RequestMapping("/public/moving-tips")
@Slf4j
public class MovingTipsController {

    @Autowired
    private MovingTipsService movingTipsService;


    @GetMapping("/page")
    public Result<PageResult> page(MovingTipsPageQueryDTO movingTipsPageQueryDTO) {
        movingTipsPageQueryDTO.setIsPublished(true);
        log.info("用户端搬家须知分页查询，参数为:{}", movingTipsPageQueryDTO);
        PageResult pageResult = movingTipsService.pageQuery(movingTipsPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询搬家须知详情
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<MovingTips> getById(@PathVariable Long id) {
        log.info("用户端根据ID查询搬家须知详情: {}", id);
        MovingTips movingTips = movingTipsService.getById(id);
        return Result.success(movingTips);
    }


}
