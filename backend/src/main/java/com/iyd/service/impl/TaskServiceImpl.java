package com.iyd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iyd.common.R;
import com.iyd.dto.TaskDTO;
import com.iyd.entity.DailyCheckin;
import com.iyd.entity.StudyTask;
import com.iyd.mapper.DailyCheckinMapper;
import com.iyd.mapper.StudyTaskMapper;
import com.iyd.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final StudyTaskMapper taskMapper;
    private final DailyCheckinMapper checkinMapper;

    @Override
    public R<?> createTask(Long userId, TaskDTO dto) {
        StudyTask task = new StudyTask();
        task.setUserId(userId);
        task.setTaskContent(dto.getTaskContent());
        task.setEstimatedMinutes(dto.getEstimatedMinutes());
        if (dto.getDeadline() != null) {
            task.setDeadline(LocalDateTime.parse(dto.getDeadline(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        task.setStatus(0);
        taskMapper.insert(task);
        return R.ok(task);
    }

    @Override
    public R<?> updateTask(Long taskId, TaskDTO dto) {
        StudyTask task = taskMapper.selectById(taskId);
        if (task == null) return R.fail("任务不存在");
        if (dto.getTaskContent() != null) task.setTaskContent(dto.getTaskContent());
        if (dto.getEstimatedMinutes() != null) task.setEstimatedMinutes(dto.getEstimatedMinutes());
        if (dto.getDeadline() != null) {
            task.setDeadline(LocalDateTime.parse(dto.getDeadline(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        taskMapper.updateById(task);
        return R.ok();
    }

    @Override
    public R<?> deleteTask(Long taskId) {
        taskMapper.deleteById(taskId);
        return R.ok();
    }

    @Override
    public R<?> checkin(Long taskId) {
        StudyTask task = taskMapper.selectById(taskId);
        if (task == null) return R.fail("任务不存在");
        task.setStatus(1);
        task.setCheckinTime(LocalDateTime.now());
        taskMapper.updateById(task);

        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LambdaQueryWrapper<DailyCheckin> qw = new LambdaQueryWrapper<>();
        qw.eq(DailyCheckin::getUserId, task.getUserId()).ge(DailyCheckin::getCheckinDate, todayStart);
        List<DailyCheckin> list = checkinMapper.selectList(qw);
        DailyCheckin checkin = list.isEmpty() ? null : list.get(0);
        if (checkin == null) {
            checkin = new DailyCheckin();
            checkin.setUserId(task.getUserId());
            checkin.setCheckinDate(todayStart);
            checkin.setTaskCount(1);
            checkin.setStudyMinutes(task.getEstimatedMinutes() != null ? task.getEstimatedMinutes() : 0);
            checkinMapper.insert(checkin);
        } else {
            checkin.setTaskCount(checkin.getTaskCount() + 1);
            checkin.setStudyMinutes(checkin.getStudyMinutes() + (task.getEstimatedMinutes() != null ? task.getEstimatedMinutes() : 0));
            checkinMapper.updateById(checkin);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("msg", "打卡成功！今日已打卡");
        return R.ok(result);
    }

    @Override
    public R<?> getTaskList(Long userId) {
        LambdaQueryWrapper<StudyTask> qw = new LambdaQueryWrapper<>();
        qw.eq(StudyTask::getUserId, userId).orderByAsc(StudyTask::getStatus).orderByDesc(StudyTask::getCreateTime);
        return R.ok(taskMapper.selectList(qw));
    }

    @Override
    public R<?> getMonthlySummary(Long userId, String month) {
        LocalDateTime start = LocalDateTime.parse(month + "-01T00:00:00");
        LocalDateTime end = start.plusMonths(1).minusDays(1);

        LambdaQueryWrapper<DailyCheckin> qw = new LambdaQueryWrapper<>();
        qw.eq(DailyCheckin::getUserId, userId)
                .ge(DailyCheckin::getCheckinDate, start)
                .le(DailyCheckin::getCheckinDate, end);

        List<DailyCheckin> records = checkinMapper.selectList(qw);
        int totalMinutes = 0, totalTasks = 0, checkinDays = records.size();
        for (DailyCheckin r : records) {
            totalMinutes += r.getStudyMinutes();
            totalTasks += r.getTaskCount();
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("month", month);
        summary.put("checkinDays", checkinDays);
        summary.put("totalMinutes", totalMinutes);
        summary.put("totalTasks", totalTasks);
        summary.put("avgDailyMinutes", checkinDays > 0 ? totalMinutes / checkinDays : 0);
        return R.ok(summary);
    }
}
