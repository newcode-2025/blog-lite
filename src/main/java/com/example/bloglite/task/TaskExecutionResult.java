package com.example.bloglite.task;

/**
 * 定时任务执行结果
 */
public record TaskExecutionResult(
        boolean success,
        String message
) {

    public static TaskExecutionResult success(String message) {
        return new TaskExecutionResult(true, message);
    }

    public static TaskExecutionResult failure(String message) {
        return new TaskExecutionResult(false, message);
    }
}