package com.ahut.controller.back;

import com.ahut.dto.MovingTipsDTO;
import com.ahut.dto.MovingTipsPageQueryDTO;
import com.ahut.result.PageResult;
import com.ahut.result.Result;
import com.ahut.service.MovingTipsService;
import com.ahut.vo.MovingTipsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController("backMovingTipsController")
@RequestMapping("/back/moving-tips")
@Slf4j
public class MovingTipsController {

    @Autowired
    private MovingTipsService movingTipsService;

    /**
     * 分页查询搬家须知列表 (带条件查询)
     *
     * @param movingTipsPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(MovingTipsPageQueryDTO movingTipsPageQueryDTO) {
        log.info("后台端搬家须知分页查询，参数为:{}", movingTipsPageQueryDTO);
        PageResult pageResult = movingTipsService.pageQueryByAdmin(movingTipsPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增搬家须知
     *
     * @param movingTipsDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody MovingTipsDTO movingTipsDTO) {
        log.info("后台端新增搬家须知: {}", movingTipsDTO);
        movingTipsService.save(movingTipsDTO);
        return Result.success();
    }

    /**
     * 根据ID查询搬家须知详情 (用于回显)
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<MovingTipsVO> getById(@PathVariable Long id) {
        log.info("后台端根据ID查询搬家须知详情: {}", id);
        MovingTipsVO movingTipsVO = movingTipsService.getByIdByAdmin(id);
        return Result.success(movingTipsVO);
    }

    /**
     * 修改搬家须知
     *
     * @param movingTipsDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody MovingTipsDTO movingTipsDTO) {
        log.info("后台端修改搬家须知: {}", movingTipsDTO);
        movingTipsService.update(movingTipsDTO);
        return Result.success();
    }

    /**
     * 根据ID删除搬家须知
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public Result deleteById(Long id) {
        log.info("后台端删除搬家须知: {}", id);
        movingTipsService.deleteById(id);
        return Result.success();
    }

    /**
     * 发布/取消发布 搬家须知
     *
     * @param isPublished
     * @param id
     * @return
     */
    @PostMapping("/status/{isPublished}")
    public Result startOrStop(@PathVariable Integer isPublished, Long id) {
        log.info("后台端发布/取消发布搬家须知：ID {}, 状态 {}", id, isPublished);
        movingTipsService.startOrStop(id, isPublished);
        return Result.success();
    }

}
