package com.example.bloglite.service.impl;

import com.example.bloglite.config.UploadProperties;
import com.example.bloglite.dto.MediaUploadResultDTO;
import com.example.bloglite.enums.MediaScene;
import com.example.bloglite.service.ImageFileValidator;
import com.example.bloglite.service.MediaFileStorage;
import com.example.bloglite.service.MediaStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 媒体上传业务测试
 */
class MediaStorageServiceImplTest {

    private Path temporaryDirectory;

    private MediaStorageService mediaStorageService;

    @BeforeEach
    void setUp() throws Exception {
        temporaryDirectory = Path.of(
                "target",
                "media-service-tests",
                UUID.randomUUID().toString()
        );
        Files.createDirectories(temporaryDirectory);

        UploadProperties uploadProperties = new UploadProperties();
        uploadProperties.setLocalDir(temporaryDirectory.toString());
        uploadProperties.setPublicPath("/uploads");
        uploadProperties.setMaxFileSize(DataSize.ofMegabytes(10));
        uploadProperties.setMaxBatchCount(9);
        uploadProperties.setMaxImagePixels(40_000_000L);

        ImageFileValidator validator =
                new ImageFileValidatorImpl(uploadProperties);
        MediaFileStorage storage =
                new LocalMediaFileStorageServiceImpl(uploadProperties);

        mediaStorageService = new MediaStorageServiceImpl(
                validator,
                storage,
                uploadProperties
        );
    }

    @Test
    void shouldUploadSingleImageIntoProjectUploadDirectory() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cover.png",
                "image/png",
                createPngBytes()
        );

        MediaUploadResultDTO result = mediaStorageService.upload(
                file,
                MediaScene.ARTICLE
        );

        assertTrue(result.getUrl().startsWith("/uploads/article/"));
        assertTrue(result.getUrl().endsWith(".png"));
        assertTrue(Files.exists(
                temporaryDirectory.resolve(
                        result.getUrl().substring("/uploads/".length())
                )
        ));
        assertEquals("cover.png", result.getOriginalName());
        assertEquals("image/png", result.getContentType());
    }

    private byte[] createPngBytes() throws Exception {
        BufferedImage image = new BufferedImage(
                1,
                1,
                BufferedImage.TYPE_INT_ARGB
        );

        try (ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        }
    }

    @Test
    void shouldUploadBatchImagesWithoutOverwriting() throws Exception {
        MockMultipartFile first = new MockMultipartFile(
                "files",
                "first.png",
                "image/png",
                createPngBytes()
        );
        MockMultipartFile second = new MockMultipartFile(
                "files",
                "second.png",
                "image/png",
                createPngBytes()
        );

        List<MediaUploadResultDTO> results = mediaStorageService.uploadBatch(
                List.of(first, second),
                MediaScene.TALK
        );

        assertEquals(2, results.size());
        assertNotEquals(results.get(0).getUrl(), results.get(1).getUrl());
        assertTrue(Files.exists(temporaryDirectory.resolve(
                results.get(0).getUrl().substring("/uploads/".length())
        )));
        assertTrue(Files.exists(temporaryDirectory.resolve(
                results.get(1).getUrl().substring("/uploads/".length())
        )));
    }

    @Test
    void shouldRejectBatchBeforeWritingWhenOneImageIsInvalid() throws Exception {
        MockMultipartFile valid = new MockMultipartFile(
                "files",
                "valid.png",
                "image/png",
                createPngBytes()
        );
        MockMultipartFile invalid = new MockMultipartFile(
                "files",
                "invalid.png",
                "image/png",
                "not-an-image".getBytes()
        );

        assertThrows(
                com.example.bloglite.exception.BizException.class,
                () -> mediaStorageService.uploadBatch(
                        List.of(valid, invalid),
                        MediaScene.TALK
                )
        );

        long storedFileCount;
        try (var paths = Files.walk(temporaryDirectory)) {
            storedFileCount = paths
                    .filter(Files::isRegularFile)
                    .count();
        }

        assertEquals(0, storedFileCount);
    }}
