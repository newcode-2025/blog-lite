package com.example.bloglite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * 本地上传静态资源访问配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    public WebMvcConfig(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String publicPath = normalizePublicPath(
                uploadProperties.getPublicPath()
        );

        Path rootPath = Paths.get(uploadProperties.getLocalDir())
                .toAbsolutePath()
                .normalize();

        String resourceLocation = rootPath.toUri().toString();

        if (!resourceLocation.endsWith("/")) {
            resourceLocation = resourceLocation + "/";
        }

        registry.addResourceHandler(publicPath + "/**")
                .addResourceLocations(resourceLocation)
                .setCacheControl(
                        CacheControl.maxAge(Duration.ofDays(365))
                                .cachePublic()
                );
    }

    private String normalizePublicPath(String publicPath) {
        String normalizedPath = publicPath.trim()
                .replace('\\', '/');

        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }

        while (normalizedPath.endsWith("/")
                && normalizedPath.length() > 1) {
            normalizedPath = normalizedPath.substring(
                    0,
                    normalizedPath.length() - 1
            );
        }

        return normalizedPath;
    }
}
