package com.iyd.service;

import com.iyd.common.R;

public interface TaskService {
    R<?> listTasks(Long userId);
    R<?> addTask(Long userId, String taskContent, Integer estimatedMinutes);
    R<?> checkin(Long userId, Long taskId);
    R<?> deleteTask(Long userId, Long taskId);
}