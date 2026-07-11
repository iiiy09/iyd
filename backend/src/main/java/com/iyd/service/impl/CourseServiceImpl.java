package com.iyd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iyd.common.R;
import com.iyd.entity.CourseInfo;
import com.iyd.entity.CourseRecord;
import com.iyd.mapper.CourseInfoMapper;
import com.iyd.mapper.CourseRecordMapper;
import com.iyd.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseInfoMapper courseMapper;
    private final CourseRecordMapper recordMapper;

    @Override
    public R<?> getCourseList(String stage, String grade, String subject, Integer page, Integer size) {
        Page<CourseInfo> p = new Page<>(page != null ? page : 1, size != null ? size : 12);
        LambdaQueryWrapper<CourseInfo> qw = new LambdaQueryWrapper<>();
        if (stage != null && !stage.isEmpty()) qw.eq(CourseInfo::getStage, stage);
        if (grade != null && !grade.isEmpty()) qw.eq(CourseInfo::getGrade, grade);
        if (subject != null && !subject.isEmpty()) qw.eq(CourseInfo::getSubject, subject);
        qw.orderByDesc(CourseInfo::getPlayCount).orderByDesc(CourseInfo::getCreateTime);
        return R.ok(courseMapper.selectPage(p, qw));
    }

    @Override
    public R<?> getCourseDetail(Long courseId) {
        CourseInfo course = courseMapper.selectById(courseId);
        if (course != null) {
            course.setPlayCount(course.getPlayCount() + 1);
            courseMapper.updateById(course);
        }
        return R.ok(course);
    }

    @Override
    public R<?> recordProgress(Long userId, Long courseId, Integer duration) {
        LambdaQueryWrapper<CourseRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(CourseRecord::getUserId, userId).eq(CourseRecord::getCourseId, courseId);
        CourseRecord record = recordMapper.selectOne(qw);
        if (record == null) {
            record = new CourseRecord();
            record.setUserId(userId);
            record.setCourseId(courseId);
            CourseInfo course = courseMapper.selectById(courseId);
            record.setTotalDuration(course != null ? course.getDuration() : 0);
        }
        record.setWatchedDuration(record.getWatchedDuration() + duration);
        record.setLastWatchTime(LocalDateTime.now());
        if (record.getTotalDuration() > 0 && record.getWatchedDuration() >= record.getTotalDuration()) {
            record.setStatus(1);
        }
        if (record.getId() == null) recordMapper.insert(record); else recordMapper.updateById(record);
        return R.ok();
    }

    @Override
    public R<?> getMyCourses(Long userId) {
        LambdaQueryWrapper<CourseRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(CourseRecord::getUserId, userId).orderByDesc(CourseRecord::getLastWatchTime);
        List<CourseRecord> records = recordMapper.selectList(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CourseRecord r : records) {
            CourseInfo course = courseMapper.selectById(r.getCourseId());
            if (course != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("record", r);
                item.put("course", course);
                item.put("progress", r.getTotalDuration() > 0 ? (r.getWatchedDuration() * 100 / r.getTotalDuration()) : 0);
                result.add(item);
            }
        }
        return R.ok(result);
    }
}
