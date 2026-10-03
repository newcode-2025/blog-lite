package com.example.bloglite.service.impl;

import com.example.bloglite.config.UploadProperties;
import com.example.bloglite.enums.MediaScene;
import com.example.bloglite.service.MediaFileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 本地媒体文件存储测试
 */
class LocalMediaFileStorageServiceImplTest {

    private Path temporaryDirectory;

    @BeforeEach
    void createTemporaryDirectory() throws Exception {
        temporaryDirectory = Path.of(
                "target",
                "media-storage-tests",
                UUID.randomUUID().toString()
        );
        Files.createDirectories(temporaryDirectory);
    }

    @Test
    void shouldStoreAndDeleteFileWithinRootDirectory() throws Exception {
        UploadProperties uploadProperties = new UploadProperties();
        uploadProperties.setLocalDir(temporaryDirectory.toString());

        MediaFileStorage storage =
                new LocalMediaFileStorageServiceImpl(uploadProperties);

        String relativePath = storage.store(
                new ByteArrayInputStream(
                        "stage-six-storage-test".getBytes(StandardCharsets.UTF_8)
                ),
                MediaScene.ARTICLE,
                "png"
        );

        Path storedFile = temporaryDirectory.resolve(relativePath);

        assertTrue(relativePath.startsWith("article/"));
        assertTrue(relativePath.endsWith(".png"));
        assertTrue(Files.exists(storedFile));

        storage.delete(relativePath);
        assertFalse(Files.exists(storedFile));
    }

    @Test
    void shouldRejectPathOutsideRootDirectory() {
        UploadProperties uploadProperties = new UploadProperties();
        uploadProperties.setLocalDir(temporaryDirectory.toString());

        MediaFileStorage storage =
                new LocalMediaFileStorageServiceImpl(uploadProperties);

        assertTrue(storage.resolve("../outside.png").isEmpty());
    }
}


