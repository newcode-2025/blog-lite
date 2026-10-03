package com.example.bloglite.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 上传目录初始化器
 */
@Slf4j
@Component
public class UploadDirectoryInitializer {

    private final UploadProperties uploadProperties;

    public UploadDirectoryInitializer(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    /**
     * 应用启动时创建上传根目录并校验其可写性
     */
    @PostConstruct
    public void initialize() {
        Path rootPath = Paths.get(uploadProperties.getLocalDir())
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(rootPath);

            if (!Files.isDirectory(rootPath) || !Files.isWritable(rootPath)) {
                throw new IllegalStateException(
                        "上传目录不存在或不可写：" + rootPath
                );
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "创建上传目录失败：" + rootPath,
                    exception
            );
        }

        log.info("本地上传目录初始化完成：{}", rootPath);
    }
}
