package com.iyd.controller;

import com.iyd.common.R;
import com.iyd.dto.ResourceQueryDTO;
import com.iyd.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public R<?> getUserList(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "20") Integer size,
                             @RequestParam(required = false) String keyword) {
        return adminService.getUserList(page, size, keyword);
    }

    @PutMapping("/user/{userId}/status")
    public R<?> toggleUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        return adminService.toggleUserStatus(userId, status);
    }

    @PostMapping("/course/upload")
    public R<?> uploadCourse(@RequestParam String courseName,
                              @RequestParam String stage,
                              @RequestParam String grade,
                              @RequestParam String subject,
                              @RequestParam(required = false) String desc,
                              @RequestParam(required = false) MultipartFile video,
                              @RequestParam(required = false) MultipartFile cover) {
        return adminService.uploadCourse(courseName, stage, grade, subject, desc, video, cover);
    }

    @PostMapping("/resource/upload")
    public R<?> uploadResource(@RequestParam String name,
                                @RequestParam String stage,
                                @RequestParam String grade,
                                @RequestParam String subject,
                                @RequestParam String type,
                                @RequestParam(required = false) MultipartFile file) {
        return adminService.uploadResource(name, stage, grade, subject, type, file);
    }

    @DeleteMapping("/resource/{resourceId}")
    public R<?> deleteResource(@PathVariable Long resourceId) {
        return adminService.deleteResource(resourceId);
    }

    @DeleteMapping("/resources/batch")
    public R<?> batchDeleteResources(@RequestParam String ids) {
        return adminService.batchDeleteResources(ids);
    }

    @PostMapping("/resources")
    public R<?> getResources(@RequestBody ResourceQueryDTO dto) {
        return adminService.getResources(dto);
    }

    @GetMapping("/sync/logs")
    public R<?> getSyncLogs(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "20") Integer size) {
        return adminService.getSyncLogs(page, size);
    }

    @GetMapping("/alert/new-resources")
    public R<?> getNewResourceAlert() {
        return adminService.getNewResourceAlert();
    }

    @GetMapping("/statistics")
    public R<?> getStatistics() {
        return adminService.getStatistics();
    }

    @PutMapping("/resource/{resourceId}/audit")
    public R<?> auditResource(@PathVariable Long resourceId, @RequestParam Integer status) {
        return adminService.auditResource(resourceId, status);
    }
}
