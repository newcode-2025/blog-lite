package com.example.bloglite.task;

import com.example.bloglite.exception.BizException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务处理器白名单注册表
 */
@Component
public class ScheduledTaskRegistry {

    private final Map<String, ScheduledTaskHandler> handlers;

    public ScheduledTaskRegistry(List<ScheduledTaskHandler> handlers) {
        Map<String, ScheduledTaskHandler> handlerMap = new LinkedHashMap<>();

        for (ScheduledTaskHandler handler : handlers) {
            String taskKey = handler.getTaskKey();

            if (taskKey == null || taskKey.isBlank()) {
                throw new IllegalStateException("定时任务键不能为空");
            }

            if (handlerMap.putIfAbsent(taskKey, handler) != null) {
                throw new IllegalStateException(
                        "重复的定时任务键：" + taskKey
                );
            }
        }

        this.handlers = Map.copyOf(handlerMap);
    }

    /**
     * 获取任务处理器，不存在时拒绝执行
     */
    public ScheduledTaskHandler requireHandler(String taskKey) {
        ScheduledTaskHandler handler = handlers.get(taskKey);

        if (handler == null) {
            throw new BizException(
                    40000,
                    "未注册的定时任务键：" + taskKey
            );
        }

        return handler;
    }

    /**
     * 查询全部白名单任务处理器
     */
    public List<ScheduledTaskHandler> listHandlers() {
        return List.copyOf(handlers.values());
    }
}