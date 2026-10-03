package com.example.bloglite.service.impl;

import com.example.bloglite.config.UploadProperties;
import com.example.bloglite.dto.ValidatedImage;
import com.example.bloglite.enums.ImageFormat;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.service.ImageFileValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;

/**
 * 图片文件校验实现
 */
@Service
public class ImageFileValidatorImpl implements ImageFileValidator {

    private final UploadProperties uploadProperties;

    public ImageFileValidatorImpl(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @Override
    public ValidatedImage validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException(40000, "上传图片不能为空");
        }

        long maxFileSize = uploadProperties.getMaxFileSize().toBytes();

        if (file.getSize() > maxFileSize) {
            throw new BizException(
                    41300,
                    "单张图片不能超过 "
                            + uploadProperties.getMaxFileSize().toMegabytes()
                            + "MB"
            );
        }

        String originalName = normalizeOriginalName(file.getOriginalFilename());
        String extension = extractExtension(originalName);
        ImageFormat detectedFormat = detectFormat(file);

        if (!detectedFormat.supportsExtension(extension)) {
            throw new BizException(
                    40000,
                    "图片扩展名与真实格式不一致"
            );
        }

        if (file.getContentType() == null
                || !detectedFormat.getContentType()
                        .equalsIgnoreCase(file.getContentType())) {
            throw new BizException(
                    40000,
                    "图片 Content-Type 与真实格式不一致"
            );
        }

        ImageDimension dimension = readDimension(file, detectedFormat);
        long pixelCount = (long) dimension.width * dimension.height;

        if (pixelCount > uploadProperties.getMaxImagePixels()) {
            throw new BizException(
                    40000,
                    "图片像素总数不能超过 "
                            + uploadProperties.getMaxImagePixels()
            );
        }

        return new ValidatedImage(
                originalName,
                detectedFormat,
                file.getSize(),
                dimension.width,
                dimension.height
        );
    }

    private String normalizeOriginalName(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BizException(40000, "原始文件名不能为空");
        }

        String normalizedName = originalFilename
                .replace('\\', '/')
                .trim();

        int lastSlashIndex = normalizedName.lastIndexOf('/');

        if (lastSlashIndex >= 0) {
            normalizedName = normalizedName.substring(lastSlashIndex + 1);
        }

        if (normalizedName.isEmpty() || normalizedName.length() > 255) {
            throw new BizException(40000, "原始文件名不合法");
        }

        return normalizedName;
    }

    private String extractExtension(String originalName) {
        int dotIndex = originalName.lastIndexOf('.');

        if (dotIndex <= 0 || dotIndex == originalName.length() - 1) {
            throw new BizException(40000, "图片文件缺少扩展名");
        }

        String extension = originalName.substring(dotIndex + 1)
                .toLowerCase(Locale.ROOT);

        if (!extension.matches("[a-z0-9]{1,10}")) {
            throw new BizException(40000, "图片扩展名不合法");
        }

        return extension;
    }

    private ImageFormat detectFormat(MultipartFile file) {
        byte[] header = new byte[8];

        try (InputStream inputStream = file.getInputStream()) {
            int bytesRead = inputStream.read(header);

            if (bytesRead < 4) {
                throw new BizException(40000, "图片文件内容不完整");
            }

            if (isJpeg(header)) {
                return ImageFormat.JPEG;
            }

            if (isPng(header, bytesRead)) {
                return ImageFormat.PNG;
            }

            if (isGif(header, bytesRead)) {
                return ImageFormat.GIF;
            }

            throw new BizException(
                    40000,
                    "仅支持 JPG、PNG、GIF 图片"
            );
        } catch (IOException exception) {
            throw new BizException(40000, "读取上传图片失败", exception);
        }
    }

    private ImageDimension readDimension(
            MultipartFile file,
            ImageFormat expectedFormat
    ) {
        try (InputStream inputStream = file.getInputStream();
             ImageInputStream imageInputStream =
                     ImageIO.createImageInputStream(inputStream)) {

            if (imageInputStream == null) {
                throw new BizException(40000, "图片格式无法识别");
            }

            Iterator<ImageReader> readers =
                    ImageIO.getImageReaders(imageInputStream);

            if (!readers.hasNext()) {
                throw new BizException(40000, "图片格式无法识别");
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(imageInputStream, true, true);

                ImageFormat readerFormat = parseReaderFormat(
                        reader.getFormatName()
                );

                if (readerFormat != expectedFormat) {
                    throw new BizException(
                            40000,
                            "图片扩展名、文件头和解码格式不一致"
                    );
                }

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                if (width <= 0 || height <= 0) {
                    throw new BizException(40000, "图片尺寸不合法");
                }

                return new ImageDimension(width, height);
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new BizException(40000, "图片内容损坏或无法解析", exception);
        }
    }

    private ImageFormat parseReaderFormat(String formatName) {
        if (formatName == null) {
            throw new BizException(40000, "图片格式无法识别");
        }

        return switch (formatName.toLowerCase(Locale.ROOT)) {
            case "jpeg", "jpg" -> ImageFormat.JPEG;
            case "png" -> ImageFormat.PNG;
            case "gif" -> ImageFormat.GIF;
            default -> throw new BizException(
                    40000,
                    "不支持的图片解码格式：" + formatName
            );
        };
    }

    private boolean isJpeg(byte[] header) {
        return header.length >= 3
                && (header[0] & 0xFF) == 0xFF
                && (header[1] & 0xFF) == 0xD8
                && (header[2] & 0xFF) == 0xFF;
    }

    private boolean isPng(byte[] header, int length) {
        return length >= 8
                && (header[0] & 0xFF) == 0x89
                && header[1] == 0x50
                && header[2] == 0x4E
                && header[3] == 0x47
                && header[4] == 0x0D
                && header[5] == 0x0A
                && header[6] == 0x1A
                && header[7] == 0x0A;
    }

    private boolean isGif(byte[] header, int length) {
        return length >= 6
                && header[0] == 0x47
                && header[1] == 0x49
                && header[2] == 0x46
                && header[3] == 0x38
                && (header[4] == 0x37 || header[4] == 0x39)
                && header[5] == 0x61;
    }

    /**
     * 图片尺寸
     */
    private record ImageDimension(int width, int height) {
    }
}
