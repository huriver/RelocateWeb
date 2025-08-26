package com.ahut.controller.common;

import com.ahut.context.BaseContext;
import com.ahut.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
public class FileUploadController {

    @Value("${upload.image.base-path}")
    private String uploadBasePath;

    @Value("${upload.image.base-url}")
    private String imageBaseUrl;

    /**
     * 后台用户图片上传接口
     * 由 JwtTokenBackInterceptor 拦截并认证
     * 拦截器将后台用户角色 (admin, driver, mover) 设置到 BaseContext
     * 接口路径: /back/upload
     *
     * @param file 上传的文件
     * @return 图片访问 URL
     */
    @PostMapping("/back/upload") // 新的后台上传接口路径
    public Result<String> backUploadImage(MultipartFile file) {
        log.info("后台文件上传请求接收: {}", file.getOriginalFilename());
        // 调用通用的内部处理方法
        return handleFileUploadRequest(file);
    }

    /**
     * 前端用户图片上传接口
     * 由 JwtTokenFrontInterceptor 拦截并认证
     * 拦截器将前端用户角色 (consumer) 设置到 BaseContext
     * 接口路径: /front/upload
     *
     * @param file 上传的文件
     * @return 图片访问 URL
     */
    @PostMapping("/front/upload")
    public Result<String> frontUploadImage(@RequestParam("file") MultipartFile file) {
        log.info("前端文件上传请求接收: {}", file.getOriginalFilename());
        // 调用通用的内部处理方法
        return handleFileUploadRequest(file);
    }

    /**
     * 内部方法: 处理文件上传的通用核心逻辑
     * 从 BaseContext 获取角色作为子目录名，进行文件保存并返回 URL
     *
     * @param file 上传文件
     * @return 处理结果 Result<String> (String 为图片URL)
     */
    private Result<String> handleFileUploadRequest(MultipartFile file) {
        log.info("文件上传核心逻辑处理中...");

        // 从 BaseContext 中获取当前用户的角色，用于确定上传到哪个子目录
        String subdirectory = BaseContext.getCurrentUserRole();

        if (subdirectory == null || subdirectory.trim().isEmpty()) {
            log.error("BaseContext中用户角色缺失或为空，无法确定上传子目录");
            return Result.error("用户身份信息异常或丢失，文件上传失败");
        }

        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            int dotIndex = originalFilename != null ? originalFilename.lastIndexOf(".") : -1;
            if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                fileExtension = originalFilename.substring(dotIndex);
            } else {
                log.warn("上传文件没有扩展名: {}", originalFilename);
            }

            String filename = UUID.randomUUID().toString() + fileExtension;

            Path uploadDir = Paths.get(uploadBasePath, subdirectory);
            log.info("上传目标目录: {}", uploadDir.toAbsolutePath());

            Files.createDirectories(uploadDir);
            log.info("上传目录已确认/创建: {}", uploadDir.toAbsolutePath());

            Path dest = uploadDir.resolve(filename);
            log.info("目标文件路径: {}", dest.toAbsolutePath());

            file.transferTo(dest.toFile());
            log.info("文件 {} 保存成功", originalFilename);

            String baseUrl = imageBaseUrl.endsWith("/") ? imageBaseUrl : imageBaseUrl + "/";
            String fileUrl = baseUrl + subdirectory + "/" + filename;
            log.info("文件上传成功，访问URL: {}", fileUrl);

            return Result.success(fileUrl);

        } catch (IllegalArgumentException e) {
            log.error("文件上传参数或逻辑异常: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (IOException e) {
            log.error("文件上传IO错误", e);
            return Result.error("文件保存失败");
        } catch (Exception e) {
            log.error("文件上传发生未知错误", e);
            return Result.error("文件上传过程中发生错误");
        }
    }

}