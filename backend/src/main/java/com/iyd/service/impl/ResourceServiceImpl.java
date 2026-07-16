package com.iyd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iyd.common.R;
import com.iyd.dto.ResourceQueryDTO;
import com.iyd.entity.StudyResource;
import com.iyd.entity.UserCollection;
import com.iyd.mapper.StudyResourceMapper;
import com.iyd.mapper.UserCollectionMapper;
import com.iyd.service.DeepSeekService;
import com.iyd.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final StudyResourceMapper resourceMapper;
    private final UserCollectionMapper collectionMapper;
    private final DeepSeekService deepSeekService;

    @Override
    public R<?> searchResources(ResourceQueryDTO dto) {
        Page<StudyResource> p = new Page<>(dto.getPage() != null ? dto.getPage() : 1, dto.getSize() != null ? dto.getSize() : 12);
        LambdaQueryWrapper<StudyResource> qw = new LambdaQueryWrapper<>();
        qw.eq(StudyResource::getAuditStatus, 1);
        if (dto.getStage() != null && !dto.getStage().isEmpty()) qw.eq(StudyResource::getStage, dto.getStage());
        if (dto.getGrade() != null && !dto.getGrade().isEmpty()) qw.eq(StudyResource::getGrade, dto.getGrade());
        if (dto.getSubject() != null && !dto.getSubject().isEmpty()) qw.eq(StudyResource::getSubject, dto.getSubject());
        if (dto.getResourceType() != null && !dto.getResourceType().isEmpty()) qw.eq(StudyResource::getResourceType, dto.getResourceType());
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            qw.like(StudyResource::getResourceName, dto.getKeyword());
        }
        qw.orderByDesc(StudyResource::getDownloadCount).orderByDesc(StudyResource::getCreateTime);

        Page<StudyResource> pageResult = resourceMapper.selectPage(p, qw);

        if (pageResult.getRecords() == null || pageResult.getRecords().isEmpty()) {
            return searchWithAI(dto);
        }

        return R.ok(pageResult);
    }

    private R<?> searchWithAI(ResourceQueryDTO dto) {
        StringBuilder searchInfo = new StringBuilder("User search conditions:\n");
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) 
            searchInfo.append("- Keyword: ").append(dto.getKeyword()).append("\n");
        if (dto.getSubject() != null && !dto.getSubject().isEmpty()) 
            searchInfo.append("- Subject: ").append(dto.getSubject()).append("\n");
        if (dto.getStage() != null && !dto.getStage().isEmpty()) 
            searchInfo.append("- Stage: ").append(dto.getStage()).append("\n");

        String prompt = "You are a learning resource recommendation assistant. "
                     + "Based on the user's search criteria, recommend 3-5 relevant study resources. "
                     + "For each resource provide: name, type (courseware/exam paper/review outline/answer key), "
                     + "and a brief description. Return in Chinese.";
        
        String aiRecommendation = deepSeekService.chat(prompt, searchInfo.toString());

        List<Map<String, Object>> aiResources = new ArrayList<>();
        Map<String, Object> resource = new HashMap<>();
        resource.put("resourceName", "AI Recommended: " + (dto.getKeyword() != null ? dto.getKeyword() : "Study Materials"));
        resource.put("resourceType", "AI Recommended");
        resource.put("aiGenerated", true);
        resource.put("description", aiRecommendation);
        aiResources.add(resource);

        Map<String, Object> result = new HashMap<>();
        result.put("records", aiResources);
        result.put("total", aiResources.size());
        result.put("aiRecommended", true);
        result.put("aiSuggestion", aiRecommendation);
        return R.ok(result);
    }

    @Override
    public R<?> getResourceDetail(Long resourceId) {
        StudyResource resource = resourceMapper.selectById(resourceId);
        if (resource != null) {
            resource.setDownloadCount(resource.getDownloadCount() + 1);
            resourceMapper.updateById(resource);
        }
        return R.ok(resource);
    }

    @Override
    public R<?> downloadResource(Long resourceId) {
        StudyResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) return R.fail("File not found");
        resource.setDownloadCount(resource.getDownloadCount() + 1);
        resourceMapper.updateById(resource);
        return R.ok(resource.getFileUrl());
    }

    @Override
    public R<?> collectResource(Long userId, Long resourceId) {
        LambdaQueryWrapper<UserCollection> qw = new LambdaQueryWrapper<>();
        qw.eq(UserCollection::getUserId, userId).eq(UserCollection::getResourceId, resourceId).eq(UserCollection::getCollectionType, 1);
        UserCollection exist = collectionMapper.selectOne(qw);
        if (exist != null) {
            collectionMapper.deleteById(exist);
            Map<String, Object> m = new HashMap<>(); m.put("collected", false);
            return R.ok(m);
        }
        UserCollection collection = new UserCollection();
        collection.setUserId(userId);
        collection.setResourceId(resourceId);
        collection.setCollectionType(1);
        collectionMapper.insert(collection);
        Map<String, Object> m = new HashMap<>(); m.put("collected", true);
        return R.ok(m);
    }

    @Override
    public R<?> getMyCollections(Long userId) {
        LambdaQueryWrapper<UserCollection> qw = new LambdaQueryWrapper<>();
        qw.eq(UserCollection::getUserId, userId).orderByDesc(UserCollection::getCreateTime);
        List<UserCollection> collections = collectionMapper.selectList(qw);
        List<StudyResource> resources = new ArrayList<>();
        for (UserCollection c : collections) {
            StudyResource r = resourceMapper.selectById(c.getResourceId());
            if (r != null) resources.add(r);
        }
        return R.ok(resources);
    }
}