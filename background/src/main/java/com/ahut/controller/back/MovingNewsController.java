package com.ahut.controller.back;

import com.ahut.dto.MovingNewsDTO;
import com.ahut.dto.MovingNewsPageQueryDTO;
import com.ahut.result.PageResult;
import com.ahut.result.Result;
import com.ahut.service.MovingNewsService;
import com.ahut.vo.MovingNewsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController("backMovingNewsController")
@RequestMapping("/back/moving-news")
@Slf4j
public class MovingNewsController {

    @Autowired
    private MovingNewsService movingNewsService;


    /**
     * 分页查询搬家新闻列表 (带条件查询)
     *
     * @param pageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(MovingNewsPageQueryDTO pageQueryDTO) {
        log.info("后台端搬家新闻分页查询: {}", pageQueryDTO);
        PageResult pageResult = movingNewsService.pageQueryByAdmin(pageQueryDTO);
        return Result.success(pageResult); // 返回包含所有字段+关联管理员姓名的分页结果
    }

    /**
     * 新增搬家新闻
     *
     * @param movingNewsDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody MovingNewsDTO movingNewsDTO) {
        log.info("后台端新增搬家新闻: {}", movingNewsDTO);
        movingNewsService.save(movingNewsDTO);
        return Result.success();
    }

    /**
     * 根据ID查询搬家新闻详情 (用于回显)
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<MovingNewsVO> getById(@PathVariable Long id) {
        log.info("后台端根据ID查询搬家新闻详情: {}", id);
        MovingNewsVO movingNewsVO = movingNewsService.getByIdByAdmin(id);
        return Result.success(movingNewsVO);
    }

    /**
     * 修改搬家新闻
     *
     * @param movingNewsDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody MovingNewsDTO movingNewsDTO) {
        log.info("后台端修改搬家新闻: {}", movingNewsDTO);
        movingNewsService.update(movingNewsDTO);
        return Result.success();
    }

    /**
     * 根据ID删除搬家新闻
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public Result deleteById(Long id) {
        log.info("后台端删除搬家新闻: {}", id);
        movingNewsService.deleteById(id);
        return Result.success();
    }

    /**
     * 发布/取消发布 搬家新闻
     *
     * @param isPublished
     * @param id
     * @return
     */
    @PostMapping("/status/{isPublished}")
    public Result startOrStop(@PathVariable Integer isPublished, Long id) {
        log.info("后台端发布/取消发布搬家新闻：ID {}, 状态 {}", id, isPublished);
        movingNewsService.startOrStop(id, isPublished);
        return Result.success();
    }

}
