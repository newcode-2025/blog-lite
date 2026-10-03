package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.MailTestRequest;
import com.example.bloglite.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 后台邮件接口
 */
@RestController
@RequestMapping("/api/v1/admin/mail")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMailController {

    private final EmailService emailService;

    public AdminMailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * 发送邮件配置测试
     */
    @PostMapping("/test")
    public ApiResponse<Void> sendTestMail(
            @Valid @RequestBody MailTestRequest request
    ) {
        String subject = request.getSubject() == null
                || request.getSubject().isBlank()
                ? "blog-lite 邮件测试"
                : request.getSubject().trim();

        emailService.sendHtml(
                request.getTo(),
                subject,
                "test.html",
                Map.of(
                        "sentAt",
                        LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm:ss"
                                )
                        )
                )
        );
        return ApiResponse.success(null);
    }
}