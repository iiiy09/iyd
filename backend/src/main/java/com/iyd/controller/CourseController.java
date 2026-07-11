package com.iyd.controller;

import com.iyd.common.R;
import com.iyd.config.JwtUtil;
import com.iyd.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final JwtUtil jwtUtil;

    @GetMapping("/list")
    public R<?> getCourseList(@RequestParam(required = false) String stage,
                               @RequestParam(required = false) String grade,
                               @RequestParam(required = false) String subject,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "12") Integer size) {
        return courseService.getCourseList(stage, grade, subject, page, size);
    }

    @GetMapping("/{courseId}")
    public R<?> getCourseDetail(@PathVariable Long courseId) {
        return courseService.getCourseDetail(courseId);
    }

    @PostMapping("/{courseId}/progress")
    public R<?> recordProgress(@RequestHeader("Authorization") String token,
                                @PathVariable Long courseId,
                                @RequestParam Integer duration) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return courseService.recordProgress(userId, courseId, duration);
    }

    @GetMapping("/my")
    public R<?> getMyCourses(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return courseService.getMyCourses(userId);
    }
}
