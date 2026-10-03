package com.example.bloglite.task;

/**
 * 可调度任务处理器
 * 所有定时任务必须先注册处理器，数据库不能直接指定任意类和方法。
 */
public interface ScheduledTaskHandler {

    /**
     * 白名单任务键
     */
    String getTaskKey();

    /**
     * 任务显示名称
     */
    String getTaskName();

    /**
     * 执行任务
     */
    TaskExecutionResult execute();
}