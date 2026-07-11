package com.iyd.service;

import com.iyd.common.R;
import com.iyd.dto.ResourceQueryDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {
    R<?> getUserList(Integer page, Integer size, String keyword);
    R<?> toggleUserStatus(Long userId, Integer status);
    R<?> uploadCourse(String courseName, String stage, String grade, String subject, String desc, MultipartFile video, MultipartFile cover);
    R<?> uploadResource(String name, String stage, String grade, String subject, String type, MultipartFile file);
    R<?> deleteResource(Long resourceId);
    R<?> batchDeleteResources(String ids);
    R<?> getResources(ResourceQueryDTO dto);
    R<?> getSyncLogs(Integer page, Integer size);
    R<?> getNewResourceAlert();
    R<?> getStatistics();
    R<?> auditResource(Long resourceId, Integer status);
}
