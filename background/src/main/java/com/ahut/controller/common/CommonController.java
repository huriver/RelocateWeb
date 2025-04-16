package com.ahut.controller.common;

import com.ahut.constant.JwtClaimsConstant;
import com.ahut.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${upload.image.base-path}")
    private String uploadBasePath;

    @Value("${upload.image.base-url}")
    private String imageBaseUrl;

    @PostMapping("/driver/image")
    public Result<String> uploadDriverImage(MultipartFile file) throws Exception {
        return uploadImage(file, JwtClaimsConstant.ROLE_DRIVER);
    }

    @PostMapping("/mover/image")
    public Result<String> uploadMoverImage(MultipartFile file) throws Exception {
        return uploadImage(file, JwtClaimsConstant.ROLE_MOVER);
    }

    @PostMapping("/customer/image")
    public Result<String> uploadCustomerImage(MultipartFile file) throws Exception {
        return uploadImage(file, JwtClaimsConstant.ROLE_CUSTOMER);
    }

    private Result<String> uploadImage(MultipartFile file, String subdirectory) throws Exception {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + fileExtension;

        Path uploadDir = Paths.get(uploadBasePath, subdirectory);

        // 确保目录存在，如果不存在则创建
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path dest = uploadDir.resolve(filename);

        // 将文件内容传输到本地磁盘
        file.transferTo(dest.toFile());

        // 构建图片访问 URL
        String url = imageBaseUrl + subdirectory + "/" + filename;
        return Result.success(url);
    }

}
