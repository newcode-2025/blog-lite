package com.example.bloglite.service;

import com.example.bloglite.enums.MediaScene;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

/**
 * 本地媒体文件存储接口
 */
public interface MediaFileStorage {

    /**
     * 保存文件并返回相对于上传根目录的路径
     */
    String store(
            InputStream inputStream,
            MediaScene scene,
            String extension
    );

    /**
     * 删除相对路径对应的文件
     */
    void delete(String relativePath);

    /**
     * 安全解析相对路径
     */
    Optional<Path> resolve(String relativePath);
}
