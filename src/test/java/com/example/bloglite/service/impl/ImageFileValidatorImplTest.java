package com.example.bloglite.service.impl;

import com.example.bloglite.config.UploadProperties;
import com.example.bloglite.dto.ValidatedImage;
import com.example.bloglite.enums.ImageFormat;
import com.example.bloglite.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 图片上传校验测试
 */
class ImageFileValidatorImplTest {

    private UploadProperties uploadProperties;

    private ImageFileValidatorImpl validator;

    @BeforeEach
    void setUp() {
        uploadProperties = new UploadProperties();
        uploadProperties.setMaxFileSize(DataSize.ofMegabytes(10));
        uploadProperties.setMaxImagePixels(40_000_000L);
        validator = new ImageFileValidatorImpl(uploadProperties);
    }

    @Test
    void shouldAcceptValidPngImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cover.png",
                "image/png",
                createPngBytes()
        );

        ValidatedImage result = validator.validate(file);

        assertEquals(ImageFormat.PNG, result.getFormat());
        assertEquals(1, result.getWidth());
        assertEquals(1, result.getHeight());
    }

    @Test
    void shouldRejectExtensionMismatch() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cover.jpg",
                "image/png",
                createPngBytes()
        );

        BizException exception = assertThrows(
                BizException.class,
                () -> validator.validate(file)
        );

        assertEquals(40000, exception.getCode());
    }

    @Test
    void shouldRejectCorruptedImage() {
        byte[] corruptedPng = new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x01, 0x02
        };

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "broken.png",
                "image/png",
                corruptedPng
        );

        assertThrows(BizException.class, () -> validator.validate(file));
    }

    @Test
    void shouldRejectFileLargerThanConfiguredLimit() {
        uploadProperties.setMaxFileSize(DataSize.ofBytes(1));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cover.png",
                "image/png",
                new byte[]{1, 2, 3}
        );

        BizException exception = assertThrows(
                BizException.class,
                () -> validator.validate(file)
        );

        assertEquals(41300, exception.getCode());
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
}
