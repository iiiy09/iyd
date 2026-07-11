package com.iyd.service;

import com.iyd.common.R;

public interface CourseService {
    R<?> getCourseList(String stage, String grade, String subject, Integer page, Integer size);
    R<?> getCourseDetail(Long courseId);
    R<?> recordProgress(Long userId, Long courseId, Integer duration);
    R<?> getMyCourses(Long userId);
}
