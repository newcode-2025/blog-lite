package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 应用健康检查接口
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(
                Map.of(
                        "status", "UP",
                        "application", "blog-lite"
                )
        );
    }
}
