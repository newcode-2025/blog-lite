package com.example.bloglite.util;

import com.google.gson.Gson;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志参数脱敏与截断工具
 */
public final class LogSanitizer {

    private static final int MAX_LOG_LENGTH = 4000;

    private static final Gson GSON = new Gson();

    private static final Pattern SENSITIVE_FIELD_PATTERN = Pattern.compile(
            "(?i)(\"[^\"]*(?:password|token|secret|authorization|cookie)[^\"]*\"\\s*:\\s*)\"(?:\\\\.|[^\"\\\\])*\""
    );

    private static final Pattern SENSITIVE_QUERY_PATTERN = Pattern.compile(
            "(?i)(^|[&?])((?:password|token|secret|authorization|cookie)[^=&\\s]*)=([^&\\s]*)"
    );

    private LogSanitizer() {
    }

    /**
     * 将控制器参数转换为脱敏、截断后的日志文本
     */
    public static String sanitizeArguments(Object[] arguments) {
        if (arguments == null || arguments.length == 0) {
            return "";
        }

        List<String> parts = new ArrayList<>();

        for (Object argument : arguments) {
            if (argument == null
                    || argument instanceof ServletRequest
                    || argument instanceof ServletResponse
                    || argument instanceof BindingResult) {
                continue;
            }

            if (argument instanceof MultipartFile
                    || argument instanceof MultipartFile[]) {
                parts.add("[文件内容已省略]");
                continue;
            }

            try {
                parts.add(GSON.toJson(argument));
            } catch (RuntimeException exception) {
                parts.add("[参数序列化失败: "
                        + argument.getClass().getSimpleName() + "]");
            }
        }

        String json = parts.isEmpty() ? "" : String.join(", ", parts);
        return sanitizeText(json);
    }

    /**
     * 对普通文本进行敏感字段脱敏和长度截断
     */
    public static String sanitizeText(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        Matcher matcher = SENSITIVE_FIELD_PATTERN.matcher(text);
        String sanitized = matcher.replaceAll("$1\"***\"");
        sanitized = SENSITIVE_QUERY_PATTERN
                .matcher(sanitized)
                .replaceAll("$1$2=***");

        if (sanitized.length() <= MAX_LOG_LENGTH) {
            return sanitized;
        }

        return sanitized.substring(0, MAX_LOG_LENGTH) + "...[已截断]";
    }
}