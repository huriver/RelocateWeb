package com.ahut.controller.back;

import com.ahut.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class testController {

    @GetMapping("/test")
    public Result test(){
        log.info("test");
        return Result.success("test");
    }
}
