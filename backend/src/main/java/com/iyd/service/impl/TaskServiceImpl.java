package com.iyd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iyd.common.R;
import com.iyd.entity.StudyTask;
import com.iyd.mapper.StudyTaskMapper;
import com.iyd.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final StudyTaskMapper taskMapper;

    @Override
    public R<?> listTasks(Long userId) {
        LambdaQueryWrapper<StudyTask> qw = new LambdaQueryWrapper<>();
        qw.eq(StudyTask::getUserId, userId).orderByDesc(StudyTask::getCreateTime);
        return R.ok(taskMapper.selectList(qw));
    }

    @Override
    public R<?> addTask(Long userId, String taskContent, Integer estimatedMinutes) {
        StudyTask task = new StudyTask();
        task.setUserId(userId);
        task.setTaskContent(taskContent);
        task.setEstimatedMinutes(estimatedMinutes != null ? estimatedMinutes : 30);
        task.setStatus(0);
        taskMapper.insert(task);
        return R.ok(task);
    }

    @Override
    public R<?> checkin(Long userId, Long taskId) {
        StudyTask task = taskMapper.selectById(taskId);
        if (task == null) return R.fail("任务不存在");
        if (!task.getUserId().equals(userId)) return R.fail("无权操作");
        task.setStatus(1);
        task.setCheckinTime(LocalDateTime.now());
        taskMapper.updateById(task);
        return R.ok(task);
    }

    @Override
    public R<?> deleteTask(Long userId, Long taskId) {
        StudyTask task = taskMapper.selectById(taskId);
        if (task == null) return R.fail("任务不存在");
        if (!task.getUserId().equals(userId)) return R.fail("无权操作");
        taskMapper.deleteById(taskId);
        return R.ok();
    }
}