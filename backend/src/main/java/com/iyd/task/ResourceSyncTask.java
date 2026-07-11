package com.iyd.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iyd.entity.ResourceSyncLog;
import com.iyd.entity.StudyResource;
import com.iyd.mapper.ResourceSyncLogMapper;
import com.iyd.mapper.StudyResourceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ResourceSyncTask {

    @Autowired
    private StudyResourceMapper resourceMapper;
    @Autowired
    private ResourceSyncLogMapper syncLogMapper;
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(cron = "0 0 3 * * ?")
    public void syncNewResources() {
        log.info("=== 开始每日学习资料自动同步 ===");
        String batchNo = "SYNC-" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
        List<StudyResource> newResources = new ArrayList<>();
        int newCount = 0;
        try {
            List<Map<String, String>> scrapedData = scrapeExternalResources();
            for (Map<String, String> item : scrapedData) {
                LambdaQueryWrapper<StudyResource> qw = new LambdaQueryWrapper<>();
                qw.eq(StudyResource::getResourceName, item.get("name"));
                if (resourceMapper.selectCount(qw) > 0) continue;
                StudyResource resource = new StudyResource();
                resource.setResourceName(item.get("name"));
                resource.setStage(item.get("stage"));
                resource.setGrade(item.get("grade"));
                resource.setSubject(item.get("subject"));
                resource.setResourceType(item.get("type"));
                resource.setFileUrl(item.get("url"));
                resource.setSource("sync");
                resource.setAuditStatus(0);
                resourceMapper.insert(resource);
                newResources.add(resource);
                newCount++;
            }
            ResourceSyncLog logEntry = new ResourceSyncLog();
            logEntry.setBatchNo(batchNo);
            logEntry.setNewResourceCount(newCount);
            logEntry.setOperationType("auto");
            StringBuilder ids = new StringBuilder();
            for (StudyResource r : newResources) {
                if (ids.length() > 0) ids.append(",");
                ids.append(r.getId());
            }
            logEntry.setResourceIds(ids.toString());
            syncLogMapper.insert(logEntry);
            if (newCount > 0 && redisTemplate != null) {
                redisTemplate.opsForValue().set("resource:new:alert", "true", 7, TimeUnit.DAYS);
                redisTemplate.opsForValue().set("resource:new:count", newCount, 7, TimeUnit.DAYS);
                redisTemplate.opsForValue().set("resource:new:time", DateUtil.now(), 7, TimeUnit.DAYS);
                log.info("同步完成，新增{}条资料，已设置管理员提醒", newCount);
            } else {
                log.info("本次未发现新增资料");
            }
        } catch (Exception e) {
            log.error("资料同步失败: {}", e.getMessage(), e);
        }
    }

    private List<Map<String, String>> scrapeExternalResources() {
        List<Map<String, String>> resources = new ArrayList<>();
        String[][] mockData = {
                {"2024中考数学真题汇编", "初中", "九年级", "数学", "真题"},
                {"高中物理必修一课件", "高中", "高一", "物理", "课件"},
                {"大学英语四级词汇表", "大学", "大一", "英语", "提纲"},
                {"小学语文古诗大全", "小学", "六年级", "语文", "复习提纲"},
        };
        for (String[] data : mockData) {
            Map<String, String> item = new HashMap<>();
            item.put("name", data[0]);
            item.put("stage", data[1]);
            item.put("grade", data[2]);
            item.put("subject", data[3]);
            item.put("type", data[4]);
            item.put("url", "/oss/sync/" + data[0] + ".pdf");
            resources.add(item);
        }
        return resources;
    }
}
