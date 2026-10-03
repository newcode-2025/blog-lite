package com.example.bloglite.service.impl;

import com.example.bloglite.config.UploadProperties;
import com.example.bloglite.enums.MediaScene;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.service.MediaFileStorage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * 本地媒体文件存储实现
 */
@Service
public class LocalMediaFileStorageServiceImpl implements MediaFileStorage {

    private static final DateTimeFormatter YEAR_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy");

    private static final DateTimeFormatter MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("MM");

    private final Path rootPath;

    public LocalMediaFileStorageServiceImpl(
            UploadProperties uploadProperties
    ) {
        this.rootPath = Paths.get(uploadProperties.getLocalDir())
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public String store(
            InputStream inputStream,
            MediaScene scene,
            String extension
    ) {
        if (inputStream == null) {
            throw new BizException(40000, "上传文件输入流不能为空");
        }

        if (scene == null) {
            throw new BizException(40000, "上传场景不能为空");
        }

        String normalizedExtension = normalizeExtension(extension);
        LocalDate today = LocalDate.now();

        String relativeDirectory = scene.getCode()
                + "/"
                + YEAR_FORMATTER.format(today)
                + "/"
                + MONTH_FORMATTER.format(today);

        String fileName = UUID.randomUUID()
                .toString()
                .replace("-", "")
                + "."
                + normalizedExtension;

        String relativePath = relativeDirectory + "/" + fileName;
        Path targetPath = resolvePathOrThrow(relativePath);
        Path parentDirectory = targetPath.getParent();
        Path temporaryPath = targetPath.resolveSibling(
                fileName + ".tmp"
        );

        try {
            Files.createDirectories(parentDirectory);

            try (var outputStream = Files.newOutputStream(
                    temporaryPath,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
            )) {
                inputStream.transferTo(outputStream);
            }

            moveAtomically(temporaryPath, targetPath);
            return relativePath;
        } catch (IOException exception) {
            deleteQuietly(temporaryPath);
            throw new BizException(
                    50000,
                    "媒体文件保存失败，请稍后重试",
                    exception
            );
        }
    }

    @Override
    public void delete(String relativePath) {
        Optional<Path> optionalPath = resolve(relativePath);

        if (optionalPath.isEmpty()) {
            return;
        }

        try {
            Files.deleteIfExists(optionalPath.get());
        } catch (IOException exception) {
            throw new BizException(
                    50000,
                    "媒体文件删除失败，请稍后重试",
                    exception
            );
        }
    }

    @Override
    public Optional<Path> resolve(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalizedRelativePath = relativePath
                .replace('\\', '/')
                .replaceFirst("^/+", "");

        Path candidate = rootPath
                .resolve(normalizedRelativePath)
                .normalize();

        if (!candidate.startsWith(rootPath)) {
            return Optional.empty();
        }

        return Optional.of(candidate);
    }

    private Path resolvePathOrThrow(String relativePath) {
        return resolve(relativePath).orElseThrow(() ->
                new BizException(40000, "媒体文件路径不合法")
        );
    }

    private String normalizeExtension(String extension) {
        if (extension == null || extension.trim().isEmpty()) {
            throw new BizException(40000, "文件扩展名不能为空");
        }

        String normalizedExtension = extension
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceFirst("^\\.", "");

        if (!normalizedExtension.matches("[a-z0-9]{1,10}")) {
            throw new BizException(40000, "文件扩展名不合法");
        }

        return normalizedExtension;
    }

    /**
     * 优先使用原子移动，不支持时退化为普通移动
     */
    private void moveAtomically(Path source, Path target) throws IOException {
        try {
            Files.move(
                    source,
                    target,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(
                    source,
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // 临时文件清理失败不影响原始业务异常
        }
    }
}


