package com.arttraining.business.controller;

import cn.hutool.core.util.IdUtil;
import com.arttraining.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "文件上传")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${business.upload.path:./uploads}")
    private String uploadPath;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<List<Map<String, String>>> upload(@RequestParam("files") MultipartFile[] files) {
        List<Map<String, String>> result = new ArrayList<>();

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path dirPath = Paths.get(uploadPath, dateDir);

        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            log.error("创建上传目录失败", e);
            return Result.fail("创建上传目录失败");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = IdUtil.simpleUUID() + ext;

            try {
                Path filePath = dirPath.resolve(newFileName);
                file.transferTo(filePath.toFile());

                Map<String, String> fileInfo = new HashMap<>();
                fileInfo.put("name", originalFilename);
                fileInfo.put("url", "/uploads/" + dateDir + "/" + newFileName);
                fileInfo.put("size", String.valueOf(file.getSize()));
                result.add(fileInfo);
            } catch (IOException e) {
                log.error("文件上传失败", e);
            }
        }

        return Result.success(result);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping
    public Result<Void> delete(@RequestParam String url) {
        try {
            if (url.startsWith("/uploads/")) {
                String relativePath = url.substring("/uploads/".length());
                Path filePath = Paths.get(uploadPath, relativePath);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            log.error("删除文件失败", e);
            return Result.fail("删除文件失败");
        }
        return Result.success();
    }
}
