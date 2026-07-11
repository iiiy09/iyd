package com.iyd.service;

import com.iyd.common.R;
import com.iyd.dto.TaskDTO;

public interface TaskService {
    R<?> createTask(Long userId, TaskDTO dto);
    R<?> updateTask(Long taskId, TaskDTO dto);
    R<?> deleteTask(Long taskId);
    R<?> checkin(Long taskId);
    R<?> getTaskList(Long userId);
    R<?> getMonthlySummary(Long userId, String month);
}
