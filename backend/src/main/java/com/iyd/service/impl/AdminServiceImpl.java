package com.iyd.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iyd.common.R;
import com.iyd.dto.ResourceQueryDTO;
import com.iyd.entity.*;
import com.iyd.mapper.*;
import com.iyd.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private StudyResourceMapper resourceMapper;
    @Autowired
    private ResourceSyncLogMapper syncLogMapper;
    @Autowired
    private QuestionErrorMapper errorMapper;
    @Autowired
    private ReciteRecordMapper reciteMapper;
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public R<?> getUserList(Integer page, Integer size, String keyword) {
        Page<SysUser> p = new Page<>(page != null ? page : 1, size != null ? size : 20);
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(SysUser::getPhone, keyword).or().like(SysUser::getNickname, keyword);
        }
        qw.orderByDesc(SysUser::getCreateTime);
        return R.ok(userMapper.selectPage(p, qw));
    }

    @Override
    public R<?> toggleUserStatus(Long userId, Integer status) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) return R.fail("用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
        return R.ok();
    }

    @Override
    public R<?> uploadResource(String name, String stage, String grade, String subject, String type, MultipartFile file) {
        StudyResource resource = new StudyResource();
        resource.setResourceName(name);
        resource.setStage(stage);
        resource.setGrade(grade);
        resource.setSubject(subject);
        resource.setResourceType(type);
        resource.setFileUrl("/oss/resource/" + name);
        resource.setSource("manual");
        resource.setAuditStatus(1);
        resourceMapper.insert(resource);
        return R.ok(resource);
    }

    @Override
    public R<?> deleteResource(Long resourceId) {
        resourceMapper.deleteById(resourceId);
        return R.ok();
    }

    @Override
    public R<?> batchDeleteResources(String ids) {
        String[] arr = ids.split(",");
        List<Long> idList = new ArrayList<>();
        for (String s : arr) idList.add(Long.parseLong(s.trim()));
        resourceMapper.deleteBatchIds(idList);
        return R.ok();
    }

    @Override
    public R<?> getResources(ResourceQueryDTO dto) {
        Page<StudyResource> p = new Page<>(dto.getPage() != null ? dto.getPage() : 1, dto.getSize() != null ? dto.getSize() : 20);
        LambdaQueryWrapper<StudyResource> qw = new LambdaQueryWrapper<>();
        if (dto.getStage() != null && !dto.getStage().isEmpty()) qw.eq(StudyResource::getStage, dto.getStage());
        if (dto.getGrade() != null && !dto.getGrade().isEmpty()) qw.eq(StudyResource::getGrade, dto.getGrade());
        if (dto.getSubject() != null && !dto.getSubject().isEmpty()) qw.eq(StudyResource::getSubject, dto.getSubject());
        if (dto.getResourceType() != null && !dto.getResourceType().isEmpty()) qw.eq(StudyResource::getResourceType, dto.getResourceType());
        if (dto.getAuditStatus() != null) qw.eq(StudyResource::getAuditStatus, dto.getAuditStatus());
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) qw.like(StudyResource::getResourceName, dto.getKeyword());
        qw.orderByDesc(StudyResource::getCreateTime);
        return R.ok(resourceMapper.selectPage(p, qw));
    }

    @Override
    public R<?> getSyncLogs(Integer page, Integer size) {
        Page<ResourceSyncLog> p = new Page<>(page != null ? page : 1, size != null ? size : 20);
        LambdaQueryWrapper<ResourceSyncLog> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(ResourceSyncLog::getCreateTime);
        return R.ok(syncLogMapper.selectPage(p, qw));
    }

    @Override
    public R<?> getNewResourceAlert() {
        if (redisTemplate == null) {
            Map<String, Object> m = new HashMap<>();
            m.put("hasNew", false);
            return R.ok(m);
        }
        Boolean hasNew = redisTemplate.hasKey("resource:new:alert");
        if (Boolean.TRUE.equals(hasNew)) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("hasNew", true);
            alert.put("count", redisTemplate.opsForValue().get("resource:new:count"));
            alert.put("time", redisTemplate.opsForValue().get("resource:new:time"));
            return R.ok(alert);
        }
        Map<String, Object> m = new HashMap<>();
        m.put("hasNew", false);
        return R.ok(m);
    }

    @Override
    public R<?> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalResources", resourceMapper.selectCount(null));
        stats.put("todayQuestions", errorMapper.selectCount(
                new LambdaQueryWrapper<QuestionError>().ge(QuestionError::getCreateTime, DateUtil.beginOfDay(new Date()))
        ));
        stats.put("todayRecites", reciteMapper.selectCount(
                new LambdaQueryWrapper<ReciteRecord>().ge(ReciteRecord::getCreateTime, DateUtil.beginOfDay(new Date()))
        ));
        return R.ok(stats);
    }

    @Override
    public R<?> auditResource(Long resourceId, Integer status) {
        StudyResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) return R.fail("资料不存在");
        resource.setAuditStatus(status);
        resourceMapper.updateById(resource);
        return R.ok();
    }
}